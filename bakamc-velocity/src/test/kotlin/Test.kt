import moe.forpleuvoir.nebula.common.util.defaultLaunch
import kotlin.test.Test

class Test {

    @Test
    fun test1() {
        var nameCard = "#{bind[0]} #{bind[last]}"
        val playerNames = listOf("forpleuvoir", "ibuki_kosuzu")
        playerNames.forEachIndexed { index, playerName ->
            val old = nameCard
            nameCard = nameCard.replace("#{bind[$index]}", playerName)
            println(nameCard)
            println(old == nameCard)
            if (index == playerNames.lastIndex && old == nameCard) {
                nameCard = nameCard.replace("#{bind[last]}", playerName)
            }
        }
        nameCard = nameCard.replace(Regex("""#\{bind\[((\d+)|last)]}"""), "")
        println(nameCard)
    }

    @Test
    fun test2(){
        println("test1")
        defaultLaunch {
            Thread.sleep(500)
            println("应该是异步启动的")
        }
        println("test2")
        Thread.sleep(500)
    }
}