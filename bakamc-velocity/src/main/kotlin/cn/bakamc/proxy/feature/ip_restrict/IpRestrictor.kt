package cn.bakamc.proxy.feature.ip_restrict

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.proxy.config.IpRestrictConfig.CONNECT_LIMIT
import cn.bakamc.proxy.config.IpRestrictConfig.INTERCEPT_MESSAGE
import cn.bakamc.proxy.config.IpRestrictConfig.WHITELIST
import com.velocitypowered.api.proxy.Player
import kotlinx.coroutines.delay
import moe.forpleuvoir.nebula.common.defaultLaunch
import net.kyori.adventure.extra.kotlin.text
import java.time.*
import java.util.*

object IpRestrictor {

    private var today = ""

    private val allOnlinePlayerUUID = mutableSetOf<UUID>()

    private val ipRecord = mutableMapOf<UUID, String>()

    fun onPlayerConnect(player: Player) {
        if (player.uniqueId.toString() in WHITELIST) return
        allOnlinePlayerUUID.add(player.uniqueId)
        if (checkDay()) onNewDay()
        val ip = player.remoteAddress.address.hostAddress

        ipRecord[player.uniqueId] = ip

        if (getUUIDByIP(ip).size > CONNECT_LIMIT) {
            interceptPlayer(player)
            ipRecord.remove(player.uniqueId)
        }
    }

    fun onPlayerDisconnect(player: Player) {
        if (player.uniqueId.toString() in WHITELIST) return
        allOnlinePlayerUUID.remove(player.uniqueId)
        if (checkDay()) onNewDay()
    }

    private fun onNewDay() {
        val removedKey = ipRecord.keys - allOnlinePlayerUUID
        for (uuid in removedKey) {
            ipRecord.remove(uuid)
        }
    }

    private fun getUUIDByIP(ip: String): Collection<UUID> {
        return ipRecord.filter { (_, value) -> value == ip }.keys
    }

    private fun checkDay(): Boolean {
        val now = LocalDate.now(ZoneId.of("Asia/Shanghai"))
        val currentDay = "${now.year}-${now.monthValue}-${now.dayOfMonth} "
        if (this.today != currentDay) {
            this.today = currentDay
            return true
        }
        return false
    }

    private fun interceptPlayer(player: Player) {
        text {
            INTERCEPT_MESSAGE.forEach { message ->
                append(BakaText.parse(message.replace("#{CONNECT_LIMIT}", "$CONNECT_LIMIT")))
                if (INTERCEPT_MESSAGE.last() != message) appendNewline()
            }
        }.let { player.disconnect(it) }
    }

    suspend fun test() {
        // 这将在每天的12:00PM(或你选择的任何其他时间)启动你的任务
        val timeToStartAt = LocalTime.of(12, 0)

        while (true) {
            val now = LocalDateTime.now()
            val nextRun = now.withHour(timeToStartAt.hour).withMinute(timeToStartAt.minute)

            if (now.isAfter(nextRun)) {
                // 如果当前时间已经过去了我们设定的时间，则我们取明天的这个时间
                nextRun.plusDays(1)
            }

            // 计算需要的休眠时间，以毫秒为单位
            val delay = Duration.between(now, nextRun).toMillis()

            // 延迟指定的时间，以便下一次循环正好在我们设定的时间启动
            delay(delay)

            // 开启一个新的协程
            defaultLaunch {
                // 这里是你的任务
            }
        }
    }

}