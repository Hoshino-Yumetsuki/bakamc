package cn.bakamc.proxy.config

import cn.bakamc.common.config.component.backup
import cn.bakamc.common.config.component.generateTemp
import cn.bakamc.proxy.BakamcProxyInstance
import cn.bakamc.proxy.BakamcProxyInstance.logger
import cn.bakamc.proxy.feature.ip_restrict.IpRestrictConfigs
import cn.bakamc.proxy.feature.white_list.WhiteListConfigs
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import moe.forpleuvoir.nebula.config.manager.ConfigManagerImpl
import moe.forpleuvoir.nebula.config.manager.component.localConfig
import moe.forpleuvoir.nebula.config.manager.components
import moe.forpleuvoir.nebula.config.persistence.ConfigManagerPersistence
import moe.forpleuvoir.nebula.config.persistence.jsonPersistence
import java.nio.file.Path

object Configs : ConfigManagerImpl(BakamcProxyInstance.INSTANCE.bakaName) {

    lateinit var configPath: Path
        internal set

    /**
     * 配置文件路径
     * @param path Path
     */
    suspend fun init(path: Path) {
        configPath = path

        components {
            val persistence: ConfigManagerPersistence = jsonPersistence()
            localConfig(configPath, persistence)
            backup(configPath, logger)
            generateTemp(configPath, persistence, logger)
        }

        init()
        runCatching {
            load()
            if (this.needSave) {
                save()
            }
        }.onFailure {
            it.printStackTrace()
            forceSave()
        }
    }

    object Database : ConfigContainerImpl("database") {

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

    object Misc : ConfigContainerImpl("misc") {
        val SERVER_NAME_MAPPING by ConfigStringMap(
            "server_name_mapping", mapOf(
                "s1" to "生存服",
                "s2" to "建筑服"
            )
        )
    }

    object Bot : ConfigContainerImpl("bot") {

        val CONSOLE_COMMAND_EXECUTORS by ConfigLongList("console_command", listOf(123456789L))

    }

    val WhiteList = WhiteListConfigs

    val IpRestrict = IpRestrictConfigs

}