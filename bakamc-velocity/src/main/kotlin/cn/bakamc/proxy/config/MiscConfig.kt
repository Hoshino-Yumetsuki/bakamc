package cn.bakamc.proxy.config

import moe.forpleuvoir.nebula.config.annotation.ConfigMeta
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigString
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringMap

object MiscConfig : ConfigContainerImpl("misc") {

    @ConfigMeta(order = 0)
    val SERVER_NAME_MAPPING by ConfigStringMap(
        "server_name_mapping", mapOf(
            "s1" to "生存服",
            "s2" to "建筑服"
        )
    )

    @ConfigMeta(order = 1)
    val SERVER_INFO by ConfigString(
        "server_info",
        "&{#E76E42,hover=>服务器ID:#{server_id}\\n服务器名:#{server_name}\\n点击传送至此服务器,click=>suggest_command=>/server #{server_id}}#{server_name}"
    )


    @ConfigMeta(order = 2)
    val PLAYER_INFO by ConfigString(
        "player_info",
        "&{#01E9E8,hover=>#{player_name}\\n#{player_uuid}\\n点击传送传送到Ta身边,click=>suggest_command=>/tpa #{player_name}}#{player_name}"
    )

    @ConfigMeta(order = 3)
    val PLAYER_JOIN_MESSAGE by ConfigString(
        "player_join_message",
        "#{player_info} &{#4EE983}已加入#{server_info}"
    )

    @ConfigMeta(order = 3)
    val PLAYER_QUIT_MESSAGE by ConfigString(
        "player_quit_message",
        "#{player_info} &{#E76E42}已退出服务器"
    )

}