package cn.bakamc.folia.config

import cn.bakamc.common.config.component.backup
import cn.bakamc.common.config.component.generateTemp
import cn.bakamc.common.config.item.ConfigDecorationMapping
import cn.bakamc.common.config.item.ConfigStringDoubleMap
import cn.bakamc.common.config.item.ConfigStringListMap
import cn.bakamc.common.config.item.ConfigStringLongMap
import cn.bakamc.common.text.BakaText
import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.config.item.*
import cn.bakamc.folia.event.entity.BlockInfo
import cn.bakamc.folia.event.entity.EntityInfo
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import moe.forpleuvoir.nebula.config.manager.ConfigManagerImpl
import moe.forpleuvoir.nebula.config.manager.component.localConfig
import moe.forpleuvoir.nebula.config.manager.components
import moe.forpleuvoir.nebula.config.persistence.jsonPersistence
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import java.nio.file.Path
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object Configs : ConfigManagerImpl(BakaMCPlugin.instance.bakaName) {

    lateinit var configPath: Path
        internal set

    /**
     * 配置文件路径
     * @param path Path
     */
    suspend fun step(path: Path) {
        configPath = path

        components {
            localConfig(configPath, jsonPersistence())
            backup(configPath, logger)
            generateTemp(configPath, jsonPersistence(), logger)
        }

        init()
        runCatching {
            load()
            if (this.needSave) {
                save()
            }
        }.onFailure {
            logger.warn(it.message, it)
            forceSave()
        }
    }

    object Misc : ConfigContainerImpl("misc") {

        val ENABLE_PLAYER_JOIN_MESSAGE by ConfigBoolean("enable_player_join_message", true)

        val ENABLE_PLAYER_QUIT_MESSAGE by ConfigBoolean("enable_player_quit_message", true)

        val ENABLE_ANVIL_CUSTOM_RENAME by ConfigBoolean("enable_anvil_custom_rename", true)

        val SERVER_START_COMMAND by ConfigStringLongMap(
            "server_start_command", mapOf(
                "bakamc test" to 100
            )
        )

        @Suppress("SpellCheckingInspection")
        val ANVIL_RENAME_LEGACY_FORMAT_CHARS by ConfigString("anvil_rename_level", "0123456789abcdefklmonr")

        val ANVIL_RENAME_DECORATION_MAPPING by ConfigDecorationMapping(
            key = "anvil_rename_style_mapping",
            defaultValue = BakaText.DecorationMapping(
                listOf("o", "obfuscated", "OBFUSCATED"),
                listOf("b", "bold", "BOLD"),
                listOf("s", "strikethrough", "STRIKETHROUGH"),
                listOf("l", "underline", "UNDERLINE"),
                listOf("i", "italic", "ITALIC"),
                listOf("r", "rest", "RESET")
            )
        )

    }

    object Database : ConfigContainerImpl("database") {

        val URL by ConfigString("url", "jdbc:mysql://localhost:3306/bakamc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")

        val USER by ConfigString("user", "root")

        val PASSWORD by ConfigString("password", "root")

        object DataSource : ConfigContainerImpl("data_source") {

            val CONNECTION_TIMEOUT by ConfigLong("connection_timeout", 60000)

            val IDLE_TIMEOUT by ConfigLong("idle_timeout", 60000)

            val MAXIMUM_POOL_SIZE by ConfigInt("maximum_pool_size", 30)

            val MAX_LIFETIME by ConfigLong("max_lifetime", 180000)

            val KEEPALIVE_TIME by ConfigLong("keepalive_time", 0)

            val MINIMUM_IDLE by ConfigInt("minimum_idle", 5)

        }

    }

    object FlightEnergy : ConfigContainerImpl("flight_energy") {

        val TICK_PERIOD by ConfigDuration("tick_period", 1.0.seconds)

        val ENERGY_COST by ConfigDouble("energy_cost", 1.0)

        val MAX_ENERGY by ConfigDouble("max_energy", 5000.0)

        val SYNC_PERIOD by ConfigDuration("sync_period", 5.0.minutes)

        val CLOSE_ADVENTURE_PLAYERS_FLYING by ConfigBoolean("close_adventure_players_flying", false)

        object EnergyBar : ConfigContainerImpl("energy_bar") {

            val COLOR: BarColor by ConfigEnum("color", BarColor.GREEN)

            val TITLE by ConfigString("title", "飞行能量: %.2f(%+.2f)/%.2f")

            val STYLE: BarStyle by ConfigEnum("style", BarStyle.SEGMENTED_10)

        }

        val PRICE by ConfigDouble("price", 1.0)

        val MONEY_ITEM by ConfigStringDoubleMap(
            "money_item",
            mapOf(
                "⑨币" to 5000.0,
                "冰辉石" to 78.125
            )
        )

    }

    val BLOCK_INFOS by ConfigBlockInfos(
        "block_infos",
        mapOf(
            "西瓜" to BlockInfo(type = "minecraft:melon"),
            "主世界方块" to BlockInfo(world = "minecraft:overworld"),
        )
    )

    object Entity : ConfigContainerImpl("entity") {

        val ENTITY_INFOS by ConfigEntityInfos(
            "entity_infos",
            mapOf(
                "小黑" to EntityInfo(type = "minecraft:enderman"),
                "苦力怕" to EntityInfo(type = "minecraft:creeper")
            )
        )

        val CHANGE_BLOCK_MAP by ConfigStringListMap(
            "change_block_map",
            mapOf(
                "小黑" to listOf("主世界方块 -> minecraft:air")
            )
        )

        val EXPLODE_BLOCK_MAP by ConfigStringListMap(
            "explode_block_map",
            mapOf(
                "苦力怕" to listOf("西瓜")
            )
        )

    }

}