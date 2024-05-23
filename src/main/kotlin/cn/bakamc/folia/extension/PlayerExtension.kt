package cn.bakamc.folia.extension

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.db.table.PlayerInfo
import cn.bakamc.folia.util.logger
import net.milkbowl.vault.economy.EconomyResponse
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import org.bukkit.entity.Player

val Player.info: PlayerInfo
    get() {
        return PlayerInfo {
            uuid = this@info.uuid
            name = this@info.name
        }
    }

val Player.uuid: String
    get() = this.uniqueId.toString()

val onlinePlayers: Collection<Player> get() = BakaMCPlugin.instance.server.onlinePlayers

fun Player.toServerPlayer() = MinecraftServer.getServer().playerList.getPlayer(this.uniqueId)

fun ServerPlayer.toPluginPlayer() = BakaMCPlugin.instance.server.getPlayer(this.uuid)

val Player.money: Double
    get() {
        return runCatching {
            BakaMCPlugin.instance.economy.getBalance(this)
        }.onFailure {
            logger.error("经济插件未加载!", it)
        }.getOrThrow()
    }

/**
 * Withdraws the specified amount of money from the player's account.
 *
 * @param money the amount of money to withdraw
 * @return the result of the transaction
 */
fun Player.withdraw(money: Double): EconomyResponse {
    return BakaMCPlugin.instance.economy.withdrawPlayer(this, money)
}

/**
 * Deposits the specified amount of money to the player's account.
 *
 * @param money the amount of money to deposit
 * @return the result of the deposit operation as an EconomyResponse object
 */
fun Player.deposit(money: Double): EconomyResponse {
    return BakaMCPlugin.instance.economy.depositPlayer(this, money)
}


val ServerPlayer.money: Double
    get() {
        return runCatching {
            BakaMCPlugin.instance.economy.getBalance(this.toPluginPlayer())
        }.onFailure {
            logger.error("经济插件未加载!", it)
        }.getOrThrow()
    }