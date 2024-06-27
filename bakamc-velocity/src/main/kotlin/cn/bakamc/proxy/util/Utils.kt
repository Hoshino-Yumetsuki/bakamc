package cn.bakamc.proxy.util

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.HSVColor
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
        val click = ClickEvent.suggestCommand("/tpa " + username)
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

    fun HSVColor.gradient(end: HSVColor, sliceSize: Int): List<HSVColor> {
        return gradientHSVColor(this, end, sliceSize)
    }

    fun Color.gradient(end: Color, sliceSize: Int): List<Color> {
        return gradientColor(this, end, sliceSize)
    }

    fun gradientHSVColor(start: HSVColor, end: HSVColor, sliceSize: Int): List<HSVColor> {
        check(sliceSize > 0) { "slice must be greater than 0" }
        if (sliceSize == 1) return listOf(HSVColor(start.hue, start.saturation, start.value, start.value, false))
        val hueSlice = (end.hue - start.hue) / sliceSize
        val saturationSlice = (end.saturation - start.saturation) / sliceSize
        val valueSlice = (end.value - start.value) / sliceSize
        val alphaSlice = (end.alpha - start.alpha) / sliceSize
        return buildList {
            repeat(sliceSize) {
                add(HSVColor(start.hue, start.saturation, start.value, start.value, false).also { color ->
                    color.hue = start.hue + (hueSlice * it)
                    color.saturation = start.saturation + (saturationSlice * it)
                    color.value = start.value + (valueSlice * it)
                    color.alpha = start.alpha + (alphaSlice * it)
                })
            }
        }
    }

    fun gradientColor(start: Color, end: Color, sliceSize: Int): List<Color> {
        check(sliceSize > 0) { "slice must be greater than 0" }
        if (sliceSize == 1) return listOf(Color(start.red, start.green, start.blue, start.alpha, false))
        val redSlice = (end.redF - start.redF) / sliceSize
        val greenSlice = (end.greenF - start.greenF) / sliceSize
        val blueSlice = (end.blueF - start.blueF) / sliceSize
        val alphaSlice = (end.alpha - start.alpha) / sliceSize
        return buildList {
            repeat(sliceSize) {
                add(Color(start.red, start.green, start.blue, start.alpha, false).also { color ->
                    color.redF = start.redF + (redSlice * it)
                    color.greenF = start.greenF + (greenSlice * it)
                    color.blueF = start.blueF + (blueSlice * it)
                    color.alpha = start.alpha + (alphaSlice * it)
                })
            }
        }
    }
}

