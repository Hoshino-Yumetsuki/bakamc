package cn.bakamc.common.util

import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.HSVColor

fun HSVColor.gradient(to: HSVColor, steps: Int): List<HSVColor> {
    return gradientHSVColor(this, to, steps)
}

fun ARGBColor.gradient(to: ARGBColor, steps: Int): List<ARGBColor> {
    return if (this is HSVColor && to is HSVColor)
        this.gradient(to, steps)
    else
        gradientColor(this, to, steps)
}

@Suppress("DuplicatedCode")
fun gradientHSVColor(from: HSVColor, to: HSVColor,steps: Int): List<HSVColor> {
    check(steps > 0) { "steps must be greater than 0" }
    if (steps == 1) return listOf(HSVColor(from.hue, from.saturation, from.value, from.value, false))

    return buildList {
        repeat(steps) { index ->
            add(from.lerp(to, (index * (1f / (steps - 1))).coerceIn(0f, 1f)))
        }
    }
}

@Suppress("DuplicatedCode")
fun gradientColor(from: ARGBColor, to: ARGBColor, steps: Int): List<ARGBColor> {
    check(steps > 0) { "steps must be greater than 0" }
    if (steps == 1) return listOf(Color(from.red, from.green, from.blue, from.alpha, false))
    return buildList {
        repeat(steps) { index ->
            add(from.lerp(to, (index * (1f / (steps - 1))).coerceIn(0f, 1f)))
        }
    }
}