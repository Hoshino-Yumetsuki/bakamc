package cn.bakamc.folia.config

import cn.bakamc.common.config.item.ConfigDecorationMapping
import cn.bakamc.common.config.item.ConfigStringLongMap
import cn.bakamc.common.text.bakatext.modifier.DecorationMapping
import moe.forpleuvoir.nebula.config.annotation.ConfigMeta
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigBoolean
import moe.forpleuvoir.nebula.config.item.impl.ConfigString

object MiscConfig : ConfigContainerImpl("misc") {

    @ConfigMeta(order = 0)
    val ENABLE_PLAYER_JOIN_MESSAGE by ConfigBoolean("enable_player_join_message", true)

    @ConfigMeta(order = 1)
    val ENABLE_PLAYER_QUIT_MESSAGE by ConfigBoolean("enable_player_quit_message", true)

    @ConfigMeta(order = 2)
    val ENABLE_PLAYER_INTERACT_MODIFY by ConfigBoolean("enable_player_interact_modify", true)

    @ConfigMeta(order = 3)
    val ENABLE_ANVIL_CUSTOM_RENAME by ConfigBoolean("enable_anvil_custom_rename", true)

    @ConfigMeta(order = 4)
    val SERVER_START_COMMAND by ConfigStringLongMap(
        "server_start_command", mapOf(
            "bakamc test" to 100
        )
    )

    @ConfigMeta(order = 5)
    @Suppress("SpellCheckingInspection")
    val ANVIL_RENAME_LEGACY_FORMAT_CHARS by ConfigString("anvil_rename_legacy_format_chars", "0123456789abcdefklmonr")

    @ConfigMeta(order = 6)
    val ANVIL_RENAME_DECORATION_MAPPING by ConfigDecorationMapping(
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

}