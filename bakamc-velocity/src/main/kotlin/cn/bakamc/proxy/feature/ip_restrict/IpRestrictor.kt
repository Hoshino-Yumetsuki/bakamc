package cn.bakamc.proxy.feature.ip_restrict

import cn.bakamc.proxy.BakamcProxyInstance
import cn.bakamc.proxy.feature.ip_restrict.IpRestrictConfigs.CONNECT_LIMIT
import cn.bakamc.proxy.util.literal
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component

object IpRestrictor {

//    private val connectMapping = mutableMapOf<String, MutableSet<Player>>()

    private val allPLayers = BakamcProxyInstance.server.allServers.flatMap { it.playersConnected }

    private fun getPlayerByIp(ip: String): List<Player> {
       return allPLayers.filter { it.remoteAddress.address.hostAddress == ip }
    }

    fun onPlayerConnect(player: Player) {
        val ip = player.remoteAddress.address.hostAddress
        if(getPlayerByIp(ip).size > CONNECT_LIMIT) {
            interceptPlayer(player)
        }
    }

//    fun onPlayerDisconnect(player: Player) {
//        connectMapping[player.remoteAddress.address.hostAddress]!!.remove(player)
//    }


    private fun interceptPlayer(player: Player) {
        player.disconnect(Component.text().run {
            append(literal("同一IP地址只允许${CONNECT_LIMIT}个链接"))
            this
        }.build())
    }

}