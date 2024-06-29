package cn.bakamc.folia.config

import cn.bakamc.common.config.item.ConfigStringListMap
import cn.bakamc.folia.config.item.ConfigEntityInfos
import cn.bakamc.folia.event.entity.EntityInfo
import moe.forpleuvoir.nebula.config.annotation.ConfigMeta
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl

object EntityConfig : ConfigContainerImpl("entity") {

    @ConfigMeta(order = 0)
    val ENTITY_INFOS by ConfigEntityInfos(
        "entity_infos",
        mapOf(
            "小黑" to EntityInfo(type = "minecraft:enderman"),
            "苦力怕" to EntityInfo(type = "minecraft:creeper")
        )
    )

    @ConfigMeta(order = 1)
    val CHANGE_BLOCK_MAP by ConfigStringListMap(
        "change_block_map",
        mapOf(
            "小黑" to listOf("主世界方块 -> minecraft:air")
        )
    )

    @ConfigMeta(order = 2)
    val EXPLODE_BLOCK_MAP by ConfigStringListMap(
        "explode_block_map",
        mapOf(
            "苦力怕" to listOf("西瓜")
        )
    )

}