package cn.bakamc.folia.config

import cn.bakamc.folia.config.item.blockInfos
import cn.bakamc.folia.event.pojo.BlockInfo
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl

object BlockConfig : ConfigContainerImpl("block") {

    val BLOCK_INFOS by blockInfos(
        "block_infos",
        mapOf(
            "西瓜" to BlockInfo(type = "minecraft:melon"),
            "主世界方块" to BlockInfo(world = "minecraft:overworld"),
        )
    )

}