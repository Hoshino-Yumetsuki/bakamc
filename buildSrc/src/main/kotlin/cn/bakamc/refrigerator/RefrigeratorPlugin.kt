package cn.bakamc.refrigerator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

const val BAKAMC: String = "_bakamc"

class RefrigeratorPlugin : Plugin<Project> {


    override fun apply(project: Project) {
        project.rootProject.run {
            if (!extra.has(BAKAMC)) {
                extra[BAKAMC] = BakaMC(this)
            }
            if (project == this)
                println(project.rootProject.bakamc)

        }
    }

}

val Project.bakamc get() = this.rootProject.extra[BAKAMC] as BakaMC
