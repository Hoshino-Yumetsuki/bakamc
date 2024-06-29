package cn.bakamc.common.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style

fun literal(str: String): TextComponent {
    return Component.text(str)
}

fun literal(str: String, style: Style): TextComponent {
    return Component.text(str, style)
}


