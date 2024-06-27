@file:Suppress("VulnerableLibrariesLocal")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://maven.forpleuvoir.moe/snapshots")
    }
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
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    compileOnly("io.github.dreamvoid:MiraiMC-Integration:1.8.3")

    pluginJar("moe.forpleuvoir:nebula:0.2.9b") {
        exclude("moe.forpleuvoir", "nebula-event")
        exclude("com.google.code.gson", "gson")
    }
    pluginJar("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    val ktormVersion = "3.6.0"
    pluginJarRuntime("com.mysql:mysql-connector-j:8.4.0")
    pluginJar("org.ktorm:ktorm-core:${ktormVersion}")
    pluginJar("org.ktorm:ktorm-support-mysql:${ktormVersion}")
    pluginJar("com.zaxxer:HikariCP:5.0.1")

    testImplementation(kotlin("test"))
    testImplementation("moe.forpleuvoir:nebula:0.2.9b")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

tasks {

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

val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")

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
