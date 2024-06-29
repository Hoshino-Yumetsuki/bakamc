package cn.bakamc.folia.event.entity

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.common.text.bakatext.modifier.ColorModifier
import cn.bakamc.common.text.bakatext.modifier.DecorationModifier
import cn.bakamc.common.text.bakatext.modifier.LegacyChatFormattingModifier
import cn.bakamc.folia.config.MiscConfig.ANVIL_RENAME_DECORATION_MAPPING
import cn.bakamc.folia.config.MiscConfig.ANVIL_RENAME_LEGACY_FORMAT_CHARS
import cn.bakamc.folia.config.MiscConfig.ENABLE_ANVIL_CUSTOM_RENAME
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent

object BlockEventListener : Listener {

    @EventHandler
    fun onAnvilRename(event: PrepareAnvilEvent) {
        if (ENABLE_ANVIL_CUSTOM_RENAME)
            event.result?.let { itemStack ->
                event.inventory.renameText?.let { renameText ->
                    if (!BakaText.regex.containsMatchIn(renameText)) return
                    val meta = itemStack.itemMeta
                    val text = BakaText.parse(
                        renameText, listOf(
                            DecorationModifier(ANVIL_RENAME_DECORATION_MAPPING),
                            ColorModifier,
                            LegacyChatFormattingModifier(formatChars = ANVIL_RENAME_LEGACY_FORMAT_CHARS.toSet())
                        )
                    )
                    meta.displayName(text)
                    event.result!!.itemMeta = meta
                }
            }
    }

}