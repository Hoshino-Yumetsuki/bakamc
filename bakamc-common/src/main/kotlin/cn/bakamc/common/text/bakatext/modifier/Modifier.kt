package cn.bakamc.common.text.bakatext.modifier

import net.kyori.adventure.text.TextComponent

interface Modifier {
    fun modifier(exp: String): ((TextComponent) -> TextComponent)?
}