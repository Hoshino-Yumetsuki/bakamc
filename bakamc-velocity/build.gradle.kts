@file:Suppress("VulnerableLibrariesLocal")

import cn.bakamc.refrigerator.bakamc
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.runVelocity)
}

val pluginJar: Configuration by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

val pluginJarRuntime: Configuration by configurations.creating {
    extendsFrom(configurations.runtimeOnly.get())
}

configurations {
    compileClasspath {
        extendsFrom(pluginJar)
    }
    runtimeClasspath {
        extendsFrom(pluginJarRuntime)
    }
}

dependencies {
    compileOnly(libs.velocityApi)
    annotationProcessor(libs.velocityApi)

    compileOnly("io.github.dreamvoid:MiraiMC-Integration:1.8.3")

    pluginJar(project(":bakamc-common")) {
        isTransitive = false
    }

    pluginJar(libs.nebula) {
        exclude("moe.forpleuvoir", "nebula-event")
        exclude("com.google.code.gson", "gson")
    }
    pluginJar(libs.kotlinCoroutines)

    pluginJarRuntime(libs.mysql)
    pluginJar(libs.bundles.ktorm)
    pluginJar(libs.hikari)

    testImplementation(kotlin("test"))
    testImplementation(libs.nebula)
    testImplementation(libs.kotlinCoroutines)
}

tasks {

    runVelocity {
        velocityVersion(libs.versions.velocityVersion.get())
    }

    register<ShadowJar>("pluginJar") {
        archiveBaseName.set(project.name)
        from(sourceSets["main"].output)
        configurations = listOf(
            project.configurations["pluginJar"],
            project.configurations["pluginJarRuntime"]
        )
        exclude("org/intellij/**")
        exclude("org/jetbrains/**")
        exclude("META-INF/com.android.tools/**")
    }

}

val templateSource = file("${project.projectDir}/src/main/templates")

val templateDest: Provider<Directory> = layout.buildDirectory.dir("${project.projectDir}/generated/sources/templates")

val generateTemplates by tasks.registering(Copy::class) {
    val props = mapOf(
        "version" to project.version
    )
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets["main"].java.srcDir(generateTemplates.map { it.outputs })
