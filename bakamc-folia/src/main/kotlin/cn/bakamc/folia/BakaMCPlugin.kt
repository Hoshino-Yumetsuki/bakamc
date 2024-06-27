package cn.bakamc.folia

import cn.bakamc.folia.command.registerCommand
import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.initDataBase
import cn.bakamc.folia.event.entity.EntityChangedBlockEventListener
import cn.bakamc.folia.event.registerEvent
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.item.SpecialItemManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin

class BakaMCPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: BakaMCPlugin
            private set

        internal val PluginDefaultScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

        internal val PluginIOScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    }

    lateinit var economy: Economy
        private set


    override fun onEnable() {
        instance = this
        logger.info("BakaMCPlugin loading...")
        if (setupEconomy()) {
            logger.info("BakaMCPlugin 找到经济插件")
        } else {
            logger.warning("BakaMCPlugin 未找到经济插件")
        }

        Configs.onLoaded {
            EntityChangedBlockEventListener.reloadCache()

            initDataBase()

            SpecialItemManager.init()
            FlightEnergyManager.init()
        }

        runBlocking {
            Configs.step(dataFolder.toPath())
        }

        registerCommand()

        registerEvent()

        logger.info("BakaMCPlugin is enabled")
    }

    fun reload() {

        server.asyncScheduler.cancelTasks(this)
        FlightEnergyManager.onDisable()
        SpecialItemManager.onDisable()

        runBlocking {
            Configs.load()
        }

    }

    fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp: RegisteredServiceProvider<Economy> = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        economy = rsp.provider
        return true
    }


    override fun onDisable() {
        server.asyncScheduler.cancelTasks(this)
        FlightEnergyManager.onDisable()
        SpecialItemManager.onDisable()
        PluginDefaultScope.cancel()
        PluginIOScope.cancel()
    }

}