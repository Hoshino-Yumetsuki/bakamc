package cn.bakamc.proxy.event.velocity

import cn.bakamc.proxy.messagechannel.MessageChannels
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent

object MessageChannelEventListener {

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        MessageChannels.onMessage(event.identifier, event.source, event.data)
    }

}