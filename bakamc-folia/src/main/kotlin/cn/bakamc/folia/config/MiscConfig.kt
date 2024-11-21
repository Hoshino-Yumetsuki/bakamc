package cn.bakamc.folia.config

import cn.bakamc.common.config.item.decorationMapping
import cn.bakamc.common.text.bakatext.modifier.DecorationMapping
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.list
import moe.forpleuvoir.nebula.config.item.impl.string
import moe.forpleuvoir.nebula.config.item.impl.stringLongMap
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import org.bukkit.event.inventory.InventoryType

object MiscConfig : ConfigContainerImpl("misc") {

    val ENABLE_PLAYER_JOIN_MESSAGE by boolean("enable_player_join_message", true)

    val ENABLE_PLAYER_QUIT_MESSAGE by boolean("enable_player_quit_message", true)

    val ENABLE_PLAYER_INTERACT_MODIFY by boolean("enable_player_interact_modify", true)

    val ENABLE_ANVIL_CUSTOM_RENAME by boolean("enable_anvil_custom_rename", true)

    val SERVER_START_COMMAND by stringLongMap(
        "server_start_command", mapOf(
            "bakamc test" to 100
        )
    )

    @Suppress("SpellCheckingInspection")
    val ANVIL_RENAME_LEGACY_FORMAT_CHARS by string("anvil_rename_legacy_format_chars", "0123456789abcdefklmonr")

    val ANVIL_RENAME_DECORATION_MAPPING by decorationMapping(
        key = "anvil_rename_style_mapping",
        defaultValue = DecorationMapping(
            obfuscated = listOf("o", "obfuscated", "OBFUSCATED"),
            bold = listOf("b", "bold", "BOLD"),
            italic = listOf("i", "italic", "ITALIC"),
            strikethrough = listOf("s", "strikethrough", "STRIKETHROUGH"),
            underline = listOf("l", "underline", "UNDERLINE"),
            rest = listOf("r", "rest", "RESET")
        )
    )

    val quick_block_use = addConfig(QuickBlockUse)

    object QuickBlockUse : ConfigContainerImpl("quick_block_use") {

        val INVENTORY_TYPE by list("inventory_type", InventoryType.entries, {
            SerializePrimitive(it.name)
        }, { serializeElement ->
            serializeElement.checkType<SerializePrimitive, InventoryType> {
                InventoryType.valueOf(it.asString)
            }.getOrThrow()
        })

    }

    val player_death_message = addConfig(PlayerDeathMessage)

    object PlayerDeathMessage : ConfigContainerImpl("player_death_message") {

        val ENABLE_DEATH_MESSAGE_OVERRIDE by boolean("enable_death_message_override", false)

        val DEATH_MESSAGE_PREFIX by string("death_message_prefix", "")

        val DEATH_MESSAGE_SUFFIX by string("death_message_suffix", "")

    }


}