package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigInt
import moe.forpleuvoir.nebula.config.item.impl.ConfigLong
import moe.forpleuvoir.nebula.config.item.impl.ConfigString

object DatabaseConfig : ConfigContainerImpl("database") {

    override fun configureSerializable() {
        super.configureSerializable()
        allConfigSerializable()
    }

    val URL by ConfigString("url", "jdbc:mysql://localhost:3306/bakamc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")

    val USER by ConfigString("user", "root")

    val PASSWORD by ConfigString("password", "root")

    object DataSource : ConfigContainerImpl("data_source") {

        val CONNECTION_TIMEOUT by ConfigLong("connection_timeout", 60000)

        val IDLE_TIMEOUT by ConfigLong("idle_timeout", 60000)

        val MAXIMUM_POOL_SIZE by ConfigInt("maximum_pool_size", 30)

        val MAX_LIFETIME by ConfigLong("max_lifetime", 180000)

        val KEEPALIVE_TIME by ConfigLong("keepalive_time", 0)

        val MINIMUM_IDLE by ConfigInt("minimum_idle", 5)

    }

}