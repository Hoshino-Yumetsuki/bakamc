package cn.bakamc.common.messagechannel

import java.util.regex.Pattern

interface MessageChannel {

    companion object {

        private val VALID_IDENTIFIER_REGEX: Pattern = Pattern.compile("[a-z0-9/\\-_]*")

        val BOT_CMD_CHANNEL = MessageChannel("bakamc", "bot_cmd")

        operator fun invoke(namespace: String, name: String): MessageChannel {
            check(VALID_IDENTIFIER_REGEX.matcher(namespace).matches()) {
                "namespace is not valid, must match: $VALID_IDENTIFIER_REGEX got $namespace"
            }
            check(VALID_IDENTIFIER_REGEX.matcher(name).matches()) {
                "name is not valid, must match: $VALID_IDENTIFIER_REGEX got $name"
            }
            return object : MessageChannel {
                override val namespace: String
                    get() = namespace

                override val name: String
                    get() = name
            }
        }

    }

    val namespace: String

    val name: String

    val id: String get() = "$namespace:$name"
}