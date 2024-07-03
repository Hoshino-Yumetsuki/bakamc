package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object FlightEnergyConfig : ConfigContainerImpl("flight_energy") {

    val TICK_PERIOD by duration("tick_period", 1.0.seconds)

    val ENERGY_COST by double("energy_cost", 1.0)

    val MAX_ENERGY by double("max_energy", 5000.0)

    val SYNC_PERIOD by duration("sync_period", 5.0.minutes)

    val ALLOW_FLY_WORLD by stringList("allow_fly_world", listOf("world"))

    val FORBID_FLY_WORLD_MESSAGE by string("forbid_fly_world_message", "&{#FF0000}所在的世界禁止飞行")

    val CLOSE_ADVENTURE_PLAYERS_FLYING by boolean("close_adventure_players_flying", false)

    val ENERGY_PRICE by double("energy_price", 1.0)

    val MONEY_ITEM by stringDoubleMap(
        "money_item",
        mapOf(
            "⑨币" to 5000.0,
            "冰辉石" to 78.125
        )
    )

    private val energyBar = addConfig(EnergyBar)

    object EnergyBar : ConfigContainerImpl("energy_bar") {

        val COLOR: BarColor by enum("color", BarColor.GREEN)

        val TITLE by string("title", "飞行能量: %.2f(%+.2f)/%.2f")

        val STYLE: BarStyle by enum("style", BarStyle.SEGMENTED_10)

    }

}