package cn.bakamc.refrigerator

import org.gradle.api.Project


open class BakaMC(private val project: Project) {

    val group get() = get("bakamc_group")

    val version get() = get("bakamc_version")

    val description get() = get("bakamc_description")


    private fun get(key: String): String {
        return project.properties[key].toString()
    }

    override fun toString(): String {
        return "BakaMC(group='$group', version='$version', description='$description')"
    }


}