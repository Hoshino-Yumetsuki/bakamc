package cn.bakamc.proxy.database

import cn.bakamc.proxy.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect
import javax.sql.DataSource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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


@OptIn(ExperimentalContracts::class)
internal suspend inline fun <R> database(crossinline action: Database.() -> R): R {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    return withContext(Dispatchers.IO){
         action(database)
    }
}
