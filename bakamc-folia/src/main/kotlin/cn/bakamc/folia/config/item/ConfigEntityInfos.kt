package cn.bakamc.folia.config.item

import cn.bakamc.folia.event.pojo.EntityInfo
import moe.forpleuvoir.nebula.common.util.collection.NotifiableLinkedHashMap
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.ConfigMutableMapValue
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

class ConfigEntityInfos(
    override val key: String,
    defaultValue: Map<String, EntityInfo>
) : ConfigBase<MutableMap<String, EntityInfo>, ConfigEntityInfos>(), ConfigMutableMapValue<String, EntityInfo> {

    override var configValue: MutableMap<String, EntityInfo> = map(defaultValue)

    override val defaultValue: MutableMap<String, EntityInfo> = LinkedHashMap(defaultValue)

    private fun map(map: Map<String, EntityInfo>): NotifiableLinkedHashMap<String, EntityInfo> {
        return NotifiableLinkedHashMap(map).apply {
            subscribe {
                this@ConfigEntityInfos.onChange(this@ConfigEntityInfos)
            }
        }
    }

    override fun restDefault() {
        if (isDefault()) return
        configValue = map(defaultValue)
        onChange(this)
    }

    override fun serialization(): SerializeElement = serializeObject(configValue)

    override fun deserialization(serializeElement: SerializeElement) {
        serializeElement.asObject.apply {
            configValue = this@ConfigEntityInfos.map(this.mapValues { (_, entity) ->
                EntityInfo.deserialization(entity)
            })
        }
    }

}

fun ConfigContainer.entityInfos(key: String, default: Map<String, EntityInfo>) = addConfig(ConfigEntityInfos(key, default))