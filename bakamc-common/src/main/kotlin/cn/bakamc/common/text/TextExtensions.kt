package cn.bakamc.common.text

import moe.forpleuvoir.nebula.common.color.RGBColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ScopedComponent
import net.kyori.adventure.text.format.TextColor

fun Component.color(color: RGBColor): Component {
    return this.color(TextColor.color(color.rgb))
}

fun <C : Component> ScopedComponent<C>.color(color: RGBColor): C {
    return this.color(TextColor.color(color.rgb))
}

fun Component.color(color: Int): Component {
    return this.color(TextColor.color(color))
}

fun <C : Component> ScopedComponent<C>.color(color: Int): C {
    return this.color(TextColor.color(color))
}