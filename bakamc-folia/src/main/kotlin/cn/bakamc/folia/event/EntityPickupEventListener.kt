package cn.bakamc.folia.event

import cn.bakamc.folia.config.EntityConfig.PICKUP_ITEM_MAP
import cn.bakamc.folia.event.pojo.EntityInfo
import cn.bakamc.folia.event.pojo.ItemEntityInfo
import cn.bakamc.folia.util.Reloadable
import cn.bakamc.folia.util.logger
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

object EntityPickupEventListener : Listener, Reloadable {

    override fun reload() {
        pickupItemCache = pickupItemCacheSupplier()
        logger.info("重载实体拾取物品缓存")
    }

    private var pickupItemCache: Map<Pair<EntityInfo, Boolean>, List<ItemEntityInfo>> = pickupItemCacheSupplier()

    private fun pickupItemCacheSupplier(): Map<Pair<EntityInfo, Boolean>, List<ItemEntityInfo>> =
        PICKUP_ITEM_MAP.mapKeys { (key, _) ->
            // 如果 key 字符串以感叹号`!`结尾，则移除最后的感叹号，否则保留原字符串
            val isBlackList = key.endsWith("!")
            val k = if (isBlackList) key.substring(0, key.length - 1) else key
            EntityInfo.parse(k) to isBlackList
        }.mapValues { (e, i) ->
            if (e != EntityInfo.EMPTY)
                i.map { ItemEntityInfo.parse(it) }
            else
                emptyList()
        }.filter { (key, _) ->
            key.first != EntityInfo.EMPTY
        }

    /**
     * 返回true则可以收取该物品
     */
    private fun inPickupItemCache(entity: Entity, item: Item): Boolean {
        pickupItemCache.forEach { (e, itemInfoList) ->
            val (entityInfo, isBlackList) = e
            if (entityInfo.isMatch(entity)) {
                itemInfoList.find { it.isMatch(item) }?.let {
                    return !isBlackList
                }
                return isBlackList
            }
        }
        return true
    }


    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        val entity = event.entity
        val item = event.item
        val canPickup = inPickupItemCache(entity, item)
        if (!canPickup) {
            event.isCancelled = true
        }
    }

}