package cn.bakamc.folia.config.item

import cn.bakamc.folia.event.entity.BlockInfo
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl

object BlockConfig : ConfigContainerImpl("block") {

    val BLOCK_INFOS by ConfigBlockInfos(
        "block_infos",
        mapOf(
            "西瓜" to BlockInfo(type = "minecraft:melon"),
            "主世界方块" to BlockInfo(world = "minecraft:overworld"),
        )
    )

}