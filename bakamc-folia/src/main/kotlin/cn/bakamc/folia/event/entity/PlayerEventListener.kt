package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.MiscConfig
import cn.bakamc.folia.config.MiscConfig.ENABLE_PLAYER_INTERACT_MODIFY
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
        if (!MiscConfig.ENABLE_PLAYER_JOIN_MESSAGE) {
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
    fun onChangeWorld(event: PlayerChangedWorldEvent) {
        FlightEnergyManager.onWorldChanged(event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (!MiscConfig.ENABLE_PLAYER_QUIT_MESSAGE) {
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
        if (!ENABLE_PLAYER_INTERACT_MODIFY) return
        event.item?.let { item ->
            item.asNMS.tag?.getString(BAKAMC_INTERACT_TAG_NAME)?.let { tag ->
                val list = tag.split(",")
                when (event.action) {
                    Action.LEFT_CLICK_BLOCK  -> "LEFT_CLICK_BLOCK" in list
                    Action.RIGHT_CLICK_BLOCK -> "RIGHT_CLICK_BLOCK" in list
                    Action.LEFT_CLICK_AIR    -> "LEFT_CLICK_AIR" in list
                    Action.RIGHT_CLICK_AIR   -> "RIGHT_CLICK_AIR" in list
                    Action.PHYSICAL          -> "PHYSICAL" in list
                }.takeIf { it }?.let { event.isCancelled = true }
            }
        }
    }

}