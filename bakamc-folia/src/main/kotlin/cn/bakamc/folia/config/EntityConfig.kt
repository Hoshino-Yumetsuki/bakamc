package cn.bakamc.folia.config

import cn.bakamc.common.config.item.stringListMap
import cn.bakamc.folia.config.item.entityInfos
import cn.bakamc.folia.event.entity.EntityInfo
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl

object EntityConfig : ConfigContainerImpl("entity") {

    val ENTITY_INFOS by entityInfos(
        "entity_infos",
        mapOf(
            "小黑" to EntityInfo(type = "minecraft:enderman"),
            "苦力怕" to EntityInfo(type = "minecraft:creeper")
        )
    )

    val CHANGE_BLOCK_MAP by stringListMap(
        "change_block_map",
        mapOf(
            "小黑" to listOf("主世界方块 -> minecraft:air")
        )
    )

    val EXPLODE_BLOCK_MAP by stringListMap(
        "explode_block_map",
        mapOf(
            "苦力怕" to listOf("西瓜")
        )
    )

}