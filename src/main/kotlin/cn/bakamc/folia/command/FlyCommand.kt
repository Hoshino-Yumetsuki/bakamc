package cn.bakamc.folia.command

import cn.bakamc.folia.command.base.*
import cn.bakamc.folia.config.Configs.FlightEnergy.MAX_ENERGY
import cn.bakamc.folia.config.Configs.FlightEnergy.MONEY_ITEM
import cn.bakamc.folia.config.Configs.FlightEnergy.PRICE
import cn.bakamc.folia.db.table.isMatch
import cn.bakamc.folia.db.table.toItemStack
import cn.bakamc.folia.extension.money
import cn.bakamc.folia.extension.withdraw
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.flight_energy.FlightEnergyManager.barVisible
import cn.bakamc.folia.flight_energy.FlightEnergyManager.energy
import cn.bakamc.folia.flight_energy.FlightEnergyManager.updateEnergy
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.Style
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.literalText
import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.nebula.common.color.Color
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.entity.Player


@Suppress("FunctionName")
internal fun FlyCommand(): Command = Command("fly") {
    execute<Player>(toggleFly)
    literal("enable") {
        execute<Player>(toggleFly)
        argument("enabled") {
            suggestion { listOf("true", "false") }
            execute<Player>(toggleFly)
        }
    }
    literal("barvisible") {
        execute<Player>(::toggleBarVisible)
        argument("visible") {
            suggestion { listOf("true", "false") }
            execute<Player>(::toggleBarVisible)
        }
    }
    literal("set") {
        permission { it.sender.isOp }
        argument("player") {
            suggestions = null
            argument("energy") {
                execute<Player> { ctx ->
                    val energy = ctx.getArg("energy")?.toDouble() ?: 0.0
                    runBlocking {
                        ctx.getArg("player")!!.let { name ->
                            ctx.sender.server.onlinePlayers.find { it.name == name }?.let { player ->
                                player.updateEnergy(energy)
                                ctx.success("成功设置玩家{}的飞行能量为[{}]", player, energy)
                            }
                        }
                    }
                }
            }
        }
    }

    literal("add") {
        permission { it.sender.isOp }
        argument("player") {
            suggestions = null
            argument("energy") {
                execute<Player> { ctx ->
                    val energy = ctx.getArg("energy")?.toDouble() ?: 0.0
                    runBlocking {
                        ctx.getArg("player")!!.let { name ->
                            ctx.sender.server.onlinePlayers.find { it.name == name }?.let { player ->
                                player.updateEnergy(player.energy + energy)
                                ctx.success("成功为玩家{}添加[{}]飞行能量", player, energy)
                            }
                        }
                    }
                }
            }
        }
    }

    literal("addAll") {
        permission { it.sender.isOp }
        argument("energy") {
            execute<Player> { ctx ->
                val energy = ctx.getArg("energy")?.toDouble() ?: 0.0
                runBlocking {
                    val count = PlayerService.addAllPlayerEnergy(energy, 0.0..MAX_ENERGY)
                    ctx.success("成功为{}位玩家添加了{}飞行能量", count, energy)
                }
            }
        }
    }
    literal("addOnlinePlayer") {
        permission { it.sender.isOp }
        argument("energy") {
            execute<Player> { ctx ->
                val energy = ctx.getArg("energy")?.toDouble() ?: 0.0
                runBlocking {
                    val count = FlightEnergyManager.addAllOnlinePlayerEnergy(energy, 0.0..MAX_ENERGY)
                    ctx.success("成功为{}位在线玩家添加了{}飞行能量", count, energy)
                }
            }
        }
    }

    literal("recharge") {
        execute(recharge)
        argument("energy") {
            suggestionBuild { ctx, _ ->
                val price = PRICE
                val energy = ctx.getArg("energy")!!.toDouble()
                listOf("§6每1游戏币可以购买§a[${price}]§6飞行能量,当前购买所需货币§a[${energy * price}]§6,当前持有货币§a[${(ctx.sender as Player).money}]")
            }
            execute<Player>(recharge)
        }
    }
    literal("exchange") {
        argument("money_item") {
            suggestion { SpecialItemManager.specifyType(MONEY_ITEM.keys).keys.toList() }
            execute(exchange)
            argument("count") {
                suggestionBuild { ctx, _ ->
                    val itemKey = ctx.getArg("money_item")!!
                    val energy = MONEY_ITEM[itemKey]
                    val count = ctx.getArg("count")?.toInt() ?: 1
                    if (energy != null)
                        listOf("§6每个${itemKey}可以兑换§a[${energy}]§6飞行能量,当前可兑换§a[${count * energy}]§6飞行能量")
                    else
                        listOf("§c无效的货币[${itemKey}]类型!")
                }
                execute<Player>(exchange)
            }
        }
    }
    literal("energy") {
        suggestion { null }
        execute<Player> {
            it.info("你当前的飞行能量为[{}]", it.sender.energy)
        }
    }
}

private fun toggleBarVisible(context: CommandContext<out Player>) {
    val visible = context.getArg("visible")?.toBoolean()
    context.sender.barVisible = visible ?: !context.sender.barVisible
    val status = if (context.sender.barVisible) "[开启]" else "[关闭]"
    val style = if (context.sender.barVisible) Color("#55FF55") else Color("FF5555")
    context.info("飞行能量条显示已切换{}", literalText(status, Style(style)))
}

private val toggleFly: (context: CommandContext<out Player>) -> Unit = func@{ context ->
    val player = context.sender
    val enabled = context.getArg("enabled")?.toBoolean()
    FlightEnergyManager.apply {
        if (player.energy > 0) {
            toggleFly(player, enabled)
            val status = if (player.allowFlight) "[开启]" else "[关闭]"
            val style = if (player.allowFlight) Color("#55FF55") else Color("FF5555")
            context.success("飞行状态已切换{}", literalText(status, Style(style)))
        } else {
            context.fail("你的飞行能量不足,无法切换飞行状态")
            context.fail("请输入指令 {} 购买飞行能量", "/fly recharge <飞行能量数量>")
        }
    }
}

private val recharge: (CommandContext<out Player>) -> Unit = { ctx ->
    val player = ctx.sender
    //玩家所持有的货币总量
    val totalMoney = player.money
    //需要充值的能量
    val energy = ctx.getArg("energy")!!.toDouble()
    //当次充值所消耗的货币量
    val cost = energy * PRICE

    //是否有充足的货币
    if (totalMoney >= cost) {
        //有足够的货币
        FlightEnergyManager.apply {
            //判断是否超出能量上限
            if (energy + ctx.sender.energy > MAX_ENERGY) {
                //超出了能量上限
                ctx.fail("超出了能量上限，最大值:[{}],充值后会超出:[{}]", MAX_ENERGY, (energy + ctx.sender.energy) - MAX_ENERGY)
            } else {
                //没有超出上限
                runCatching {
                    //执行扣费操作
                    val response = player.withdraw(cost)
                    when (response.type) {
                        EconomyResponse.ResponseType.SUCCESS         -> launch {
                            player.updateEnergy(ctx.sender.energy + energy)
                            ctx.success("成功购买{}的能量,当前剩余能量值[{}]", energy, ctx.sender.energy)
                        }

                        EconomyResponse.ResponseType.FAILURE         -> ctx.fail("购买失败[{}]", response.errorMessage)
                        EconomyResponse.ResponseType.NOT_IMPLEMENTED -> ctx.fail("购买失败,经济插件未加载,请联系服务器管理员")
                    }
                }.onFailure {
                    //出现异常
                    ctx.fail("购买失败[{}]", it.message ?: "未知错误")
                    ctx.fail("请联截图系管理员处理")
                }
            }
        }
    } else {
        //没有足够的货币
        ctx.fail("购买[{}]能量所需的货币不足,当前拥有货币[{}]", energy, totalMoney)
    }
}

private val exchange: (CommandContext<out Player>) -> Unit = { ctx ->
    val player = ctx.sender

    val key = ctx.getArg("money_item")!!

    val count = ctx.getArg("count")?.toInt() ?: 1

    var countTemp = count

    val moneyItem = FlightEnergyManager.moneyItem(key)

    if (moneyItem != null) {
        val (item, energy) = moneyItem

        val inventory = ctx.player!!.inventory

        //纪录扣费动作
        val actions = mutableListOf<() -> Unit>()

        //过滤出对应的货币
        inventory.items.filter { stack ->
            if (!stack.isEmpty) {
                item.isMatch(stack)
            } else false
        }.forEach { stack ->
            val temp = countTemp.coerceAtMost(stack.count)
            countTemp -= temp

            actions.add {
                stack.count -= temp
            }

        }

        //是否有充足的货币
        if (countTemp == 0) {
            //有足够的货币
            val totalEnergy = energy * count
            FlightEnergyManager.apply {
                //判断是否超出能量上限
                if (totalEnergy + ctx.sender.energy > MAX_ENERGY) {
                    //超出了能量上限
                    ctx.fail("超出了能量上限，最大值:[{}],充值后会超出:[{}]", MAX_ENERGY, (totalEnergy + ctx.sender.energy) - MAX_ENERGY)
                } else {
                    //没有超出上限
                    runCatching {
                        //执行扣费操作
                        actions.forEach { it.invoke() }
                    }.onFailure {
                        //出现异常
                        ctx.fail("购买失败[{}]", it.message ?: "未知错误")
                        ctx.fail("请联截图系管理员处理")
                    }.onSuccess {
                        //结算能量
                        launch {
                            player.updateEnergy(ctx.sender.energy + totalEnergy)
                            ctx.success("购买成功{}的能量,当前剩余能量值[{}]", item.toItemStack(count)!!, ctx.sender.energy)
                        }
                    }
                }
            }
        } else {
            //没有足够的货币
            ctx.fail("你所拥有的对应货币[{}]数量不足", key)
            ctx.fail("需要[{}]个,在背包中找到[{}]个", count, count - countTemp)
        }
    }
}