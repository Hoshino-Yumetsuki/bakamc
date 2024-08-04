package cn.bakamc.folia.command

import cn.bakamc.folia.command.base.CommandNode
import cn.bakamc.folia.command.base.execute
import cn.bakamc.folia.command.base.permission
import org.bukkit.entity.Player

@Suppress("FunctionName")
fun CommandNode.QuickUseCommand(): CommandNode = "quick_use" {
    "crafting_table" {
        permission { it.sender.hasPermission("bakamc.quick_use.crafting_table") }
        execute<Player> { it.sender.openWorkbench(it.sender.location, true) }
    }
    "stonecutter" {
        permission { it.sender.hasPermission("bakamc.quick_use.stonecutter") }
        execute<Player> { it.sender.openStonecutter(it.sender.location, true) }
    }
    "cartography_table" {
        permission { it.sender.hasPermission("bakamc.quick_use.cartography_table") }
        execute<Player> { it.sender.openCartographyTable(it.sender.location, true) }
    }
    "grindstone" {
        permission { it.sender.hasPermission("bakamc.quick_use.grindstone") }
        execute<Player> { it.sender.openGrindstone(it.sender.location, true) }
    }
    "loom" {
        permission { it.sender.hasPermission("bakamc.quick_use.loom") }
        execute<Player> { it.sender.openLoom(it.sender.location, true) }
    }
    "smithing_table" {
        permission { it.sender.hasPermission("bakamc.quick_use.smithing_table") }
        execute<Player> { it.sender.openSmithingTable(it.sender.location, true) }
    }
    "ender_chest" {
        permission { it.sender.hasPermission("bakamc.quick_use.ender_chest") }
        execute<Player> { it.sender.openInventory(it.sender.enderChest) }
    }
}