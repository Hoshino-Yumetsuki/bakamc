package cn.bakamc.common

import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

interface ServerInfo {

    companion object : Deserializer<ServerInfo>, Serializer<ServerInfo> {
        operator fun invoke(name: String): ServerInfo = object : ServerInfo {
            override val serverName: String get() = name
        }

        override fun deserialization(serializeElement: SerializeElement): ServerInfo {
            return serializeElement.checkType<SerializeObject, ServerInfo> {
                ServerInfo(
                    it["server_name"]!!.asString
                )
            }.getOrThrow()
        }

        override fun serialization(target: ServerInfo): SerializeElement {
            return serializeObject {
                "server_name" to target.serverName
            }
        }
    }

    val serverName: String

    infix fun isEquals(other: ServerInfo): Boolean {
        return serverName == other.serverName
    }

}