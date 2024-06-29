package cn.bakamc.proxy.feature.white_list

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.proxy.BakamcProxyInstance
import cn.bakamc.proxy.config.WhiteListConfigs.BIND_LIMIT
import cn.bakamc.proxy.config.WhiteListConfigs.TIPS
import cn.bakamc.proxy.config.WhiteListConfigs.VERIFY_CODE_EXPIRATION_TIME
import cn.bakamc.proxy.config.WhiteListConfigs.VERIFY_CODE_LENGTH
import cn.bakamc.proxy.database.database
import cn.bakamc.proxy.database.table.playerInfos
import cn.bakamc.proxy.services.PlayerServices
import cn.bakamc.proxy.services.PlayerServices.insertOrUpdatePlayer
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.proxy.Player
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.notEq
import org.ktorm.entity.find
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

object WhiteListManager {

    private val chars = ('A'..'Z') + ('0'..'9')

    private var today = ""

    private fun generate(length: Int): String {
        val now = LocalDate.now(ZoneId.of("Asia/Shanghai"))
        val currentDay = "${now.year}-${now.monthValue}-${now.dayOfMonth} "
        if (now.dayOfWeek == DayOfWeek.THURSDAY && this.today != currentDay) {
            val code = when (length) {
                6    -> "KFCV50"
                8    -> "KFCVME50"
                else -> ""
            }
            if (code.isNotEmpty() && Random.nextFloat() < 0.25f) {
                this.today = currentDay
                return if (verifyCodes.containsKey(code)) generate(length) else code
            }
        }
        val code = (1..length)
            .map { chars.random() }
            .joinToString("")
        return if (verifyCodes.containsKey(code)) generate(length) else code
    }

    private val verifyCodes = ConcurrentHashMap<String, Player>()

    fun onPlayerJoin(event: ServerPreConnectEvent) {
        runBlocking {
            insertOrUpdatePlayer(event.player)
            if (!verify(event.player)) {
                val code = generateVerifyCode(event.player)
                interceptPlayer(event.player, code)
            }
        }
    }

    suspend fun onMemberQuit(qq: Long) {
        database {
            playerInfos.find { it.qq eq qq }?.let { info ->
                BakamcProxyInstance.server.getPlayer(UUID.fromString(info.uuid)).ifPresent { player -> player.disconnect(Component.text("你已不在白名单中")) }
            }
        }
        PlayerServices.playerQuitGroup(qq)
    }

    suspend fun bind(verifyCode: String, qq: Long): Pair<String, List<String>?> {
        verifyCodes[verifyCode]?.let { player ->
            if (PlayerServices.getBindCount(qq) >= BIND_LIMIT) {
                return "当前QQ已达到绑定上限" to null
            }
            if (PlayerServices.bindQQ(player.uniqueId, qq)) {
                verifyCodes.remove(verifyCode)
                return "绑定成功[${player.username}(${player.uniqueId})]" to PlayerServices.getBindPlayers(qq).map { it.name }
            }
        }
        return "不存在的验证码" to null
    }

    private fun generateVerifyCode(player: Player): String {
        return generate(VERIFY_CODE_LENGTH).apply {
            var removedKey: String? = null
            verifyCodes.forEach { (key, value) ->
                if (value == player) {
                    removedKey = key
                }
            }
            removedKey?.let { verifyCodes.remove(it) }
            verifyCodes[this] = player
            BakamcProxyInstance.runDelayed(VERIFY_CODE_EXPIRATION_TIME) {
                verifyCodes.remove(this)
            }
        }
    }

    private suspend fun verify(player: Player): Boolean {
        return database {
            playerInfos.find { (it.uuid eq player.uniqueId.toString()) and (it.qq notEq 0) } != null
        }
    }

    private fun interceptPlayer(player: Player, verifyCode: String) {
        player.disconnect(Component.text().run {
            TIPS.withIndex().forEach { (index, tip) ->
                append(BakaText.parse(tip.replace("#{verify_code}", verifyCode)))
                if (index != TIPS.lastIndex) appendNewline()
            }
            this
        }.build())
    }


}