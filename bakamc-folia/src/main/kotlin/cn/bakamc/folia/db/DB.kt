package cn.bakamc.folia.db

import cn.bakamc.folia.config.DatabaseConfig
import cn.bakamc.folia.config.DatabaseConfig.DATA_SOURCE
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect
import javax.sql.DataSource


suspend fun initDataBase() = withContext(Dispatchers.IO) {
    database = Database.connect(dataSource, MySqlDialect())
}

val dataSource: DataSource
    get() {
        return HikariDataSource(
            HikariConfig().apply {
                connectionTimeout = DATA_SOURCE.CONNECTION_TIMEOUT
                idleTimeout = DATA_SOURCE.IDLE_TIMEOUT
                maximumPoolSize = DATA_SOURCE.MAXIMUM_POOL_SIZE
                maxLifetime = DATA_SOURCE.MAX_LIFETIME
                keepaliveTime = DATA_SOURCE.KEEPALIVE_TIME
                minimumIdle = DATA_SOURCE.MINIMUM_IDLE
                driverClassName = "com.mysql.cj.jdbc.Driver"
                jdbcUrl = DatabaseConfig.URL
                username = DatabaseConfig.USER
                password = DatabaseConfig.PASSWORD
            }
        )
    }

lateinit var database: Database
    private set

internal suspend fun <R> database(action: suspend Database.() -> R): R = withContext(Dispatchers.IO) {
    action(database)
}

