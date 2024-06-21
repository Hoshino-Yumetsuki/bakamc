package cn.bakamc.folia.db

import cn.bakamc.folia.config.Configs
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect
import javax.sql.DataSource


fun initDataBase() {
    database = Database.connect(dataSource, MySqlDialect())
}

val dataSource: DataSource
    get() {
        return HikariDataSource(
            HikariConfig().apply {
                connectionTimeout = Configs.Database.DataSource.CONNECTION_TIMEOUT
                idleTimeout = Configs.Database.DataSource.IDLE_TIMEOUT
                maximumPoolSize = Configs.Database.DataSource.MAXIMUM_POOL_SIZE
                maxLifetime = Configs.Database.DataSource.MAX_LIFETIME
                keepaliveTime = Configs.Database.DataSource.KEEPALIVE_TIME
                minimumIdle = Configs.Database.DataSource.MINIMUM_IDLE
                driverClassName = "com.mysql.cj.jdbc.Driver"
                jdbcUrl = Configs.Database.URL
                username = Configs.Database.USER
                password = Configs.Database.PASSWORD
            }
        )
    }

lateinit var database: Database
    private set

internal suspend fun <R> database(action: Database.() -> R): R = withContext(Dispatchers.IO) {
    action(database)
}

