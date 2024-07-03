package cn.bakamc.folia.flight_energy

import cn.bakamc.folia.config.FlightEnergyConfig
import cn.bakamc.folia.config.FlightEnergyConfig.MAX_ENERGY
import cn.bakamc.folia.db.table.FlightEnergy
import moe.forpleuvoir.nebula.common.util.clamp
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.boss.KeyedBossBar
import org.bukkit.entity.Player
import java.util.*

class EnergyBar private constructor(
    private val server: Server,
    player: Player,
    private val flightEnergy: FlightEnergy,
) {

    private var lastEnergy = flightEnergy.energy

    companion object {
        fun create(server: Server, player: Player, flightEnergy: FlightEnergy): EnergyBar {
            return EnergyBar(server, player, flightEnergy)
        }
    }

    private var bar: KeyedBossBar

    var key: NamespacedKey = NamespacedKey.minecraft("energy_bar_${player.name.lowercase(Locale.ENGLISH)}")

    init {
        bar = server.createBossBar(key, title(), FlightEnergyConfig.EnergyBar.COLOR, FlightEnergyConfig.EnergyBar.STYLE)
        bar.isVisible = false
        bar.progress = progress
        bar.addPlayer(player)
    }

    fun tick() {
        bar.setTitle(title())
        bar.progress = progress
        lastEnergy = flightEnergy.energy
    }

    private val progress get() = (flightEnergy.energy / MAX_ENERGY).clamp(0.0, 1.0)

    private fun title(): String {
        return FlightEnergyConfig.EnergyBar.TITLE.format(flightEnergy.energy, flightEnergy.energy - lastEnergy, MAX_ENERGY)
    }

    fun setVisible(visible: Boolean) {
        bar.isVisible = visible && flightEnergy.barVisible
    }

    fun close() {
        bar.removeAll()
        server.removeBossBar(key)
    }
}