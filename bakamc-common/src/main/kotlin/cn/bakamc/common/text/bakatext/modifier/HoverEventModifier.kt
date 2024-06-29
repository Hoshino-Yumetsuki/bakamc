package cn.bakamc.common.text.bakatext.modifier

import cn.bakamc.common.text.bakatext.BakaText
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent

object HoverEventModifier : Modifier {

    private val pattern = """hover=>.+""".toRegex()
    override fun modifier(exp: String): ((TextComponent) -> TextComponent)? {
        if (!exp.matches(pattern)) return null
        val content = exp.split("=>")[1]
        return { text ->
            text.hoverEvent(
                HoverEvent.showText(
                    text {
                        content.split("\\n").forEachIndexed { index, line ->
                            BakaText.parse(
                                line, listOf(
                                    DecorationModifier(),
                                    ColorModifier,
                                    LegacyChatFormattingModifier(),
                                )
                            ).let {
                                append(it)
                            }
                        }

                    }
                )
            )
        }
    }

}