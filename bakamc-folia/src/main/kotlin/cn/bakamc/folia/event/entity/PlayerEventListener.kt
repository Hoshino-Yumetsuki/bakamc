package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.asNMS
import cn.bakamc.folia.util.ioLaunch
import cn.bakamc.folia.util.logger
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.*

object PlayerEventListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!Configs.Misc.ENABLE_PLAYER_JOIN_MESSAGE) {
            event.joinMessage(null)
        }
        ioLaunch {
            runCatching {
                PlayerService.insertOrUpdate(event.player)
                FlightEnergyManager.onPlayerJoin(event.player)
            }.onSuccess {
                logger.info("玩家加入游戏")
            }.onFailure {
                logger.error("数据库错误", it)
            }
        }
    }


    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (!Configs.Misc.ENABLE_PLAYER_QUIT_MESSAGE) {
            event.quitMessage(null)
        }
        ioLaunch {
            runCatching {
                FlightEnergyManager.onPlayerQuit(event.player)
            }.onSuccess {
                logger.info("玩家加退出游戏")
            }.onFailure {
                logger.error("数据库错误", it)
            }
        }
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        FlightEnergyManager.onPlayerRespawn(event.player)
    }

    @EventHandler
    fun onPlayerGameModeChange(event: PlayerGameModeChangeEvent) {
        if (event.player.isOnline) {
            FlightEnergyManager.onPlayerGameModeChange(event.player, event.newGameMode)
        }
    }

    private const val BAKAMC_INTERACT_TAG_NAME = "bakamc_interact"

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        event.item?.let { item ->
            item.asNMS.tag?.getCompound(BAKAMC_INTERACT_TAG_NAME)?.let { tag ->
                event.isCancelled = when (event.action) {
                    Action.LEFT_CLICK_BLOCK  -> tag.getBoolean("LEFT_CLICK_BLOCK")
                    Action.RIGHT_CLICK_BLOCK -> tag.getBoolean("RIGHT_CLICK_BLOCK")
                    Action.LEFT_CLICK_AIR    -> tag.getBoolean("LEFT_CLICK_AIR")
                    Action.RIGHT_CLICK_AIR   -> tag.getBoolean("RIGHT_CLICK_AIR")
                    Action.PHYSICAL          -> tag.getBoolean("PHYSICAL")
                }
            }
        }
    }

}