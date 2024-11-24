package cn.bakamc.folia.event.pojo

import cn.bakamc.folia.config.BlockConfig
import org.bukkit.block.Block

data class BlockChange(
    val from: BlockInfo? = null,
    val to: BlockInfo? = null,
) {
    companion object {

        val EMPTY by lazy { BlockChange() }

        private const val SYMBOL = "->"

        fun parse(string: String): BlockChange {
            if (string.contains(SYMBOL)) {
                string.replace(" ", "").split(SYMBOL).apply {
                    val from = if (this[0].isNotEmpty()) BlockConfig.BLOCK_INFOS[this[0]] ?: BlockInfo(type = this[0]) else null
                    val to = if (this[1].isNotEmpty()) BlockConfig.BLOCK_INFOS[this[1]] ?: BlockInfo(type = this[1]) else null
                    return BlockChange(from, to)
                }
            } else {
                return if (string.isNotEmpty()) BlockChange(BlockConfig.BLOCK_INFOS[string] ?: BlockInfo(type = string)) else EMPTY
            }
        }
    }

    fun isMatch(from: Block, to: String): Boolean {
        var result = false
        if (this.from != null) result = this.from.isMatch(from)
        if (this.to != null) result = (this.to.type == to) && result
        return result
    }

}