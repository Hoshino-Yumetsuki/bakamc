package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.MiscConfig
import cn.bakamc.folia.config.MiscConfig.BackpackBlockEntityUse.CARTOGRAPHY_TABLE
import cn.bakamc.folia.config.MiscConfig.BackpackBlockEntityUse.CRAFTING_TABLE
import cn.bakamc.folia.config.MiscConfig.BackpackBlockEntityUse.ENDER_CHEST
import cn.bakamc.folia.config.MiscConfig.BackpackBlockEntityUse.GRINDSTONE
import cn.bakamc.folia.config.MiscConfig.BackpackBlockEntityUse.LOOM
import cn.bakamc.folia.config.MiscConfig.BackpackBlockEntityUse.SMITHING_TABLE
import cn.bakamc.folia.config.MiscConfig.BackpackBlockEntityUse.STONECUTTER
import cn.bakamc.folia.config.MiscConfig.ENABLE_PLAYER_INTERACT_MODIFY
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.asNMS
import cn.bakamc.folia.util.ioLaunch
import cn.bakamc.folia.util.logger
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

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

    private val ItemStack.bakaInteractTag
        get() = asNMS.tag?.getString(BAKAMC_INTERACT_TAG_NAME)

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        //------------ 交互限制 ------------\\
        if (!ENABLE_PLAYER_INTERACT_MODIFY) return
        event.item?.let { item ->
            item.bakaInteractTag?.let { tag ->
                val list = tag.split(",")
                when (event.action) {
                    Action.LEFT_CLICK_BLOCK  -> "LEFT_CLICK_BLOCK" in list || "LEFT_CLICK_BLOCK:${event.clickedBlock!!.type.key.key}" in list
                    Action.RIGHT_CLICK_BLOCK -> "RIGHT_CLICK_BLOCK" in list || "LEFT_CLICK_BLOCK:${event.clickedBlock!!.type.key.key}" in list
                    Action.LEFT_CLICK_AIR    -> "LEFT_CLICK_AIR" in list
                    Action.RIGHT_CLICK_AIR   -> "RIGHT_CLICK_AIR" in list
                    Action.PHYSICAL          -> "PHYSICAL" in list
                }.takeIf { it }?.let { event.isCancelled = true }
            }
        }
        //------------ 直接打开功能方块 ------------\\
        if (event.action == Action.RIGHT_CLICK_AIR) {
            event.item?.let { item ->
                when (item.type) {
                    Material.CRAFTING_TABLE    -> if (CRAFTING_TABLE) {
                        openCraftingTable(event.player)
                    }

                    Material.STONECUTTER       -> if (STONECUTTER) {
                        openStonecutter(event.player)
                    }

                    Material.CARTOGRAPHY_TABLE -> if (CARTOGRAPHY_TABLE) {
                        openCartographyTable(event.player)
                    }

                    Material.GRINDSTONE        -> if (GRINDSTONE) {
                        openGrindstone(event.player)
                    }

                    Material.LOOM              -> if (LOOM) {
                        openLoom(event.player)
                    }

                    Material.SMITHING_TABLE    -> if (SMITHING_TABLE) {
                        openSmithingTable(event.player)
                    }

                    Material.ENDER_CHEST       -> if (ENDER_CHEST) {
                        openEnderChest(event.player)
                    }

                    else                       -> return
                }
            }
        }
    }

    @EventHandler
    fun onPlayerAttackEntity(event: EntityDamageByEntityEvent) {
        if (!ENABLE_PLAYER_INTERACT_MODIFY) return
        if (event.damager is Player) {
            val player = event.damager as Player
            player.inventory.itemInMainHand.bakaInteractTag?.let { tag ->
                val list = tag.split(",")
                if ("ATTACK_ENTITY" in list || "ATTACK_ENTITY:${event.entityType.key.key}" in list) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.click == ClickType.RIGHT && event.whoClicked is Player) {
            val player = event.whoClicked as Player
            if(player.hasPermission(""))
            event.currentItem?.let { item ->
                when (item.type) {
                    Material.CRAFTING_TABLE    -> if (CRAFTING_TABLE) {
                        openCraftingTable(player)
                    }

                    Material.STONECUTTER       -> if (STONECUTTER) {
                        openStonecutter(player)
                    }

                    Material.CARTOGRAPHY_TABLE -> if (CARTOGRAPHY_TABLE) {
                        openCartographyTable(player)
                    }

                    Material.GRINDSTONE        -> if (GRINDSTONE) {
                        openGrindstone(player)
                    }

                    Material.LOOM              -> if (LOOM) {
                        openLoom(player)
                    }

                    Material.SMITHING_TABLE    -> if (SMITHING_TABLE) {
                        openSmithingTable(player)
                    }

                    Material.ENDER_CHEST       -> if (ENDER_CHEST) {
                        openEnderChest(player)
                    }

                    else                       -> return
                }
                event.isCancelled = true
            }
        }
    }

    private fun openCraftingTable(player: Player) {
        player.openWorkbench(player.location, true)
    }

    private fun openStonecutter(player: Player) {
        player.openStonecutter(player.location, true)
    }

    private fun openCartographyTable(player: Player) {
        player.openCartographyTable(player.location, true)
    }

    private fun openGrindstone(player: Player) {
        player.openGrindstone(player.location, true)
    }

    private fun openLoom(player: Player) {
        player.openLoom(player.location, true)
    }

    private fun openSmithingTable(player: Player) {
        player.openSmithingTable(player.location, true)
    }

    private fun openEnderChest(player: Player) {
        player.openInventory(player.enderChest)
    }

}

