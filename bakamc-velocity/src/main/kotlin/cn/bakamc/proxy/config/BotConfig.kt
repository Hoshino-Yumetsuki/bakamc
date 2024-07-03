package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.longList

object BotConfig : ConfigContainerImpl("bot") {

    val BOTS by longList(
        "bots", listOf()
    )

    val GROUPS by longList("groups", listOf())

    val CONSOLE_COMMAND_EXECUTORS by longList("console_command_executors", listOf())

    val WhiteList = addConfig(WhiteListConfigs)

}