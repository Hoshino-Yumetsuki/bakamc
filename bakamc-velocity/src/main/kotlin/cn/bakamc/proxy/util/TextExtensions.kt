package cn.bakamc.proxy.util

import cn.bakamc.proxy.util.Utils.gradient
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.pick
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

fun literal(str: String): TextComponent {
    return Component.text(str)
}

fun literal(str: String, style: Style): TextComponent {
    return Component.text(str, style)
}

fun parseBakaText(content: String): Component {
    val modifiers = ArrayList<List<(TextComponent) -> TextComponent>>()
    val regex = Regex("&\\{.*?}")
    val text: TextComponent.Builder = Component.text()
    content.replace(regex) { result ->
        val value = result.value
        val list = mutableListOf<(TextComponent) -> TextComponent>()
        value.substring(2, value.length - 1).split(',').forEach { s ->
            val inverted = s.startsWith('!')
            val cs = if (s.isNotEmpty()) inverted.pick(s.substring(1), s) else s

            when (cs) {
                "o", "obfuscated"    -> list.add { text -> text.decoration(TextDecoration.OBFUSCATED, !inverted) }
                "b", "bold"          -> list.add { text -> text.decoration(TextDecoration.BOLD, !inverted) }
                "strikethrough", "s" -> list.add { text -> text.decoration(TextDecoration.STRIKETHROUGH, !inverted) }
                "underline", "l"     -> list.add { text -> text.decoration(TextDecoration.UNDERLINED, !inverted) }
                "italic", "i"        -> list.add { text -> text.decoration(TextDecoration.ITALIC, !inverted) }
                "rest", "r"          -> list.add { text ->
                    text.color(TextColor.color(Colors.WHITE.rgb))
                        .decoration(TextDecoration.OBFUSCATED, false)
                        .decoration(TextDecoration.BOLD, false)
                        .decoration(TextDecoration.STRIKETHROUGH, false)
                        .decoration(TextDecoration.UNDERLINED, false)
                        .decoration(TextDecoration.ITALIC, false)
                }
            }
            //&{#FF66CC}
            if (s.matches(Regex("#[0-9A-Fa-f]{6}")) && s.length == 7) {
                list.add { text -> text.color(TextColor.color(Color(s).rgb)) }
            }
            //&{#FF66CC->#FF88BB}
            if (s.matches(Regex("#[0-9A-Fa-f]{6}->#[0-9A-Fa-f]{6}"))) {
                val (_start, _end) = s.split("->")
                val (start, end) = Color(_start) to Color(_end)
                list.add { text ->
                    val t = Component.text("").apply {
                        applyFallbackStyle(text.style())
                    }
                    val c = text.content()
                    start.gradient(end, c.length).forEachIndexed { index, color ->
                        t.append(Component.text(c[index].toString()).style { style ->
                            style.color { color.rgb }
                        })
                    }
                    t
                }
            }
            //&{[360 100 20]}
            if (s.matches(Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]"))) {
                val hsv = s.substring(1, s.length - 1).split(' ').map { it.toFloat() }
                list.add { text -> text.color { HSVColor(hsv[0], hsv[1] / 100f, hsv[2] / 100f).rgb } }
            }
            //&{[360 99.6 20]->[360 20 100]}
            if (s.matches(Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]->\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]"))) {
                val (_start, _end) = s.split("->").map { exp -> exp.substring(1, exp.length - 1).split(" ").map { it.toFloat() } }
                val (start, end) = HSVColor(_start[0], _start[1] / 100, _start[2] / 100) to HSVColor(_end[0], _end[1] / 100, _end[2] / 100)
                list.add { text ->
                    val t = Component.text("").apply {
                        applyFallbackStyle(text.style())
                    }
                    val c = text.content()
                    start.gradient(end, c.length).forEachIndexed { index, color ->
                        t.append(Component.text(c[index].toString()).style { style ->
                            style.color { color.rgb }
                        })
                    }
                    t
                }
            }
        }
        modifiers.add(list)
        value
    }.split(regex).toMutableList().let { list ->
        text.append(Component.text(list[0]))
        modifiers.forEachIndexed { index, modifier ->
            var t = Component.text(list[index + 1])
            modifier.forEach { m ->
                t = m.invoke(t)
            }
            text.append(t)
        }
    }
    return text.build()
}