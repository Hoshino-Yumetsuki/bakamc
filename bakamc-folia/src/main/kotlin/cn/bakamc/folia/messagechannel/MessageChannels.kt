package cn.bakamc.folia.messagechannel

import cn.bakamc.common.messagechannel.Message
import cn.bakamc.common.messagechannel.MessageChannel.Companion.BOT_CMD_CHANNEL
import cn.bakamc.common.messagechannel.MessageData
import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.config.Configs.SERVER_INFO
import cn.bakamc.folia.util.logger
import cn.bakamc.folia.util.server
import org.bukkit.Server
import org.bukkit.entity.Player

object MessageChannels {

    fun register(server: Server) {
        logger.info("注册PluginMessage...")
        channels.forEach { pluginMessage ->
            server.messenger.apply {
                runCatching {
                    registerOutgoingPluginChannel(BakaMCPlugin.instance, pluginMessage.channel.id)
                    registerIncomingPluginChannel(BakaMCPlugin.instance, pluginMessage.channel.id) { _, source, message ->
                        pluginMessage.messageHandler.invoke(source, MessageData.fromBytes(message))
                    }
                }.onFailure {
                    logger.error("注册失败", it)
                }.onSuccess {
                    logger.info("注册完成[${pluginMessage.channel.id}]")
                }
            }
        }
    }

    val botCmdChannel: Message<Player> = Message(BOT_CMD_CHANNEL) { _, message ->
        server.globalRegionScheduler.execute(BakaMCPlugin.instance) {
            message.tryConsume(SERVER_INFO) {
                logger.info("收到指令:${message.data.toString(Charsets.UTF_8)}")
                server.dispatchCommand(server.consoleSender, message.data.toString(Charsets.UTF_8))
            }
        }
    }

    private val channels = listOf(
        botCmdChannel
    )

}