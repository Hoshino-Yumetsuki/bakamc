package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.Configs.Misc.ANVIL_RENAME_STYLE_MAPPING
import cn.bakamc.folia.config.Configs.Misc.ENABLE_ANVIL_CUSTOM_RENAME
import cn.bakamc.folia.util.literalText
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.pick
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent

object BlockEventListener : Listener {

    @EventHandler
    fun onAnvilRename(event: PrepareAnvilEvent) {
        if (ENABLE_ANVIL_CUSTOM_RENAME)
            event.result?.let { itemStack ->
                val stack = CraftItemStack.asNMSCopy(itemStack)
                val displayName = stack.hoverName.string
                val styles = ArrayList<List<(Style) -> Style>>()
                val regex = Regex("&\\{.*?}")
                val text: MutableComponent = literalText("")
                displayName.replace(regex) { result ->
                    val value = result.value
                    val list = mutableListOf<(Style) -> Style>()
                    value.substring(2, value.length - 1).split(',').forEach { s ->
                        val inverted = s.startsWith('!')
                        val cs = if (s.isNotEmpty()) inverted.pick(s.substring(1), s) else s

                        ANVIL_RENAME_STYLE_MAPPING["obfuscated"]?.let {
                            if (cs in it) {
                                list.add { style -> style.withObfuscated(!inverted) }
                            }
                        }
                        ANVIL_RENAME_STYLE_MAPPING["bold"]?.let {
                            if (cs in it) {
                                list.add { style -> style.withBold(!inverted) }
                            }
                        }
                        ANVIL_RENAME_STYLE_MAPPING["strikethrough"]?.let {
                            if (cs in it) {
                                list.add { style -> style.withStrikethrough(!inverted) }
                            }
                        }
                        ANVIL_RENAME_STYLE_MAPPING["underline"]?.let {
                            if (cs in it) {
                                list.add { style -> style.withUnderlined(!inverted) }
                            }
                        }
                        ANVIL_RENAME_STYLE_MAPPING["italic"]?.let {
                            if (cs in it) {
                                list.add { style -> style.withItalic(!inverted) }
                            }
                        }
                        ANVIL_RENAME_STYLE_MAPPING["rest"]?.let {
                            if (cs in it) {
                                list.add { style -> style.withBold(false).withStrikethrough(false).withUnderlined(false).withItalic(false).withColor(Colors.WHITE.rgb) }
                            }
                        }

                        if (s.matches(Regex("#[0-9A-Fa-f]{6}")) && s.length == 7) {
                            list.add { style -> style.withColor(Color(s).rgb) }
                        }
                    }
                    styles.add(list)
                    value
                }.split(regex).toMutableList().let { list ->
                    text.append(literalText(list[0]))
                    styles.forEachIndexed { index, styles ->
                        text.append(literalText(list[index + 1]).withStyle { s ->
                            var s1 = s
                            styles.forEach { s2 ->
                                s1 = s2(s1)
                            }
                            s1
                        })
                    }

                }
                stack.setHoverName(text)
                event.result = CraftItemStack.asBukkitCopy(stack)
            }
    }


}