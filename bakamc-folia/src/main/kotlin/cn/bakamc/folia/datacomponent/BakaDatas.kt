package cn.bakamc.folia.datacomponent

import cn.bakamc.folia.BakaMCPlugin
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.component.CustomData
import java.util.function.UnaryOperator

object BakaDatas {

    val namespace get() = BakaMCPlugin.instance.bakaName

    private fun id(id: String) = "${namespace}_$id"


    val INTERACT :DataComponentType<CustomData> = register("interact"){
        it.persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC)
    }


    private fun <T> register(id: String, builderOperator: UnaryOperator<DataComponentType.Builder<T>>): DataComponentType<T> {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id(id), builderOperator.apply(DataComponentType.builder()).build())
    }
}