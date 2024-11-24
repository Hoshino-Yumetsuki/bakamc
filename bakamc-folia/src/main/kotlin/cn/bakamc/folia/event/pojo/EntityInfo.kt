package cn.bakamc.folia.event.pojo

import cn.bakamc.folia.config.EntityConfig
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import org.bukkit.entity.Entity

data class EntityInfo(
    val name: String? = null,
    val uuid: String? = null,
    val type: String? = null,
) : Serializable {

    fun isMatch(target: Entity): Boolean {
        if (this == EMPTY) return false
        return name?.let { target.name == it } != false
                && uuid?.let { target.uniqueId.toString() == it } != false
                && type?.let { target.type.key.toString() == it } != false
    }

    override fun serialization(): SerializeElement {
        return serializeObject {
            name?.let { "name" - it }
            uuid?.let { "uuid" - it }
            type?.let { "type" - it }
        }
    }

    companion object : Deserializer<EntityInfo> {

        val EMPTY by lazy { EntityInfo() }

        fun parse(string: String): EntityInfo {
            return EntityConfig.ENTITY_INFOS[string] ?: if (string.isNotEmpty()) EntityInfo(type = string) else EMPTY
        }

        override fun deserialization(serializeElement: SerializeElement): EntityInfo {
            return serializeElement.checkType<SerializeObject, EntityInfo> { obj ->
                EntityInfo(
                    obj["name"]?.asString,
                    obj["uuid"]?.asString,
                    obj["type"]?.asString,
                )
            }.getOrThrow()
        }
    }

}