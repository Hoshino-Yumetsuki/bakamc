package cn.bakamc.proxy.event.mirai

import cn.bakamc.common.ServerInfo
import cn.bakamc.common.messagechannel.MessageData
import cn.bakamc.common.messagechannel.ServerConsumer
import cn.bakamc.proxy.BakamcProxyInstance.logger
import cn.bakamc.proxy.BakamcProxyInstance.server
import cn.bakamc.proxy.config.BotConfig.BOTS
import cn.bakamc.proxy.config.BotConfig.CONSOLE_COMMAND_EXECUTORS
import cn.bakamc.proxy.config.BotConfig.GROUPS
import cn.bakamc.proxy.config.Configs.SERVER_INFO
import cn.bakamc.proxy.config.WhiteListConfigs.BIND_COMMAND
import cn.bakamc.proxy.config.WhiteListConfigs.FORCE_UPDATE_MEMBER_CARD
import cn.bakamc.proxy.config.WhiteListConfigs.RENAME_AFTER_BIND
import cn.bakamc.proxy.config.WhiteListConfigs.RENAME_EXP
import cn.bakamc.proxy.config.WhiteListConfigs.VERIFY_CODE_LENGTH
import cn.bakamc.proxy.config.WhiteListConfigs.VERIFY_GROUP
import cn.bakamc.proxy.feature.white_list.WhiteListManager
import cn.bakamc.proxy.messagechannel.MessageChannels.botCmdChannel
import cn.bakamc.proxy.messagechannel.MessageChannels.identifier
import cn.bakamc.proxy.services.PlayerServices
import com.velocitypowered.api.event.Subscribe
import me.dreamvoid.miraimc.velocity.event.group.member.MiraiMemberCardChangeEvent
import me.dreamvoid.miraimc.velocity.event.group.member.MiraiMemberLeaveEvent
import me.dreamvoid.miraimc.velocity.event.message.passive.MiraiGroupMessageEvent
import moe.forpleuvoir.nebula.common.ioLaunch


object GroupEventListener {

    @Subscribe
    fun onMemberNameChange(event: MiraiMemberCardChangeEvent) {
        if (!FORCE_UPDATE_MEMBER_CARD) return
        if (event.oldNick == event.newNick) return
        ioLaunch {
            val names = PlayerServices.getBindPlayers(event.memberID).map { it.name }
            names.reversed().let { playerNames ->
                if (event.group.botPermission >= 1) {
                    val nameCard = generateNameCard(playerNames)
                    if (event.member.nameCard != nameCard && nameCard.isNotEmpty()) {
                        event.member.nameCard = nameCard
                    }
                }
            }
        }
    }

    private val consoleCommandRegex = Regex("c(@([^/]+)/)(.*)")

    @Subscribe
    fun onGroupMessage(event: MiraiGroupMessageEvent) {
        if (event.groupID in GROUPS && event.botID in BOTS) {
            val message = event.message
            when {
                message.startsWith(BIND_COMMAND)     -> onBind(event)
                message.startsWith("updateCard")     -> updateCard(event)
                message.matches(consoleCommandRegex) -> consoleCommand(event)
            }
        }
    }

    private fun consoleCommand(event: MiraiGroupMessageEvent) {
        if (event.senderID in CONSOLE_COMMAND_EXECUTORS) {
            val message = event.message
            consoleCommandRegex.find(message)?.let { matchResult ->
                val serverId = matchResult.groupValues[2]
                val cmd = matchResult.groupValues[3]
                logger.info("收到指令:[cmd:${cmd},server_id:${serverId}]")
                if (serverId == SERVER_INFO.serverName) {
                    server.commandManager.executeImmediatelyAsync(server.consoleCommandSource, cmd)
                } else {
                    //由子服执行
                    server.allServers.forEach {
                        it.sendPluginMessage(
                            botCmdChannel.identifier,
                            MessageData(ServerConsumer(ServerInfo(serverId)), cmd.toByteArray(Charsets.UTF_8)).toBytes()
                        )
                    }
                }
            }
        }
    }

    private fun updateCard(event: MiraiGroupMessageEvent) {
        if (event.senderPermission == 0 || event.groupID != VERIFY_GROUP) return
        val message = event.message
        val id = message.substring("updateCard".length + 1)
        runCatching {
            event.group.members.find { it.id == id.toLong() }!!
        }.onFailure {
            if (it is NullPointerException) {
                event.reply("未找到该成员($id)")
            } else {
                event.reply(it.message)
            }
        }.getOrThrow().let { member ->
            ioLaunch {
                PlayerServices.getBindPlayers(member.id).map { it.name }.reversed().let { playerNames ->
                    if (event.botPermission >= 1) {
                        val nameCard = generateNameCard(playerNames)
                        if (nameCard.isNotEmpty()) {
                            member.nameCard = nameCard
                        }
                    }
                }
            }

        }
    }

    private fun generateNameCard(playerNames: List<String>): String {
        var nameCard = RENAME_EXP
        playerNames.forEachIndexed { index, playerName ->
            val old = nameCard
            nameCard = nameCard.replace("#{bind[$index]}", playerName)
            if (index == playerNames.lastIndex && old == nameCard) {
                nameCard = nameCard.replace("#{bind[last]}", playerName)
            }
        }
        nameCard = nameCard.replace(Regex("""#\{bind\[((\d+)|last)]}"""), "")
        return if (playerNames.isNotEmpty()) nameCard else ""
    }

    private fun onBind(event: MiraiGroupMessageEvent) {
        if (event.groupID != VERIFY_GROUP) return
        val message = event.message
        logger.info("绑定指令$message")
        val code = message.substring(BIND_COMMAND.length + 1)
        @Suppress("RegExpSimplifiable")
        if (code.matches("^[A-Z0-9]{${VERIFY_CODE_LENGTH}}\$".toRegex())) {
            ioLaunch {
                val (msg, names) = WhiteListManager.bind(code, event.senderID)
                if (RENAME_AFTER_BIND) {
                    names?.reversed()?.let { playerNames ->
                        event.group.members.find { it.id == event.senderID }?.let { member ->
                            if (event.botPermission >= 1) {
                                val nameCard = generateNameCard(playerNames)
                                if (nameCard.isNotEmpty()) {
                                    member.nameCard = nameCard
                                }
                            }
                        }
                    }
                }
                event.reply(msg)
            }
        }
    }


    @Subscribe
    fun onQuitGroup(event: MiraiMemberLeaveEvent.Quit) {
        if (event.groupID == VERIFY_GROUP && event.botID in BOTS) {
            val id = event.targetID
            ioLaunch {
                WhiteListManager.onMemberQuit(id)
                logger.info("玩家[${event.memberNick}(${event.targetID})]退出QQ群,将移除白名单")
            }
        }
    }

    @Subscribe
    fun onQuitGroup(event: MiraiMemberLeaveEvent.Kick) {
        if (event.groupID == VERIFY_GROUP && event.botID in BOTS) {
            val id = event.targetID
            ioLaunch {
                WhiteListManager.onMemberQuit(id)
                logger.info("玩家[${event.memberNick}(${event.targetID})]被踢出QQ群,将移除白名单")
            }
        }
    }

}