package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.int
import moe.forpleuvoir.nebula.config.item.impl.stringList

object IpRestrictConfig : ConfigContainerImpl("ip_restrict") {

    val ENABLED by boolean("enabled", true)

    val CONNECT_LIMIT by int("connect_limit", 2)

    val WHITELIST by stringList("whitelist", listOf())

    val INTERCEPT_MESSAGE by stringList(
        "intercept_message", listOf(
            "&{#FF2200}每个IP每天只允许#{CONNECT_LIMIT}个链接"
        )
    )
}