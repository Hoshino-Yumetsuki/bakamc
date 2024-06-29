package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.annotation.ConfigMeta
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigLongList

object BotConfig : ConfigContainerImpl("bot") {

    @ConfigMeta(order = 0)
    val BOTS by ConfigLongList(
        "bots", listOf(
            123456789L
        )
    )

    @ConfigMeta(order = 1)
    val CONSOLE_COMMAND_EXECUTORS by ConfigLongList("console_command", listOf(123456789L))

    @ConfigMeta(order = 2)
    val WhiteList = WhiteListConfigs

}