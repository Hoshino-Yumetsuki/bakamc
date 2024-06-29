package cn.bakamc.proxy.config

import cn.bakamc.common.config.component.backup
import cn.bakamc.common.config.component.generateTemp
import cn.bakamc.proxy.BakamcProxyInstance
import cn.bakamc.proxy.BakamcProxyInstance.logger
import moe.forpleuvoir.nebula.config.annotation.ConfigMeta
import moe.forpleuvoir.nebula.config.manager.ConfigManagerImpl
import moe.forpleuvoir.nebula.config.manager.component.localConfig
import moe.forpleuvoir.nebula.config.manager.components
import moe.forpleuvoir.nebula.config.persistence.ConfigManagerPersistence
import moe.forpleuvoir.nebula.config.persistence.jsonPersistence
import java.nio.file.Path

object Configs : ConfigManagerImpl(BakamcProxyInstance.INSTANCE.bakaName) {

    /**
     * 配置文件路径
     * @param path Path
     */
    suspend fun init(path: Path) {
        components {
            val persistence: ConfigManagerPersistence = jsonPersistence()
            localConfig(path, persistence)
            backup(path, logger)
            generateTemp(path, persistence, logger)
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

    @ConfigMeta(order = 0)
    val DataBase = DatabaseConfig

    @ConfigMeta(order = 1)
    val Misc = MiscConfig

    @ConfigMeta(order = 2)
    val Bot = BotConfig

    @ConfigMeta(order = 3)
    val IpRestrict = IpRestrictConfig

}