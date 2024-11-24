package cn.bakamc.folia.config.item

import cn.bakamc.folia.event.pojo.ItemEntityInfo
import moe.forpleuvoir.nebula.common.util.collection.NotifiableLinkedHashMap
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.ConfigMutableMapValue
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

class ConfigItemEntityInfos(
    override val key: String,
    defaultValue: Map<String, ItemEntityInfo>
) : ConfigBase<MutableMap<String, ItemEntityInfo>, ConfigItemEntityInfos>(), ConfigMutableMapValue<String, ItemEntityInfo> {

    override var configValue: MutableMap<String, ItemEntityInfo> = map(defaultValue)

    override val defaultValue: MutableMap<String, ItemEntityInfo> = LinkedHashMap(defaultValue)

    private fun map(map: Map<String, ItemEntityInfo>): NotifiableLinkedHashMap<String, ItemEntityInfo> {
        return NotifiableLinkedHashMap(map).apply {
            subscribe {
                this@ConfigItemEntityInfos.onChange(this@ConfigItemEntityInfos)
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
            configValue = this@ConfigItemEntityInfos.map(this.mapValues { (_, entity) ->
                ItemEntityInfo.deserialization(entity)
            })
        }
    }

}

fun ConfigContainer.itemEntityInfos(key: String, default: Map<String, ItemEntityInfo>) = addConfig(ConfigItemEntityInfos(key, default))