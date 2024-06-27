package cn.bakamc.common.config.component

import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.nebula.config.manager.ConfigManager
import moe.forpleuvoir.nebula.config.manager.component.ConfigManagerComponent
import moe.forpleuvoir.nebula.config.persistence.ConfigManagerPersistence
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import java.nio.file.Path

class GenerateTemp(
    val manager: () -> ConfigManager,
    val configPath: () -> Path,
    val persistence: () -> ConfigManagerPersistence
) : ConfigManagerComponent {

    override fun beforeInit() {
        runBlocking {
            runCatching {
                ConfigUtil.run {
                    val fileName = persistence().wrapFileName(manager().key)
                    val file = configFile("$fileName.temp", configPath())
                    writeToFile(persistence().serializeToString(manager().serialization().asObject), file)
                }
            }.onFailure {
                logger.error("模板文件生成失败", it)
            }.onSuccess {
                logger.info("模板文件生成成功")
            }
        }
    }

}