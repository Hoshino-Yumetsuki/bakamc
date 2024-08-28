package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.MiscConfig
import cn.bakamc.folia.config.MiscConfig.ENABLE_PLAYER_INTERACT_MODIFY
import cn.bakamc.folia.config.MiscConfig.quick_block_use
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.asNMS
import cn.bakamc.folia.util.ioLaunch
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.common.util.primitive.ifc
import net.minecraft.core.component.DataComponents
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
        get() = asNMS.components.get(DataComponents.)?.getString(BAKAMC_INTERACT_TAG_NAME)

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        //------------ 交互限制 ------------\\
        if (ENABLE_PLAYER_INTERACT_MODIFY) {
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
        }
        //------------ 直接打开功能方块 ------------\\
        if (event.action == Action.RIGHT_CLICK_AIR) {
            val player = event.player
            event.item?.let { item ->
                quickUse(item, player).ifc {
                    event.isCancelled = true
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
        val clicker = event.whoClicked
        if (event.inventory.type in quick_block_use.INVENTORY_TYPE && event.click == ClickType.RIGHT && clicker is Player) {
            event.currentItem?.let { item ->
                quickUse(item, clicker).ifc {
                    clicker.updateInventory()
                    event.isCancelled = true
                }
            }
        }
    }

    private fun quickUse(itemStack: ItemStack, player: Player): Boolean {
        return when (val type = itemStack.type) {
            Material.CRAFTING_TABLE    -> if (player.hasPermission("bakamc.quick_use.crafting_table")) {
                openBlock(type, player)
                true
            } else false

            Material.STONECUTTER       -> if (player.hasPermission("bakamc.quick_use.stonecutter")) {
                openBlock(type, player)
                true
            } else false

            Material.CARTOGRAPHY_TABLE -> if (player.hasPermission("bakamc.quick_use.cartography_table")) {
                openBlock(type, player)
                true
            } else false

            Material.GRINDSTONE        -> if (player.hasPermission("bakamc.quick_use.grindstone")) {
                openBlock(type, player)
                true
            } else false

            Material.LOOM              -> if (player.hasPermission("bakamc.quick_use.loom")) {
                openBlock(type, player)
                true
            } else false

            Material.SMITHING_TABLE    -> if (player.hasPermission("bakamc.quick_use.smithing_table")) {
                openBlock(type, player)
                true
            } else false

            Material.ENDER_CHEST       -> if (player.hasPermission("bakamc.quick_use.ender_chest")) {
                openBlock(type, player)
                true
            } else false

            else                       -> false
        }
    }

    private fun openBlock(type: Material, player: Player) {
        when (type) {
            Material.CRAFTING_TABLE    -> {
                player.openWorkbench(player.location, true)
            }

            Material.STONECUTTER       -> {
                player.openStonecutter(player.location, true)
            }

            Material.CARTOGRAPHY_TABLE -> {
                player.openCartographyTable(player.location, true)
            }

            Material.GRINDSTONE        -> {
                player.openGrindstone(player.location, true)
            }

            Material.LOOM              -> {
                player.openLoom(player.location, true)
            }

            Material.SMITHING_TABLE    -> {
                player.openSmithingTable(player.location, true)
            }

            Material.ENDER_CHEST       -> {
                player.openInventory(player.enderChest)
            }

            else                       -> Unit
        }
    }


}

