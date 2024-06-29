package cn.bakamc.folia.config

import cn.bakamc.common.config.item.ConfigStringDoubleMap
import moe.forpleuvoir.nebula.config.annotation.ConfigMeta
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object FlightEnergyConfig : ConfigContainerImpl("flight_energy") {

    @ConfigMeta(order = 0)
    val TICK_PERIOD by ConfigDuration("tick_period", 1.0.seconds)

    @ConfigMeta(order = 1)
    val ENERGY_COST by ConfigDouble("energy_cost", 1.0)

    @ConfigMeta(order = 2)
    val MAX_ENERGY by ConfigDouble("max_energy", 5000.0)

    @ConfigMeta(order = 3)
    val SYNC_PERIOD by ConfigDuration("sync_period", 5.0.minutes)

    @ConfigMeta(order = 4)
    val ALLOW_FLY_WORLD by ConfigStringList("allow_fly_world", listOf("world"))

    @ConfigMeta(order = 4)
    val FORBID_FLY_WORLD_MESSAGE by ConfigString("forbid_fly_world_message","&{#FF0000}所在的世界禁止飞行")

    @ConfigMeta(order = 5)
    val CLOSE_ADVENTURE_PLAYERS_FLYING by ConfigBoolean("close_adventure_players_flying", false)

    @ConfigMeta(order = 6)
    val ENERGY_PRICE by ConfigDouble("energy_price", 1.0)

    @ConfigMeta(order = 7)
    val MONEY_ITEM by ConfigStringDoubleMap(
        "money_item",
        mapOf(
            "⑨币" to 5000.0,
            "冰辉石" to 78.125
        )
    )

    @ConfigMeta(order = 8)
    object EnergyBar : ConfigContainerImpl("energy_bar") {

        @ConfigMeta(order = 0)
        val COLOR: BarColor by ConfigEnum("color", BarColor.GREEN)

        @ConfigMeta(order = 1)
        val TITLE by ConfigString("title", "飞行能量: %.2f(%+.2f)/%.2f")

        @ConfigMeta(order = 2)
        val STYLE: BarStyle by ConfigEnum("style", BarStyle.SEGMENTED_10)

    }

}