package cn.bakamc.folia.event.pojo

import cn.bakamc.folia.config.ItemEntityConfig
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import org.bukkit.entity.Item


data class ItemEntityInfo(
    val name: String? = null,
    val type: String? = null,
    val count: Int? = null,
) : Serializable {

    fun isMatch(target: Item): Boolean {
        if (this == EMPTY) return false
        return this.name?.let { target.name == it } != false
                && this.type?.let { target.itemStack.type.key.toString() == it } != false
                && this.count?.let { target.itemStack.amount == it } != false

    }

    override fun serialization(): SerializeElement =
        serializeObject {
            name?.let { "name" - it }
            type?.let { "type" - it }
            count?.let { "count" - it }
        }

    companion object : Deserializer<ItemEntityInfo> {

        val EMPTY by lazy { ItemEntityInfo() }

        fun parse(string: String): ItemEntityInfo {
            return ItemEntityConfig.ITEM_ENTITY_INFO[string] ?: if (string.isNotEmpty()) ItemEntityInfo(type = string) else EMPTY
        }

        override fun deserialization(serializeElement: SerializeElement): ItemEntityInfo {
            return serializeElement.checkType<SerializeObject, ItemEntityInfo> { obj ->
                ItemEntityInfo(
                    obj["name"]?.asString,
                    obj["type"]?.asString,
                    obj["count"]?.asInt,
                )
            }.getOrThrow()
        }
    }

}
