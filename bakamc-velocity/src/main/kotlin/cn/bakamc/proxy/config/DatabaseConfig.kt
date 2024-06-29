package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.annotation.ConfigMeta
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigInt
import moe.forpleuvoir.nebula.config.item.impl.ConfigLong
import moe.forpleuvoir.nebula.config.item.impl.ConfigString

object DatabaseConfig : ConfigContainerImpl("database") {

    @ConfigMeta(order = 0)
    val URL by ConfigString("url", "jdbc:mysql://localhost:3306/bakamc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")

    @ConfigMeta(order = 1)
    val USER by ConfigString("user", "root")

    @ConfigMeta(order = 2)
    val PASSWORD by ConfigString("password", "root")

    object DataSource : ConfigContainerImpl("data_source") {

        @ConfigMeta(order = 0)
        val CONNECTION_TIMEOUT by ConfigLong("connection_timeout", 60000)

        @ConfigMeta(order = 2)
        val IDLE_TIMEOUT by ConfigLong("idle_timeout", 60000)

        @ConfigMeta(order = 3)
        val MAXIMUM_POOL_SIZE by ConfigInt("maximum_pool_size", 30)

        @ConfigMeta(order = 4)
        val MAX_LIFETIME by ConfigLong("max_lifetime", 180000)

        @ConfigMeta(order = 5)
        val KEEPALIVE_TIME by ConfigLong("keepalive_time", 0)

        @ConfigMeta(order = 6)
        val MINIMUM_IDLE by ConfigInt("minimum_idle", 5)

    }

}