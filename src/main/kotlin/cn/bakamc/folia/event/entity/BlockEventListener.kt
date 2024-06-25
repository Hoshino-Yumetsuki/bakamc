package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.Configs.Misc.ANVIL_RENAME_LEGACY_FORMATTING
import cn.bakamc.folia.config.Configs.Misc.ANVIL_RENAME_STYLE_MAPPING
import cn.bakamc.folia.config.Configs.Misc.ENABLE_ANVIL_CUSTOM_RENAME
import cn.bakamc.folia.util.gradientColor
import cn.bakamc.folia.util.gradientHSVColor
import cn.bakamc.folia.util.literalText
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.pick
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
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
                val regex = Regex("&\\{.*?}")
                if (event.inventory.renameText != null && regex.containsMatchIn(displayName)) {
                    val modifiers = ArrayList<List<(MutableComponent) -> MutableComponent>>()
                    val text: MutableComponent = literalText("")
                    displayName.replace(regex) { result ->
                        val value = result.value
                        val list = mutableListOf<(MutableComponent) -> MutableComponent>()
                        value.substring(2, value.length - 1).split(',').forEach { s ->
                            val inverted = s.startsWith('!')
                            val cs = if (s.isNotEmpty()) inverted.pick(s.substring(1), s) else s

                            ANVIL_RENAME_STYLE_MAPPING["obfuscated"]?.let {
                                if (cs in it) {
                                    list.add { text ->
                                        text.withStyle { style -> style.withObfuscated(!inverted) }
                                    }
                                }
                            }
                            ANVIL_RENAME_STYLE_MAPPING["bold"]?.let {
                                if (cs in it) {
                                    list.add { text ->
                                        text.withStyle { style -> style.withBold(!inverted) }
                                    }
                                }
                            }
                            ANVIL_RENAME_STYLE_MAPPING["strikethrough"]?.let {
                                if (cs in it) {
                                    list.add { text ->
                                        text.withStyle { style -> style.withStrikethrough(!inverted) }
                                    }
                                }
                            }
                            ANVIL_RENAME_STYLE_MAPPING["underline"]?.let {
                                if (cs in it) {
                                    list.add { text ->
                                        text.withStyle { style -> style.withUnderlined(!inverted) }
                                    }
                                }
                            }
                            ANVIL_RENAME_STYLE_MAPPING["italic"]?.let {
                                if (cs in it) {
                                    list.add { text ->
                                        text.withStyle { style -> style.withItalic(!inverted) }
                                    }
                                }
                            }
                            ANVIL_RENAME_STYLE_MAPPING["rest"]?.let {
                                if (cs in it) {
                                    list.add { text ->
                                        text.withStyle { style ->
                                            style.withBold(false)
                                                .withObfuscated(false)
                                                .withStrikethrough(false)
                                                .withUnderlined(false)
                                                .withItalic(false)
                                                .withColor(Colors.WHITE.rgb)
                                        }
                                    }

                                }
                            }

                            if (s.startsWith("$") && s.length == 2) {
                                list.add { text ->
                                    if (s[1] in ANVIL_RENAME_LEGACY_FORMATTING) {
                                        text.withStyle { style ->
                                            style.applyFormat(ChatFormatting.getByCode(s[1])!!)
                                        }
                                    } else text
                                }
                            }
                            //&{#FF66CC}
                            if (s.matches(Regex("#[0-9A-Fa-f]{6}")) && s.length == 7) {
                                list.add { text -> text.withColor(Color(s).rgb) }
                            }
                            //&{#FF66CC->#FF88BB}
                            if (s.matches(Regex("#[0-9A-Fa-f]{6}->#[0-9A-Fa-f]{6}"))) {
                                val (_start, _end) = s.split("->")
                                val (start, end) = Color(_start) to Color(_end)
                                list.add { text ->
                                    val t = literalText("").withStyle(text.style)
                                    val content = text.string
                                    gradientColor(start, end, content.length).forEachIndexed { index, color ->
                                        t.append(literalText(content[index].toString()).withStyle { style ->
                                            style.withColor(color.rgb)
                                        })
                                    }
                                    t
                                }
                            }
                            //&{[360 100 20]}
                            if (s.matches(Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]"))) {
                                val hsv = s.substring(1, s.length - 1).split(' ').map { it.toFloat() }
                                list.add { text -> text.withColor(HSVColor(hsv[0], hsv[1] / 100f, hsv[2] / 100f).rgb) }
                            }
                            //&{[360 99.6 20]->[360 20 100]}
                            if (s.matches(Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]->\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]"))) {
                                val (_start, _end) = s.split("->").map { exp -> exp.substring(1, exp.length - 1).split(" ").map { it.toFloat() } }
                                val (start, end) = HSVColor(_start[0], _start[1] / 100, _start[2] / 100) to HSVColor(_end[0], _end[1] / 100, _end[2] / 100)
                                list.add { text ->
                                    val t = literalText("").withStyle(text.style)
                                    val content = text.string
                                    gradientHSVColor(start, end, content.length).forEachIndexed { index, color ->
                                        t.append(literalText(content[index].toString()).withStyle { style ->
                                            style.withColor(color.rgb)
                                        })
                                    }
                                    t
                                }
                            }
                        }
                        modifiers.add(list)
                        value
                    }.split(regex).toMutableList().let { list ->
                        text.append(literalText(list[0]))
                        modifiers.forEachIndexed { index, modifier ->
                            var t = literalText(list[index + 1])
                            modifier.forEach { m ->
                                t = m.invoke(t)
                            }
                            text.append(t)
                        }
                    }
                    stack.setHoverName(text)
                    event.result = CraftItemStack.asBukkitCopy(stack)
                }
            }
    }

}