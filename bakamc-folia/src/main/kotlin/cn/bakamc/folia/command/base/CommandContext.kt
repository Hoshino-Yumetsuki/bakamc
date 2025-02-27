package cn.bakamc.folia.command.base

import cn.bakamc.folia.extension.toServerPlayer
import cn.bakamc.folia.util.formatText
import cn.bakamc.folia.util.sendMessage
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandContext<S : CommandSender>
internal constructor(
    val sender: S,
    val root: String,
    val args: List<String>
) {

    var level: Int = -1
        private set

    val commandChain: MutableSet<CommandNode> = mutableSetOf()

    /**
     * 正在输入的节点
     */
    val building: String
        get() = args.last()

    fun next(currentNode: CommandNode): CommandContext<S> {
        level++
        if (commandChain.isNotEmpty()) {
            if (commandChain.last() != currentNode)
                commandChain.add(currentNode)
        } else commandChain.add(currentNode)
        return this
    }

    fun currentNodeString(): String {
        return if (level > 0)
            args[level - 1]
        else root
    }

    fun nextNodeString(): String? {
        return runCatching { args[level] }.getOrNull()
    }

    inline fun hasNext(action: CommandContext<S>.(String) -> Unit): CommandContext<S> {
        if (level < args.size) action(this, nextNodeString()!!)
        return this
    }

    inline fun isEnd(action: CommandContext<S>.(String) -> Unit): CommandContext<S> {
        if (level >= args.size) action(this, currentNodeString())
        return this
    }

    val commandLine: String
        get() {
            return buildString {
                append("/")
                commandChain.forEachIndexed { index, it ->
                    append(if (it is ArgumentCommandSubNode) "<${it.command}>" else it.command)
                    if (index != commandChain.size - 1) append(" ")
                }
            }
        }

    private val arguments: MutableMap<String, String> = mutableMapOf()

    val player: ServerPlayer?
        get() = (sender as? Player)?.toServerPlayer()

    fun putArg(key: String, value: String) {
        arguments[key] = value
    }

    fun getArg(key: String): String? {
        return arguments[key]

    }

    fun <T> getArg(key: String, map: (String) -> T): T? {
        return arguments[key]?.let(map)
    }

    fun success(message: String, vararg params: Any) {
        feedback(message, style = Style.EMPTY.applyFormat(ChatFormatting.GREEN), *params)
    }

    fun fail(message: String, vararg params: Any) {
        feedback(message, style = Style.EMPTY.applyFormat(ChatFormatting.RED), *params)
    }

    fun info(message: String, vararg params: Any) {
        feedback(message, style = Style.EMPTY.applyFormat(ChatFormatting.GOLD), *params)
    }

    fun feedback(text: String, style: Style = Style.EMPTY, vararg params: Any) {
        feedback(formatText(text, style, *params))
    }

    fun feedback(text: String, vararg params: Any) {
        feedback(formatText(text, *params))
    }

    fun feedback(message: Component) {
        when (sender) {
            is Player -> player!!.sendSystemMessage(message)
            else      -> sender.sendMessage(message)
        }
    }
}