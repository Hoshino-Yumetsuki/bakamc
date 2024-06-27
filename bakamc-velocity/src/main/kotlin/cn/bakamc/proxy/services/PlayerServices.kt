package cn.bakamc.proxy.services

import cn.bakamc.proxy.database.database
import cn.bakamc.proxy.database.table.PlayerInfo
import cn.bakamc.proxy.database.table.info
import cn.bakamc.proxy.database.table.playerInfos
import com.velocitypowered.api.proxy.Player
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import java.util.*

object PlayerServices {

    suspend fun insertOrUpdatePlayer(player: Player): Boolean {
        return database {
            playerInfos.find { it.uuid eq player.uniqueId.toString() }?.let { playerInfo ->
                return@database playerInfos.update(player.info.apply { qq = playerInfo.qq }) > 0
            }
            return@database playerInfos.add(player.info) > 0
        }
    }

    suspend fun playerQuitGroup(qq: Long): Int {
        return database {
            playerInfos.removeIf { it.qq eq qq }
        }
    }

    suspend fun bindQQ(playerUUID: UUID, qq: Long): Boolean {
        return database {
            playerInfos.find { it.uuid eq playerUUID.toString() }?.let { playerInfo ->
                playerInfo.qq = qq
                playerInfo.flushChanges()
                return@database true
            }
            return@database false
        }
    }

    suspend fun getBindQQ(playerUUID: UUID): Long? {
        return database {
            playerInfos.find { it.uuid eq playerUUID.toString() }?.qq?.let { if (it == 0L) null else it }
        }
    }

    suspend fun getBindPlayers(qq: Long): List<PlayerInfo> {
        return database {
            playerInfos.filter {
                it.qq eq qq
            }.toList()
        }
    }

    suspend fun getBindCount(qq: Long): Int {
        return database {
            playerInfos.count {
                it.qq eq qq
            }
        }
    }

}