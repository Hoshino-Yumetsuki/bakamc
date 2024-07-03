package cn.bakamc.common.messagechannel

import cn.bakamc.common.ServerInfo
import moe.forpleuvoir.nebula.common.api.ExperimentalApi
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import moe.forpleuvoir.nebula.serialization.json.JsonParser
import moe.forpleuvoir.nebula.serialization.json.JsonSerializer.Companion.dumpAsJson

class MessageData(
    val messageConsumer: MessageConsumer,
    val data: ByteArray,
) {

    companion object {
        private val charset = Charsets.UTF_8

        @OptIn(ExperimentalApi::class)
        fun fromBytes(bytes: ByteArray): MessageData {
            return JsonParser.parse(bytes.toString(charset)).let { serializeElement ->
                serializeElement.checkType<SerializeObject, MessageData> {
                    val consumer = MessageConsumer.deserialization(it["consumer"]!!)
                    val data = it["data"]!!.asString.toByteArray(charset)
                    MessageData(consumer, data)
                }
            }.getOrThrow()
        }
    }

    @OptIn(ExperimentalApi::class)
    fun toBytes(): ByteArray {
        return serializeObject {
            "consumer" to messageConsumer
            "data" to data.toString(charset)
        }.dumpAsJson().toByteArray(charset)
    }

    fun canConsume(severInfo: ServerInfo): Boolean = messageConsumer.canConsume(severInfo)

    inline fun tryConsume(severInfo: ServerInfo, block: () -> Unit) {
        if (canConsume(severInfo)) block.invoke()
    }

}

sealed interface MessageConsumer : Serializable {

    companion object : Deserializer<MessageConsumer> {
        override fun deserialization(serializeElement: SerializeElement): MessageConsumer {
            return serializeElement.checkType {
                check<SerializePrimitive> {
                    if (it.asString == "Broadcast") Broadcast
                    throw IllegalArgumentException("Expected 'Broadcast' value but got something else")
                }
                check<SerializeObject> {
                    ServerConsumer(ServerInfo.deserialization(it))
                }
            }.getOrThrow()
        }

    }

    fun canConsume(severInfo: ServerInfo): Boolean


}

inline fun MessageConsumer.tryConsume(severInfo: ServerInfo, block: () -> Unit) {
    if (canConsume(severInfo)) block.invoke()
}

data object Broadcast : MessageConsumer {
    override fun canConsume(severInfo: ServerInfo): Boolean {
        return true
    }

    override fun serialization(): SerializeElement = SerializePrimitive("Broadcast")

}

data class ServerConsumer(
    val serverInfo: ServerInfo
) : MessageConsumer {
    override fun canConsume(severInfo: ServerInfo): Boolean {
        return severInfo isEquals this.serverInfo
    }

    override fun serialization(): SerializeElement = ServerInfo.serialization(serverInfo)

}

