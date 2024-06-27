package cn.bakamc.proxy.feature.white_list

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import kotlin.time.Duration.Companion.minutes

object WhiteListConfigs : ConfigContainerImpl("white_list") {

    val ENABLED by ConfigBoolean("enabled", true)

    val BIND_LIMIT by ConfigInt("bind_limit", 2)

    val BOT_ID by ConfigLong("bot_id", 1000000)

    val FORCE_UPDATE_MEMBER_CARD by ConfigBoolean("force_update_member_card", true)

    val BIND_COMMAND by ConfigString("bind_command", "bind")

    val RENAME_AFTER_BIND by ConfigBoolean("rename_after_bind", true)

    val RENAME_EXP by ConfigString("rename_exp", "#{bind[0]} #{bind[last]}")

    val VERIFY_CODE_LENGTH by ConfigInt("verify_code_length", 6)

    val VERIFY_GROUP by ConfigLong("verify_group", 12345678)

    val VERIFY_CODE_EXPIRATION_TIME by ConfigDuration("verify_code_expiration_time", 10.minutes)

    val TIPS by ConfigStringList(
        "tips", listOf(
            "&{#FF4000}你不在服务器白名单内",
            "&{#FF4000}请于10分钟内在群内发送消息: &{#FF66CC,b}bind #{verify_code}&{#FF4000} ,完成白名单验证",
        )
    )

}