package cn.bakamc.common.text

import cn.bakamc.common.util.gradient
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.pick
import net.kyori.adventure.extra.kotlin.style
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

object BakaText {

    @JvmStatic
    val regex = Regex("&\\{.*?}")

    interface Modifier {
        fun modifier(exp: String): ((TextComponent) -> TextComponent)?
    }

    fun parse(
        content: String, bakaExpModifiers: List<Modifier> = listOf(
            DecorationModifier(),
            ColorModifier,
            LegacyChatFormattingModifier()
        )
    ): Component {
        val modifiers = ArrayList<List<(TextComponent) -> TextComponent>>()
        val text: TextComponent.Builder = Component.text()
        content.replace(regex) { result ->
            val value = result.value
            val list = mutableListOf<(TextComponent) -> TextComponent>()
            value.substring(2, value.length - 1).split(',').forEach { exp ->

                bakaExpModifiers.forEach { modifier ->
                    modifier.modifier(exp)?.let(list::add)
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

    data class DecorationMapping(
        val obfuscated: List<String> = listOf("o", "obfuscated"),
        val bold: List<String> = listOf("b", "bold"),
        val italic: List<String> = listOf("i", "italic"),
        val strikethrough: List<String> = listOf("s", "strikethrough"),
        val underline: List<String> = listOf("l", "underline"),
        val rest: List<String> = listOf("r", "rest"),
    )

    @JvmStatic
    val DECORATION_MAPPING = DecorationMapping()

    data class DecorationModifier(val decorationMapping: DecorationMapping = DECORATION_MAPPING) : Modifier {
        override fun modifier(exp: String): ((TextComponent) -> TextComponent)? {
            val inverted = exp.startsWith('!')
            val cs = if (exp.isNotEmpty()) inverted.pick(exp.substring(1), exp) else exp
            return when (cs) {
                in decorationMapping.obfuscated    -> { text -> text.decoration(TextDecoration.OBFUSCATED, !inverted) }
                in decorationMapping.bold          -> { text -> text.decoration(TextDecoration.BOLD, !inverted) }
                in decorationMapping.strikethrough -> { text -> text.decoration(TextDecoration.STRIKETHROUGH, !inverted) }
                in decorationMapping.underline     -> { text -> text.decoration(TextDecoration.UNDERLINED, !inverted) }
                in decorationMapping.italic        -> { text -> text.decoration(TextDecoration.ITALIC, !inverted) }
                in decorationMapping.rest          -> { text ->
                    text.color(null)
                        .decoration(TextDecoration.OBFUSCATED, false)
                        .decoration(TextDecoration.BOLD, false)
                        .decoration(TextDecoration.STRIKETHROUGH, false)
                        .decoration(TextDecoration.UNDERLINED, false)
                        .decoration(TextDecoration.ITALIC, false)
                }

                else                               -> null
            }
        }

    }

    object ColorModifier : Modifier {
        override fun modifier(exp: String): ((TextComponent) -> TextComponent)? {
            return parseColor(exp)
        }

        private fun parseColor(exp: String): ((TextComponent) -> TextComponent)? {
            parseRGBColor(exp)?.let {
                return it
            }
            parseHSVColor(exp)?.let {
                return it
            }
            return null
        }

        private fun parseRGBColor(exp: String): ((TextComponent) -> TextComponent)? {
            //&{#FF66CC}
            if (exp.matches(Regex("#[0-9A-Fa-f]{6}")) && exp.length == 7) {
                return { text -> text.color(TextColor.color(Color(exp).rgb)) }
            }
            //&{#FF66CC->#FF88BB}
            if (exp.matches(Regex("#[0-9A-Fa-f]{6}->#[0-9A-Fa-f]{6}"))) {
                val (_start, _end) = exp.split("->")
                val (start, end) = Color(_start) to Color(_end)
                return { text ->
                    var t = Component.text("").apply {
                        applyFallbackStyle(text.style())
                    }
                    val c = text.content()
                    start.gradient(end, c.length).forEachIndexed { index, color ->
                        t = t.append(Component.text(c[index].toString(), style {
                            color(TextColor.color(color.rgb))
                        }))
                    }
                    t
                }
            }
            return null
        }

        private fun parseHSVColor(exp: String): ((TextComponent) -> TextComponent)? {
            //&{[360 100 20]}
            if (exp.matches(Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]"))) {
                val hsv = exp.substring(1, exp.length - 1).split(' ').map { it.toFloat() }
                return { text -> text.color { HSVColor(hsv[0], hsv[1] / 100f, hsv[2] / 100f).rgb } }
            }
            //&{[360 99.6 20]->[360 20 100]}
            if (exp.matches(Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]->\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]"))) {
                val (_start, _end) = exp.split("->").map { it -> it.substring(1, it.length - 1).split(" ").map { it.toFloat() } }
                val (start, end) = HSVColor(_start[0], _start[1] / 100, _start[2] / 100) to HSVColor(_end[0], _end[1] / 100, _end[2] / 100)
                return { text ->
                    var t = Component.text("").apply {
                        applyFallbackStyle(text.style())
                    }
                    val c = text.content()
                    start.gradient(end, c.length).forEachIndexed { index, color ->
                        t = t.append(Component.text(c[index].toString(), style {
                            color(TextColor.color(color.rgb))
                        }))
                    }
                    t
                }
            }
            return null
        }
    }

    @Suppress("SpellCheckingInspection")
    const val LEGACY_FORMAT_CHARS = "0123456789abcdefklmonr"

    class LegacyChatFormattingModifier(
        private val prefixChar: Char = '$',
        formatChars: Set<Char> = LEGACY_FORMAT_CHARS.toSet(),
    ) : Modifier {

        private val formatChars: Set<Char> = fixFormatChars(formatChars)

        private fun fixFormatChars(formatChars: Set<Char>): Set<Char> {
            return buildSet {
                formatChars.forEach { c ->
                    if (c in LEGACY_FORMAT_CHARS) {
                        add(c)
                    }
                }
            }

        }

        override fun modifier(exp: String): ((TextComponent) -> TextComponent)? {
            if (exp.startsWith(prefixChar) && exp.length == 2) {
                return { text ->
                    if (exp[1] in formatChars) {
                        when (exp[1]) {
                            '0'  -> text.color(0x000000)
                            '1'  -> text.color(0x0000AA)
                            '2'  -> text.color(0x00AA00)
                            '3'  -> text.color(0x00AAAA)
                            '4'  -> text.color(0xAA0000)
                            '5'  -> text.color(0xAA00AA)
                            '6'  -> text.color(0xFFAA00)
                            '7'  -> text.color(0xAAAAAA)
                            '8'  -> text.color(0x555555)
                            '9'  -> text.color(0x5555FF)
                            'a'  -> text.color(0x55FF55)
                            'b'  -> text.color(0x55FFFF)
                            'c'  -> text.color(0xFF5555)
                            'd'  -> text.color(0xFF55FF)
                            'e'  -> text.color(0xFFFF55)
                            'f'  -> text.color(0xFFFFFF)
                            'k'  -> text.decoration(TextDecoration.OBFUSCATED, true)
                            'l'  -> text.decoration(TextDecoration.BOLD, true)
                            'm'  -> text.decoration(TextDecoration.STRIKETHROUGH, true)
                            'n'  -> text.decoration(TextDecoration.UNDERLINED, true)
                            'o'  -> text.decoration(TextDecoration.ITALIC, true)
                            'r'  -> {
                                text.color(null)
                                    .decoration(TextDecoration.OBFUSCATED, false)
                                    .decoration(TextDecoration.BOLD, false)
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                                    .decoration(TextDecoration.UNDERLINED, false)
                                    .decoration(TextDecoration.ITALIC, false)
                            }

                            else -> throw Exception("Unknown format char: ${exp[1]}")
                        }
                    } else text
                }
            }
            return null
        }

    }

}
