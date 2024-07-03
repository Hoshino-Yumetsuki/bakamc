package cn.bakamc.common.config.item

import cn.bakamc.common.ServerInfo
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement

class ConfigSeverInfo(
    override val key: String,
    override val defaultValue: ServerInfo
) : ConfigBase<ServerInfo, ConfigSeverInfo>() {

    override var configValue: ServerInfo = defaultValue

    override fun deserialization(serializeElement: SerializeElement) {
        configValue = ServerInfo.deserialization(serializeElement)
    }

    override fun serialization(): SerializeElement = ServerInfo.serialization(configValue)
}

fun ConfigContainer.serverInfo(key: String, defaultValue: ServerInfo) = addConfig(ConfigSeverInfo(key, defaultValue))