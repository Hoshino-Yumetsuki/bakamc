package cn.bakamc.folia.event

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.util.Reloadable
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

private val registrationList = listOf(
    EntityChangedBlockEventListener,
    EntityPickupEventListener,
    FlightEnergyManager,
    PlayerEventListener,
    BlockEventListener,
    ServerEventListener
)

fun JavaPlugin.registerEvent() {
    server.pluginManager.run {
        registrationList.forEach { register(it) }
    }
}

private fun PluginManager.register(listener: Listener) {
    this.registerEvents(listener, BakaMCPlugin.instance)
}

fun onReload() {
    registrationList.filter {
        it is Reloadable
    }.forEach {
        (it as Reloadable).reload()
    }
}