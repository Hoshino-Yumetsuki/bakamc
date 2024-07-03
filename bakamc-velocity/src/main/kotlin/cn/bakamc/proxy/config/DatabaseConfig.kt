package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.int
import moe.forpleuvoir.nebula.config.item.impl.long
import moe.forpleuvoir.nebula.config.item.impl.string

object DatabaseConfig : ConfigContainerImpl("database") {

    val URL by string("url", "jdbc:mysql://localhost:3306/bakamc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")

    val USER by string("user", "root")

    val PASSWORD by string("password", "root")


    private val source = addConfig(DataSource)

    object DataSource : ConfigContainerImpl("data_source") {

        val CONNECTION_TIMEOUT by long("connection_timeout", 60000)

        val IDLE_TIMEOUT by long("idle_timeout", 60000)

        val MAXIMUM_POOL_SIZE by int("maximum_pool_size", 30)

        val MAX_LIFETIME by long("max_lifetime", 180000)

        val KEEPALIVE_TIME by long("keepalive_time", 0)

        val MINIMUM_IDLE by int("minimum_idle", 5)

    }

}