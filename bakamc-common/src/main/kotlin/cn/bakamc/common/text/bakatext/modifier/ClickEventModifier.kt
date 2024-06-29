package cn.bakamc.common.text.bakatext.modifier

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent

object ClickEventModifier : Modifier {

    private val pattern = """click=>(run_command|suggest_command|open_url|open_file|change_page|copy_to_clipboard)=>.+""".toRegex()
    override fun modifier(exp: String): ((TextComponent) -> TextComponent)? {
        if (!exp.matches(pattern)) return null
        val s = exp.split("=>")
        val action = s[1]
        val value = s[2]
        return { text ->
            when (action) {
                "run_command"       -> ClickEvent.runCommand(value)
                "suggest_command"   -> ClickEvent.suggestCommand(value)
                "open_url"          -> ClickEvent.openUrl(value)
                "open_file"         -> ClickEvent.openFile(value)
                "change_page"       -> ClickEvent.changePage(value)
                "copy_to_clipboard" -> ClickEvent.copyToClipboard(value)
                else                -> null
            }?.let {
                text.clickEvent(it)
            } ?: text
        }
    }

}