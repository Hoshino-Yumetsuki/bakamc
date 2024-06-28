package cn.bakamc.folia.config

import cn.bakamc.common.config.item.ConfigStringDoubleMap
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object FlightEnergyConfig : ConfigContainerImpl("flight_energy") {

    val TICK_PERIOD by ConfigDuration("tick_period", 1.0.seconds)

    val ENERGY_COST by ConfigDouble("energy_cost", 1.0)

    val MAX_ENERGY by ConfigDouble("max_energy", 5000.0)

    val SYNC_PERIOD by ConfigDuration("sync_period", 5.0.minutes)

    val CLOSE_ADVENTURE_PLAYERS_FLYING by ConfigBoolean("close_adventure_players_flying", false)

    object EnergyBar : ConfigContainerImpl("energy_bar") {

        val COLOR: BarColor by ConfigEnum("color", BarColor.GREEN)

        val TITLE by ConfigString("title", "飞行能量: %.2f(%+.2f)/%.2f")

        val STYLE: BarStyle by ConfigEnum("style", BarStyle.SEGMENTED_10)

    }

    val PRICE by ConfigDouble("price", 1.0)

    val MONEY_ITEM by ConfigStringDoubleMap(
        "money_item",
        mapOf(
            "⑨币" to 5000.0,
            "冰辉石" to 78.125
        )
    )

}