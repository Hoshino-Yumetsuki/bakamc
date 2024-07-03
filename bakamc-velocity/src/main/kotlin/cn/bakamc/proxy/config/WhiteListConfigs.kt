package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import kotlin.time.Duration.Companion.minutes

object WhiteListConfigs : ConfigContainerImpl("white_list") {

    val ENABLED by boolean("enabled", true)

    val BIND_LIMIT by int("bind_limit", 2)

    val FORCE_UPDATE_MEMBER_CARD by boolean("force_update_member_card", true)

    val BIND_COMMAND by string("bind_command", "bind")

    val RENAME_AFTER_BIND by boolean("rename_after_bind", true)

    val RENAME_EXP by string("rename_exp", "#{bind[0]} #{bind[last]}")

    val VERIFY_CODE_LENGTH by int("verify_code_length", 6)

    val VERIFY_GROUP by long("verify_group", 12345678)

    val VERIFY_CODE_EXPIRATION_TIME by duration("verify_code_expiration_time", 10.minutes)

    val TIPS by stringList(
        "tips", listOf(
            "&{#FF4000}你不在服务器白名单内",
            "&{#FF4000}请于10分钟内在群内发送消息: &{#FF66CC,b}bind #{verify_code}&{#FF4000} ,完成白名单验证",
        )
    )

}