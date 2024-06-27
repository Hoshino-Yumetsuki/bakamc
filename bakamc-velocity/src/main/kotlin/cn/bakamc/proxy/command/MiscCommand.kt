package cn.bakamc.proxy.command

import cn.bakamc.proxy.BakamcProxyInstance
import com.mojang.brigadier.Command
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

object MiscCommand {

    fun register(server: ProxyServer): BrigadierCommand {
        val node = literal("bakamc_proxy")
            .requires { source -> source.hasPermission("bakamc.op") }
            .then(
                literal("reload")
                    .executes { context ->
                        runCatching {
                            BakamcProxyInstance.reload()
                        }.onSuccess {
                            context.source.sendMessage(Component.text("Bakamc Proxy 配置重载成功", NamedTextColor.GREEN))
                        }.onFailure {
                            context.source.sendMessage(Component.text("Bakamc Proxy 配置重载失败", NamedTextColor.RED))
                            it.printStackTrace()
                        }
                        Command.SINGLE_SUCCESS
                    }
            )
            .build()
        return BrigadierCommand(node)
    }

}