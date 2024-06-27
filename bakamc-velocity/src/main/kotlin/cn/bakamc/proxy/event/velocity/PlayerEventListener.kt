package cn.bakamc.proxy.event.velocity

import cn.bakamc.proxy.BakamcProxyInstance
import cn.bakamc.proxy.config.Configs.Misc.SERVER_NAME_MAPPING
import cn.bakamc.proxy.feature.ip_restrict.IpRestrictor
import cn.bakamc.proxy.feature.white_list.WhiteListManager
import cn.bakamc.proxy.util.Utils.asComponent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import kotlin.time.Duration.Companion.milliseconds
import cn.bakamc.proxy.feature.ip_restrict.IpRestrictConfigs.ENABLED as ipRestrictEnabled
import cn.bakamc.proxy.feature.white_list.WhiteListConfigs.ENABLED as whiteListEnabled

object PlayerEventListener {

    @Subscribe
    fun onPlayerJoin(event: ServerPreConnectEvent) {
        if (whiteListEnabled) {
            WhiteListManager.onPlayerJoin(event)
        }

        if (ipRestrictEnabled) {
            IpRestrictor.onPlayerConnect(event.player)
        }
    }

    @Subscribe
    fun onServerConnectedEvent(event: ServerConnectedEvent) {
        val player = event.player
        val currentServer = event.server

        BakamcProxyInstance.runDelayed(500.milliseconds) {
            BakamcProxyInstance.server.allServers.forEach { server ->
                server.sendMessage(
                    player.asComponent()
                        .append(Component.text(" 已加入 ").applyFallbackStyle(TextColor.color(0x4EE983)))
                        .append(currentServer.asComponent(SERVER_NAME_MAPPING))
                )
            }
        }
    }

    @Subscribe
    fun onPlayerQuit(event: DisconnectEvent) {
//        if (ipRestrictEnabled) {
//            IpRestrictor.onPlayerDisconnect(event.player)
//        }
        val player = event.player
        BakamcProxyInstance.runTask {
            BakamcProxyInstance.server.allServers.forEach { server ->
                server.sendMessage(
                    player.asComponent()
                        .append(Component.text(" 已退出服务器").applyFallbackStyle(TextColor.color(0xE9685C)))
                )
            }
        }
    }

}