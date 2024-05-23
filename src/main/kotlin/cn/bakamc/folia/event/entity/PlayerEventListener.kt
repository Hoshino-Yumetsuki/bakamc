package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.logger
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.*
import org.bukkit.inventory.meta.BlockStateMeta

object PlayerEventListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!Configs.Misc.ENABLE_PLAYER_JOIN_MESSAGE) {
            event.joinMessage(null)
        }
        launch {
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
        launch {
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

//    @EventHandler
    fun onPlayerUse(event: PlayerInteractEvent) {
        val player = event.player
        if (event.action == Action.RIGHT_CLICK_AIR && player.isSneaking) {
            val shulkerBoxItem =
                if (player.inventory.itemInMainHand.type == Material.SHULKER_BOX) player.inventory.itemInMainHand
                else if (player.inventory.itemInOffHand.type == Material.SHULKER_BOX) player.inventory.itemInOffHand else null
            if (shulkerBoxItem == null) return
            if (shulkerBoxItem.itemMeta is BlockStateMeta) {
                val boxMeta = shulkerBoxItem.itemMeta as BlockStateMeta
                if(boxMeta.blockState is ShulkerBox){
                    val shulkerBox = boxMeta.blockState as ShulkerBox
                    val boxInventory = Bukkit.createInventory(null, 27, Component.text("潜影盒"))
                    boxInventory.contents = shulkerBox.inventory.contents
                    player.openInventory(boxInventory)
                }
            }
        }

    }

}