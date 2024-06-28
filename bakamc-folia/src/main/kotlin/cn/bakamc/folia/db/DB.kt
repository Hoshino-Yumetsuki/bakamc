package cn.bakamc.folia.db

import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.config.DatabaseConfig
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
                connectionTimeout = DatabaseConfig.DataSource.CONNECTION_TIMEOUT
                idleTimeout = DatabaseConfig.DataSource.IDLE_TIMEOUT
                maximumPoolSize = DatabaseConfig.DataSource.MAXIMUM_POOL_SIZE
                maxLifetime = DatabaseConfig.DataSource.MAX_LIFETIME
                keepaliveTime = DatabaseConfig.DataSource.KEEPALIVE_TIME
                minimumIdle = DatabaseConfig.DataSource.MINIMUM_IDLE
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

