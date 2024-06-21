package cn.bakamc.folia.config

import cn.bakamc.folia.config.base.ConfigBlockInfos
import cn.bakamc.folia.config.base.ConfigEntityInfos
import cn.bakamc.folia.config.base.ConfigStringDoubleMap
import cn.bakamc.folia.config.base.ConfigStringListMap
import cn.bakamc.folia.event.entity.BlockInfo
import cn.bakamc.folia.event.entity.EntityInfo
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import moe.forpleuvoir.nebula.config.manager.ConfigManagerImpl
import moe.forpleuvoir.nebula.config.manager.component.localConfig
import moe.forpleuvoir.nebula.config.manager.compose
import moe.forpleuvoir.nebula.config.persistence.JsonConfigManagerPersistence.serializeObjectToString
import moe.forpleuvoir.nebula.config.persistence.JsonConfigManagerPersistence.wrapFileName
import moe.forpleuvoir.nebula.config.persistence.jsonPersistence
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import java.nio.file.Path
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object Configs : ConfigManagerImpl("bakamc") {

    lateinit var configPath: Path
        internal set

    /**
     * 配置文件路径
     * @param path Path
     */
    suspend fun init(path: Path) {
        configPath = path

        compose {
            localConfig(configPath, jsonPersistence())
        }

        backup()
        init()
        generateTemp()
        runCatching {
            load()
            if (this.needSave) {
                save()
            }
        }.onFailure {
            it.printStackTrace()
            forceSave()
        }
    }

    @Suppress("RedundantSuspendModifier", "MemberVisibilityCanBePrivate")
    internal suspend fun backup() {
        runCatching {
            ConfigUtil.run {
                val fileName = wrapFileName(this@Configs.key)
                val backupFile = configFile("$fileName.backup", configPath)
                val file = configFile(fileName, configPath)
                file.copyTo(backupFile, true)
            }
        }.onFailure {
            logger.error("备份配置文件失败", it)
        }.onSuccess {
            logger.info("备份配置文件成功")
        }

    }

    /**
     * 生成当前版本的默认配置文件
     */
    @Suppress("RedundantSuspendModifier", "MemberVisibilityCanBePrivate")
    internal suspend fun generateTemp() {
        runCatching {
            ConfigUtil.run {
                val fileName = wrapFileName(this@Configs.key)
                val file = configFile("$fileName.temp", configPath)
                writeStringToFile(serializeObjectToString(serialization().asObject), file)
            }
        }.onFailure {
            logger.error("模板文件生成失败", it)
        }.onSuccess {
            logger.info("模板文件生成成功")
        }
    }

    object Misc : ConfigContainerImpl("misc") {

        val ENABLE_PLAYER_JOIN_MESSAGE by ConfigBoolean("enable_player_join_message", true)

        val ENABLE_PLAYER_QUIT_MESSAGE by ConfigBoolean("enable_player_quit_message", true)

        val ENABLE_ANVIL_CUSTOM_RENAME by ConfigBoolean("enable_anvil_custom_rename", true)

        val ANVIL_RENAME_STYLE_MAPPING by ConfigStringListMap(
            key = "anvil_rename_style_mapping",
            defaultValue = mapOf(
                "obfuscated" to listOf("o", "obfuscated", "OBFUSCATED"),
                "bold" to listOf("b", "bold", "BOLD"),
                "strikethrough" to listOf("s", "strikethrough", "STRIKETHROUGH"),
                "underline" to listOf("l", "underline", "UNDERLINE"),
                "italic" to listOf("i", "italic", "ITALIC"),
                "rest" to listOf("r", "rest", "RESET")
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