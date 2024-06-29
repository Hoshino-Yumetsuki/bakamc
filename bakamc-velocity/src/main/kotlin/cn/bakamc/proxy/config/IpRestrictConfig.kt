package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.annotation.ConfigMeta
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigBoolean
import moe.forpleuvoir.nebula.config.item.impl.ConfigInt
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringList

object IpRestrictConfig : ConfigContainerImpl("ip_restrict") {

    @ConfigMeta(order = 0)
    val ENABLED by ConfigBoolean("enabled", true)

    @ConfigMeta(order = 1)
    val CONNECT_LIMIT by ConfigInt("connect_limit", 2)

    @ConfigMeta(order = 2)
    val INTERCEPT_MESSAGE by ConfigStringList(
        "intercept_message", listOf(
            "&{#FF2200}每个IP每天只允许#{CONNECT_LIMIT}个链接"
        )
    )
}