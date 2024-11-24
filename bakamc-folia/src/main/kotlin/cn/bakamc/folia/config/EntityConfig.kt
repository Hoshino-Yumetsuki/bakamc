package cn.bakamc.folia.config

import cn.bakamc.common.config.item.stringListMap
import cn.bakamc.folia.config.item.entityInfos
import cn.bakamc.folia.event.pojo.EntityInfo
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl

object EntityConfig : ConfigContainerImpl("entity") {

    val ENTITY_INFOS by entityInfos(
        "entity_infos",
        mapOf(
            "小黑" to EntityInfo(type = "minecraft:enderman"),
            "苦力怕" to EntityInfo(type = "minecraft:creeper"),
            "僵尸" to EntityInfo(type = "minecraft:zombie"),
            "尸壳" to EntityInfo(type = "minecraft:husk")
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

    val PICKUP_ITEM_MAP by stringListMap(
        "pickup_item_map",
        mapOf(
            "僵尸" to listOf("鸡蛋"),
            "尸壳!" to listOf("鸡蛋")
        )
    )

}