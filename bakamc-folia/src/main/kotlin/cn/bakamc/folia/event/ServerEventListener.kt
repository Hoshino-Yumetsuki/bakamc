package cn.bakamc.folia.event

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.config.MiscConfig
import cn.bakamc.folia.util.server
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent

object ServerEventListener : Listener {

    @EventHandler
    fun onServerLoad(event: ServerLoadEvent) {
        if (event.type == ServerLoadEvent.LoadType.STARTUP) {
            MiscConfig.SERVER_START_COMMAND.forEach { (cmd, delay) ->
                server.globalRegionScheduler.runDelayed(BakaMCPlugin.Companion.instance, {
                    server.dispatchCommand(server.consoleSender, cmd)
                }, delay)
            }
        }
    }
}