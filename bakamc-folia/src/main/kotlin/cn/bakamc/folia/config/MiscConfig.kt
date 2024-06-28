package cn.bakamc.folia.config

import cn.bakamc.common.config.item.ConfigDecorationMapping
import cn.bakamc.common.config.item.ConfigStringLongMap
import cn.bakamc.common.text.BakaText
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigBoolean
import moe.forpleuvoir.nebula.config.item.impl.ConfigString

object MiscConfig : ConfigContainerImpl("misc") {

    val ENABLE_PLAYER_JOIN_MESSAGE by ConfigBoolean("enable_player_join_message", true)

    val ENABLE_PLAYER_QUIT_MESSAGE by ConfigBoolean("enable_player_quit_message", true)

    val ENABLE_PLAYER_INTERACT_MODIFY by ConfigBoolean("enable_player_interact_modify", true)

    val ENABLE_ANVIL_CUSTOM_RENAME by ConfigBoolean("enable_anvil_custom_rename", true)

    val SERVER_START_COMMAND by ConfigStringLongMap(
        "server_start_command", mapOf(
            "bakamc test" to 100
        )
    )

    @Suppress("SpellCheckingInspection")
    val ANVIL_RENAME_LEGACY_FORMAT_CHARS by ConfigString("anvil_rename_level", "0123456789abcdefklmonr")

    val ANVIL_RENAME_DECORATION_MAPPING by ConfigDecorationMapping(
        key = "anvil_rename_style_mapping",
        defaultValue = BakaText.DecorationMapping(
            listOf("o", "obfuscated", "OBFUSCATED"),
            listOf("b", "bold", "BOLD"),
            listOf("s", "strikethrough", "STRIKETHROUGH"),
            listOf("l", "underline", "UNDERLINE"),
            listOf("i", "italic", "ITALIC"),
            listOf("r", "rest", "RESET")
        )
    )

}