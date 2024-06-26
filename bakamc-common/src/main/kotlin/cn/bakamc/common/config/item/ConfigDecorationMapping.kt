package cn.bakamc.common.config.item

import cn.bakamc.common.text.BakaText
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.toSerializeObject

class ConfigDecorationMapping(
    override val key: String,
    override val defaultValue: BakaText.DecorationMapping
) : ConfigBase<BakaText.DecorationMapping, ConfigDecorationMapping>() {

    override var configValue: BakaText.DecorationMapping = defaultValue

    override fun deserialization(serializeElement: SerializeElement) {
        configValue = serializeElement.checkType<SerializeObject, BakaText.DecorationMapping> { obj ->
            BakaText.DecorationMapping(
                obj["obfuscated"]?.asArray?.map { it.asString } ?: emptyList(),
                obj["bold"]?.asArray?.map { it.asString } ?: emptyList(),
                obj["italic"]?.asArray?.map { it.asString } ?: emptyList(),
                obj["strikethrough"]?.asArray?.map { it.asString } ?: emptyList(),
                obj["underline"]?.asArray?.map { it.asString } ?: emptyList(),
                obj["rest"]?.asArray?.map { it.asString } ?: emptyList(),
            )
        }.getOrThrow()
    }

    override fun serialization(): SerializeElement {
        return configValue.toSerializeObject()
    }

}