package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.string
import moe.forpleuvoir.nebula.config.item.impl.stringMap

object MiscConfig : ConfigContainerImpl("misc") {

    val SERVER_NAME_MAPPING by stringMap(
        "server_name_mapping", mapOf(
            "s1" to "生存服",
            "s2" to "建筑服"
        )
    )

    val SERVER_INFO by string(
        "server_info",
        "&{#E76E42,hover=>服务器ID:#{server_id}\\n服务器名:#{server_name}\\n点击传送至此服务器,click=>suggest_command=>/server #{server_id}}#{server_name}"
    )

    val PLAYER_INFO by string(
        "player_info",
        "&{#01E9E8,hover=>#{player_name}\\n#{player_uuid}\\n点击传送传送到Ta身边,click=>suggest_command=>/tpa #{player_name}}#{player_name}"
    )

    val PLAYER_JOIN_MESSAGE by string(
        "player_join_message",
        "#{player_info} &{#4EE983}已加入#{server_info}"
    )

    val PLAYER_QUIT_MESSAGE by string(
        "player_quit_message",
        "#{player_info} &{#E76E42}已退出服务器"
    )

}