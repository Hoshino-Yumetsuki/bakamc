package cn.bakamc.proxy.command

import cn.bakamc.proxy.BakamcProxyInstance
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer


fun registerCommands(server: ProxyServer) {
    server.commandManager.apply {
        register(MiscCommand.register(server))
    }
    BakamcProxyInstance.logger.info("Bakamc Proxy 指令注册完成")
}


fun literal(node: String): LiteralArgumentBuilder<CommandSource> {
    return BrigadierCommand.literalArgumentBuilder(node)
}

fun <T> argument(node: String, argument: ArgumentType<T>): RequiredArgumentBuilder<CommandSource, T> {
    return BrigadierCommand.requiredArgumentBuilder(node, argument)
}