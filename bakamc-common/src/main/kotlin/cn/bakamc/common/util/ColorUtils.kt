package cn.bakamc.common.util

import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.HSVColor

fun HSVColor.gradient(end: HSVColor, sliceSize: Int): List<HSVColor> {
    return gradientHSVColor(this, end, sliceSize)
}

fun Color.gradient(end: Color, sliceSize: Int): List<Color> {
    return gradientColor(this, end, sliceSize)
}

@Suppress("DuplicatedCode")
fun gradientHSVColor(start: HSVColor, end: HSVColor, sliceSize: Int): List<HSVColor> {
    check(sliceSize > 0) { "slice must be greater than 0" }
    if (sliceSize == 1) return listOf(HSVColor(start.hue, start.saturation, start.value, start.value, false))
    val hueSlice = (end.hue - start.hue) / sliceSize
    val saturationSlice = (end.saturation - start.saturation) / sliceSize
    val valueSlice = (end.value - start.value) / sliceSize
    val alphaSlice = (end.alpha - start.alpha) / sliceSize
    return buildList {
        repeat(sliceSize) {
            add(HSVColor(start.hue, start.saturation, start.value, start.value, false).also { color ->
                color.hue = start.hue + (hueSlice * it)
                color.saturation = start.saturation + (saturationSlice * it)
                color.value = start.value + (valueSlice * it)
                color.alpha = start.alpha + (alphaSlice * it)
            })
        }
    }
}

@Suppress("DuplicatedCode")
fun gradientColor(start: Color, end: Color, sliceSize: Int): List<Color> {
    check(sliceSize > 0) { "slice must be greater than 0" }
    if (sliceSize == 1) return listOf(Color(start.red, start.green, start.blue, start.alpha, false))
    val redSlice = (end.redF - start.redF) / sliceSize
    val greenSlice = (end.greenF - start.greenF) / sliceSize
    val blueSlice = (end.blueF - start.blueF) / sliceSize
    val alphaSlice = (end.alpha - start.alpha) / sliceSize
    return buildList {
        repeat(sliceSize) {
            add(Color(start.red, start.green, start.blue, start.alpha, false).also { color ->
                color.redF = start.redF + (redSlice * it)
                color.greenF = start.greenF + (greenSlice * it)
                color.blueF = start.blueF + (blueSlice * it)
                color.alpha = start.alpha + (alphaSlice * it)
            })
        }
    }
}