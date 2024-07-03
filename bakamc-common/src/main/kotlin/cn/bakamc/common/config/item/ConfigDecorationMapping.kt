package cn.bakamc.common.config.item

import cn.bakamc.common.text.bakatext.modifier.DecorationMapping
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.toSerializeObject

class ConfigDecorationMapping(
    override val key: String,
    override val defaultValue: DecorationMapping
) : ConfigBase<DecorationMapping, ConfigDecorationMapping>() {

    override var configValue: DecorationMapping = defaultValue

    override fun deserialization(serializeElement: SerializeElement) {
        configValue = serializeElement.checkType<SerializeObject, DecorationMapping> { obj ->
            DecorationMapping(
                obfuscated = obj["obfuscated"]?.asArray?.map { it.asString } ?: emptyList(),
                bold = obj["bold"]?.asArray?.map { it.asString } ?: emptyList(),
                italic = obj["italic"]?.asArray?.map { it.asString } ?: emptyList(),
                strikethrough = obj["strikethrough"]?.asArray?.map { it.asString } ?: emptyList(),
                underline = obj["underline"]?.asArray?.map { it.asString } ?: emptyList(),
                rest = obj["rest"]?.asArray?.map { it.asString } ?: emptyList(),
            )
        }.getOrThrow()
    }

    override fun serialization(): SerializeElement {
        return configValue.toSerializeObject()
    }

}

fun ConfigContainer.decorationMapping(key: String, defaultValue: DecorationMapping) = addConfig(ConfigDecorationMapping(key, defaultValue))