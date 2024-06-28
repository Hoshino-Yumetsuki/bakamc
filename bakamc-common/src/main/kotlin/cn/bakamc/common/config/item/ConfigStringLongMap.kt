package cn.bakamc.common.config.item

import moe.forpleuvoir.nebula.common.util.NotifiableLinkedHashMap
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.item.ConfigMutableMapValue
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

class ConfigStringLongMap(
    override val key: String,
    defaultValue: Map<String, Long>
) : ConfigBase<MutableMap<String, Long>, ConfigStringLongMap>(), ConfigMutableMapValue<String, Long> {

    override val defaultValue: MutableMap<String, Long> = LinkedHashMap(defaultValue)

    override var configValue: MutableMap<String, Long> = map(this.defaultValue)

    private fun map(map: Map<String, Long>): NotifiableLinkedHashMap<String, Long> {
        return NotifiableLinkedHashMap(map).apply {
            subscribe {
                this@ConfigStringLongMap.onChange(this@ConfigStringLongMap)
            }
        }
    }

    override fun restDefault() {
        if (isDefault()) return
        configValue = map(defaultValue)
        onChange(this)
    }

    override fun serialization(): SerializeElement =
        serializeObject(configValue)

    override fun deserialization(serializeElement: SerializeElement) {
        configValue = serializeElement.checkType {
            check<SerializeObject> {
                this@ConfigStringLongMap.map(it.mapValues { (_, value) ->
                    value.asLong
                })
            }
        }.getOrThrow()
    }
}