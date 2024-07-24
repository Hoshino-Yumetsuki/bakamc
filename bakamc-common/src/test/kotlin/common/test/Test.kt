package common.test

import moe.forpleuvoir.nebula.common.color.HSVColor
import org.junit.Test

class Test {
    @Test
    fun t1() {
        matchColor("[360 50 80]->[180 20 60]->[320 20 66]").forEach {
            println(it)
        }
    }

    fun matchColor(str: String) =
        buildList {
            val regex = Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]")
            str.split("[0-9]{2}".toRegex()).forEach { hsvStr ->
                if (hsvStr.matches(regex)) {
                    val (h, s, v) = hsvStr.substring(1, hsvStr.lastIndex ).split(" ").map { it.toFloat() }
                    add(HSVColor(h, s/100, v/100))
                }
            }
        }

}