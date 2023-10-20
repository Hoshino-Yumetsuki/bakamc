package cn.bakamc.folia.flight_energy

import cn.bakamc.folia.config.Configs.FlightEnergy.ENERGY_COST
import cn.bakamc.folia.config.Configs.FlightEnergy.SYNC_PERIOD
import cn.bakamc.folia.config.Configs.FlightEnergy.TICK_PERIOD
import cn.bakamc.folia.extension.onlinePlayers
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.AsyncTask
import cn.bakamc.folia.util.runAtFixedRate
import moe.forpleuvoir.nebula.common.api.Initializable
import moe.forpleuvoir.nebula.common.util.minute
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.ConcurrentHashMap


object FlightEnergyManager : Listener, Initializable {


    private lateinit var energyCache: MutableMap<Player, Double>

    private lateinit var tasks: List<AsyncTask>

    override fun init() {
        tasks = listOf(
            //tick
            AsyncTask(0L, TICK_PERIOD) { tick() },
            //sync
            AsyncTask(1.minute, SYNC_PERIOD) { sync() }
        )

        tasks.forEach { runAtFixedRate(it) }

        energyCache = ConcurrentHashMap()
        energyCache.putAll(PlayerService.getFlightEnergy(onlinePlayers))

    }

    fun onDisable() {
        if (this::energyCache.isInitialized) {
            sync()
        }
        if (this::energyCache.isInitialized) {
            energyCache.clear()
        }
    }

    fun onPlayerJoin(player: Player) {
        energyCache[player] = PlayerService.getFlightEnergy(player)
    }

    fun onPlayerQuit(player: Player) {
        PlayerService.updateFlightEnergy(player, energyCache[player]!!)
        energyCache.remove(player)
    }

    fun sync() {
        PlayerService.updateFlightEnergy(energyCache)
    }

    /**
     * 玩家当前的飞行能量
     */
    var Player.energy: Double
        get() {
            return energyCache[this] ?: PlayerService.getFlightEnergy(this)
        }
        set(value) {
            energyCache[this] = value
        }

    /**
     * 由单独线程控制循环
     * 每秒执行一次
     */
    private fun tick() {
        onlinePlayers.filter {
            it.gameMode == GameMode.SURVIVAL && it.isFlying && it.energy > 0.0
        }.forEach {
            it.energy = (it.energy - (ENERGY_COST)).coerceAtLeast(0.0)
            if (it.energy <= 0.0) {
                it.allowFlight = false
                it.sendMessage("§c飞行能量已耗尽")
                it.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 400, 1, false, true))
            }
        }

    }


}