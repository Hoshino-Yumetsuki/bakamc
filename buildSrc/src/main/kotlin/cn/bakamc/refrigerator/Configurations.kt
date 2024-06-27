package cn.bakamc.refrigerator

import org.gradle.api.Project

fun applyConfiguration(project: Project) {
    bakaImplementation(project)
    bakaRuntimeOnly(project)
}


