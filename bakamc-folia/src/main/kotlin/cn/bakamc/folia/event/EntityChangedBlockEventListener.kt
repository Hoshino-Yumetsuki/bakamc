package cn.bakamc.folia.event

import cn.bakamc.folia.config.EntityConfig.CHANGE_BLOCK_MAP
import cn.bakamc.folia.config.EntityConfig.EXPLODE_BLOCK_MAP
import cn.bakamc.folia.event.pojo.BlockChange
import cn.bakamc.folia.event.pojo.BlockInfo
import cn.bakamc.folia.event.pojo.EntityInfo
import cn.bakamc.folia.util.Reloadable
import cn.bakamc.folia.util.logger
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent

object EntityChangedBlockEventListener : Listener, Reloadable {

    override fun reload() {
        changedCache = changedCacheSupplier()
        logger.info("重载实体更改方块缓存")
        explodeCache = explodeCacheSupplier()
        logger.info("重载实体爆炸破坏方块缓存")
    }

    private var changedCache: Map<EntityInfo, List<BlockChange>> = changedCacheSupplier()

    private var explodeCache: Map<EntityInfo, List<BlockInfo>> = explodeCacheSupplier()

    private fun changedCacheSupplier() =
        CHANGE_BLOCK_MAP.mapKeys { (key, _) ->
            EntityInfo.parse(key)
        }.mapValues { (e, b) ->
            if (e != EntityInfo.EMPTY) b.map { BlockChange.parse(it) }
            else emptyList()
        }.filter { (key, _) ->
            key != EntityInfo.EMPTY
        }

    private fun explodeCacheSupplier() = EXPLODE_BLOCK_MAP.mapKeys { (key, _) ->
        EntityInfo.parse(key)
    }.mapValues { (e, b) ->
        if (e != EntityInfo.EMPTY) b.map { BlockInfo.parse(it) }
        else emptyList()
    }.filter { (key, _) ->
        key != EntityInfo.EMPTY
    }

    private inline fun inChangedCache(entity: Entity, from: Block, to: String, action: () -> Unit) {
        changedCache.forEach { (entityInfo, blockChangeList) ->
            if (entityInfo.isMatch(entity)) {
                blockChangeList.find { it.isMatch(from, to) }?.let {
                    action()
                }
            }
        }
    }

    private inline fun inExplodeCache(entity: Entity, blocks: List<Block>, action: (Block) -> Unit) {
        explodeCache.forEach { (e, b) ->
            if (e.isMatch(entity)) {
                blocks.forEach { block ->
                    b.find { it.isMatch(block) }?.let {
                        action(block)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun entityChangedBlockEvent(event: EntityChangeBlockEvent) {
        inChangedCache(event.entity, event.block, event.to.key.toString()) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun entityExplodeEvent(event: EntityExplodeEvent) {
        val list = ArrayList<Block>()
        inExplodeCache(event.entity, event.blockList()) {
            list.add(it)
        }
        event.blockList().removeAll(list)
    }

}


