package cn.bakamc.folia.config

import cn.bakamc.common.config.component.backup
import cn.bakamc.common.config.component.generateTemp
import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.config.item.BlockConfig
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.config.manager.ConfigManagerImpl
import moe.forpleuvoir.nebula.config.manager.component.localConfig
import moe.forpleuvoir.nebula.config.manager.components
import moe.forpleuvoir.nebula.config.persistence.ConfigManagerPersistence
import moe.forpleuvoir.nebula.config.persistence.jsonPersistence
import java.nio.file.Path

object Configs : ConfigManagerImpl(BakaMCPlugin.instance.bakaName) {

    /**
     * 配置文件路径
     * @param path Path
     */
    suspend fun step(path: Path) {
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
            logger.warn(it.message, it)
            forceSave()
        }
    }

    override fun configureSerializable() {
        addConfigSerializable(DatabaseConfig)
        addConfigSerializable(MiscConfig)
        addConfigSerializable(FlightEnergyConfig)
        addConfigSerializable(BlockConfig)
        addConfigSerializable(EntityConfig)
    }

}