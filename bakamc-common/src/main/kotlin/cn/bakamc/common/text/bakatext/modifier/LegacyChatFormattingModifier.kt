package cn.bakamc.common.text.bakatext.modifier

import cn.bakamc.common.text.color
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration.*

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
                        'k'  -> text.decoration(OBFUSCATED, true)
                        'l'  -> text.decoration(BOLD, true)
                        'm'  -> text.decoration(STRIKETHROUGH, true)
                        'n'  -> text.decoration(UNDERLINED, true)
                        'o'  -> text.decoration(ITALIC, true)
                        'r'  -> {
                            text.color(null)
                                .decoration(OBFUSCATED, false)
                                .decoration(BOLD, false)
                                .decoration(STRIKETHROUGH, false)
                                .decoration(UNDERLINED, false)
                                .decoration(ITALIC, false)
                        }

                        else -> throw Exception("Unknown format char: ${exp[1]}")
                    }
                } else text
            }
        }
        return null
    }

}