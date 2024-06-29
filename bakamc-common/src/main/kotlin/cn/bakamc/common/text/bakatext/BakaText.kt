package cn.bakamc.common.text.bakatext

import cn.bakamc.common.text.bakatext.modifier.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent

object BakaText {

    @JvmStatic
    val regex = Regex("&\\{.*?}")

    fun parse(
        content: String, bakaExpModifiers: List<Modifier> = listOf(
            DecorationModifier(),
            ColorModifier,
            LegacyChatFormattingModifier(),
            ClickEventModifier,
            HoverEventModifier,
        )
    ): Component {
        val modifiers = ArrayList<List<(TextComponent) -> TextComponent>>()
        val text: TextComponent.Builder = text()
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
            text.append(text(list[0]))
            modifiers.forEachIndexed { index, modifier ->
                var t = text(list[index + 1])
                modifier.forEach { m ->
                    t = m.invoke(t)
                }
                text.append(t)
            }
        }
        return text.build()
    }


}
