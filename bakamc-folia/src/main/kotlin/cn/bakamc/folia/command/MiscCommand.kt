package cn.bakamc.folia.command

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.command.base.*
import cn.bakamc.folia.util.launch
import org.bukkit.entity.Player

@Suppress("FunctionName", "DuplicatedCode")
fun MiscCommand(): Command = Command("bakamc") {
    literal("reload") {
        permission { it.sender.hasPermission("bakamc.admin") }
        execute {
            launch {
                BakaMCPlugin.instance.reload()
                it.success("重载配置文件")
            }
        }
    }

    literal("world") {
        permission { it.sender.hasPermission("bakamc.admin") }
        execute<Player> {
            it.feedback(it.sender.world.name)
        }
    }

    "setPermission" {
        permission { it.sender.hasPermission("bakamc.admin") }
        argument("permission") {
            argument("player") {
                execute { ctx ->
                    val player = ctx.getArg("player") { name -> ctx.sender.server.onlinePlayers.find { it.name == name } }
                    val permission = ctx.getArg("permission")!!
                    player?.addAttachment(BakaMCPlugin.instance)?.setPermission(permission, true)
                }
            }
        }
    }

    QuickUseCommand()

}

