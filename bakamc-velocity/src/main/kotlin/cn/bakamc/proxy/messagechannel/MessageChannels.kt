package cn.bakamc.proxy.messagechannel

import cn.bakamc.common.messagechannel.Message
import cn.bakamc.common.messagechannel.MessageChannel.Companion.BOT_CMD_CHANNEL
import cn.bakamc.common.messagechannel.MessageData
import cn.bakamc.proxy.BakamcProxyInstance.logger
import cn.bakamc.proxy.BakamcProxyInstance.server
import cn.bakamc.proxy.config.Configs.SERVER_INFO
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.ChannelIdentifier
import com.velocitypowered.api.proxy.messages.ChannelMessageSource
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier

object MessageChannels {

    fun register(server: ProxyServer) {
        logger.info("注册PluginMessage...")
        channels.forEach { pluginMessage ->
            runCatching {
                server.channelRegistrar.register(pluginMessage.identifier)
            }.onFailure {
                logger.error("注册失败", it)
            }.onSuccess {
                logger.info("注册完成[${pluginMessage.channel.id}]")
            }
        }
    }

    val Message<*>.identifier: ChannelIdentifier
        get() {
            return MinecraftChannelIdentifier.create(channel.namespace, channel.name)
        }

    fun onMessage(identifier: ChannelIdentifier, source: ChannelMessageSource, message: ByteArray) {

        channels.find { it.channel.id == identifier.id }?.messageHandler?.invoke(source, MessageData.fromBytes(message))
    }

    val botCmdChannel: Message<ChannelMessageSource> = Message(BOT_CMD_CHANNEL) { source, message ->
        message.tryConsume(SERVER_INFO) {
            logger.info("收到指令:${message.data.toString(Charsets.UTF_8)}")
            server.commandManager.executeImmediatelyAsync(server.consoleCommandSource, message.data.toString(Charsets.UTF_8))
        }
    }

    private val channels = listOf(
        botCmdChannel
    )

}