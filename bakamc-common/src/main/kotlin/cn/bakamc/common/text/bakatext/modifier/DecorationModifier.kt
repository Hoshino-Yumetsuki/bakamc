package cn.bakamc.common.text.bakatext.modifier

import moe.forpleuvoir.nebula.common.pick
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration.*

val DECORATION_MAPPING = DecorationMapping()

data class DecorationMapping(
    val obfuscated: List<String> = listOf("o", "obfuscated"),
    val bold: List<String> = listOf("b", "bold"),
    val italic: List<String> = listOf("i", "italic"),
    val strikethrough: List<String> = listOf("s", "strikethrough"),
    val underline: List<String> = listOf("l", "underline"),
    val rest: List<String> = listOf("r", "rest"),
)

data class DecorationModifier(val decorationMapping: DecorationMapping = DECORATION_MAPPING) : Modifier {
    override fun modifier(exp: String): ((TextComponent) -> TextComponent)? {
        val inverted = exp.startsWith('!')
        val cs = if (exp.isNotEmpty()) inverted.pick(exp.substring(1), exp) else exp
        return when (cs) {
            in decorationMapping.obfuscated    -> { text -> text.decoration(OBFUSCATED, !inverted) }
            in decorationMapping.bold          -> { text -> text.decoration(BOLD, !inverted) }
            in decorationMapping.strikethrough -> { text -> text.decoration(STRIKETHROUGH, !inverted) }
            in decorationMapping.underline     -> { text -> text.decoration(UNDERLINED, !inverted) }
            in decorationMapping.italic        -> { text -> text.decoration(ITALIC, !inverted) }
            in decorationMapping.rest          -> { text ->
                text.color(null)
                    .decoration(OBFUSCATED, false)
                    .decoration(BOLD, false)
                    .decoration(STRIKETHROUGH, false)
                    .decoration(UNDERLINED, false)
                    .decoration(ITALIC, false)
            }

            else                               -> null
        }
    }

}