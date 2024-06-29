package cn.bakamc.proxy.util

import cn.bakamc.proxy.config.MiscConfig.PLAYER_INFO
import cn.bakamc.proxy.config.MiscConfig.SERVER_INFO
import cn.bakamc.proxy.config.MiscConfig.SERVER_NAME_MAPPING
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor

object Utils {

    fun Player.asComponent(): Component {
        val hover = HoverEvent.showText(
            Component.text(username).appendNewline()
                .append(Component.text(uniqueId.toString()))
                .append(Component.text("点击传送传送到Ta身边"))
        )
        val click = ClickEvent.suggestCommand("/tpa $username")
        return Component.text(username).applyFallbackStyle(hover, click, TextColor.color(0x01E9E8))
    }

    fun RegisteredServer.asComponent(mapping: Map<String, String>): Component {
        val serverID = this.serverInfo.name
        val serverName = mapping.getOrDefault(serverID, serverID)
        val hover = HoverEvent.showText(
            Component.text("服务器ID:$serverID").appendNewline()
                .append(Component.text("服务器名:$serverName")).appendNewline()
                .append(Component.text("点击传送至此服务器"))
        )
        val click = ClickEvent.suggestCommand("/server $serverID")
        return Component.text(serverName).applyFallbackStyle(hover, click, TextColor.color(0xE76E42))
    }

    fun replace(bakaText: String, server: RegisteredServer, player: Player): String {
        val serverInfo = server.serverInfo
        return bakaText.replace("#{server_info}", SERVER_INFO)
            .replace("#{player_info}", PLAYER_INFO)
            .replace("#{server_id}", serverInfo.name)
            .replace("#{server_name}", SERVER_NAME_MAPPING[serverInfo.name] ?: serverInfo.name)
            .replace("#{player_uuid}", player.uniqueId.toString())
            .replace("#{player_name}", player.username)
    }


}

