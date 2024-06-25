package cn.bakamc.folia.event.entity

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.config.Configs.Misc.SERVER_START_COMMAND
import cn.bakamc.folia.util.server
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent

object ServerEventListener : Listener {

    @EventHandler
    fun onServerLoad(event: ServerLoadEvent) {
        if (event.type == ServerLoadEvent.LoadType.STARTUP) {
            SERVER_START_COMMAND.forEach { (cmd, delay) ->
                server.globalRegionScheduler.runDelayed(BakaMCPlugin.instance, {
                    server.dispatchCommand(server.consoleSender, cmd)
                }, delay)
            }
        }
    }
}