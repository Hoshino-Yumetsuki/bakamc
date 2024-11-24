package cn.bakamc.folia.config

import cn.bakamc.folia.config.item.itemEntityInfos
import cn.bakamc.folia.event.pojo.ItemEntityInfo
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl

object ItemEntityConfig : ConfigContainerImpl("item_entity") {

    val ITEM_ENTITY_INFO by itemEntityInfos(
        "item_entity_info",
        mapOf(
            "鸡蛋" to ItemEntityInfo(type = "minecraft:egg", count = 1),
        )
    )
}