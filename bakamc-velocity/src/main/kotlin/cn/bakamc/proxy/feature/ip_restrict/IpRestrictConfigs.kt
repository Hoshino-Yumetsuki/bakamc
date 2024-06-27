package cn.bakamc.proxy.feature.ip_restrict

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigBoolean
import moe.forpleuvoir.nebula.config.item.impl.ConfigInt

object IpRestrictConfigs : ConfigContainerImpl("ip_restrict") {

    val ENABLED by ConfigBoolean("enabled", true)

    val CONNECT_LIMIT by ConfigInt("connect_limit", 2)

}