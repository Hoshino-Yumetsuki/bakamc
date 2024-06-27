package cn.bakamc.proxy.database.table

import com.velocitypowered.api.proxy.Player
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

open class PlayerInfos(alias: String?) : Table<PlayerInfo>("player_bind", alias) {
    companion object : PlayerInfos(null)

    override fun aliased(alias: String): Table<PlayerInfo> = PlayerInfos(alias)

    val uuid = varchar("uuid").primaryKey().bindTo { it.uuid }

    val name = varchar("name").bindTo { it.name }

    val qq = long("qq").bindTo { it.qq }

}

interface PlayerInfo : Entity<PlayerInfo> {
    companion object : Entity.Factory<PlayerInfo>()

    var uuid: String

    var name: String

    var qq: Long

}

val Database.playerInfos get() = this.sequenceOf(PlayerInfos)

val Player.info: PlayerInfo
    get() = PlayerInfo {
        uuid = this@info.uniqueId.toString()
        name = this@info.username
        qq = 0
    }
