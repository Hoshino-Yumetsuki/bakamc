package cn.bakamc.proxy.event.mirai

import cn.bakamc.proxy.BakamcProxyInstance
import cn.bakamc.proxy.config.BotConfig.BOTS
import cn.bakamc.proxy.config.BotConfig.CONSOLE_COMMAND_EXECUTORS
import cn.bakamc.proxy.config.WhiteListConfigs.BIND_COMMAND
import cn.bakamc.proxy.config.WhiteListConfigs.FORCE_UPDATE_MEMBER_CARD
import cn.bakamc.proxy.config.WhiteListConfigs.RENAME_AFTER_BIND
import cn.bakamc.proxy.config.WhiteListConfigs.RENAME_EXP
import cn.bakamc.proxy.config.WhiteListConfigs.VERIFY_CODE_LENGTH
import cn.bakamc.proxy.config.WhiteListConfigs.VERIFY_GROUP
import cn.bakamc.proxy.feature.white_list.WhiteListManager
import cn.bakamc.proxy.services.PlayerServices
import com.velocitypowered.api.event.Subscribe
import me.dreamvoid.miraimc.velocity.event.group.member.MiraiMemberCardChangeEvent
import me.dreamvoid.miraimc.velocity.event.group.member.MiraiMemberLeaveEvent
import me.dreamvoid.miraimc.velocity.event.message.passive.MiraiGroupMessageEvent
import moe.forpleuvoir.nebula.common.ioLaunch
import moe.forpleuvoir.nebula.common.pick


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

    @Subscribe
    fun onGroupMessage(event: MiraiGroupMessageEvent) {
        if (event.groupID == VERIFY_GROUP && event.botID in BOTS) {
            val message = event.message
            when {
                message.startsWith(BIND_COMMAND) -> onBind(event)
                message.startsWith("updateCard") -> updateCard(event)
                message.startsWith("c/")         -> consoleCommand(event)
            }
        }
    }

    private fun consoleCommand(event: MiraiGroupMessageEvent) {
        if (event.senderID in CONSOLE_COMMAND_EXECUTORS) {
            val message = event.message
            val cmd = message.substring(1)
            val source = BakamcProxyInstance.server.consoleCommandSource
            BakamcProxyInstance.server.commandManager.executeImmediatelyAsync(source, cmd)
                .thenAccept { event.reply(it.pick("指令执行成功", "指令执行失败")) }
        }
    }

    private fun updateCard(event: MiraiGroupMessageEvent) {
        if (event.senderPermission == 0) return
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
        val message = event.message
        BakamcProxyInstance.logger.info("绑定指令$message")
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
    fun onQuitGroup(event: MiraiMemberLeaveEvent) {
        if (event.groupID == VERIFY_GROUP && event.botID in BOTS) {
            val id = event.member.id
            ioLaunch {
                WhiteListManager.onMemberQuit(id)
            }
        }
    }

}