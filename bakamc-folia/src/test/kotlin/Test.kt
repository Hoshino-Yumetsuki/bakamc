import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.PlayerInfo
import cn.bakamc.folia.db.table.playerInfos
import kotlinx.coroutines.*
import org.ktorm.entity.toList
import java.nio.file.Path
import kotlin.math.pow

fun main() {
    println("bind 66FFCC".substring("bind".length+1))
}

fun Double.round(c: Int): Double {
    return Math.round(this * 10.0.pow(c.toDouble())) * 0.1.pow(c.toDouble())
}

fun CoroutineScope.suspendFun(): Deferred<Int> {
    return async {
        delay(2000)
        println(Thread.currentThread().name)
        114514
    }
}

suspend fun Configs.testInit(path: Path) {
    init()
    runCatching {
        Configs.load()
        if (this.savable()) {
            Configs.save()
        }
    }.onFailure {
        it.printStackTrace()
        Configs.forceSave()
    }
}

suspend fun suspendFun2() {
    database {
        playerInfos.toList()
    }
}


val DefaultScope = CoroutineScope(Dispatchers.Default)

suspend fun db() {
    Configs.step(Path.of("./build/config"))


    val players = DefaultScope.async { getAllPlayer() }
    println("我就不等他执行完了")

    runBlocking {
        players.await().forEach {
            println(it)
        }

    }

}

suspend fun getAllPlayer(): List<PlayerInfo> {
    return database {
        playerInfos.toList().subList(0, 50)
    }
}


