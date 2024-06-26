package cn.bakamc.refrigerator

import org.gradle.api.Project


open class BakaMC(private val project: Project) {

    val group get() = get("bakamc_group")

    val version get() = get("bakamc_version")

    val description get() = get("bakamc_description")

    val minecraftVersion get() = get("minecraft_version")

    val nebulaVersion get() = get("nebula_version")

    val coroutinesVersion get() = get("kotlinx_coroutines_version")

    val ktormVersion get() = get("ktorm_version")

    val mysqlVersion get() = get("mysql_version")

    val hikariVersion get() = get("hikari_version")


    val kyoriVersion get() = get("kyori_version")


    private fun get(key: String): String {
        return project.properties[key].toString()
    }

    override fun toString(): String {
        return "BakaMC(group='$group', version='$version', description='$description', minecraftVersion='$minecraftVersion', nebulaVersion='$nebulaVersion', coroutinesVersion='$coroutinesVersion', ktormVersion='$ktormVersion', mysqlVersion='$mysqlVersion', hikariVersion='$hikariVersion')"
    }

}