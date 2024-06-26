import cn.bakamc.refrigerator.bakamc

plugins {
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.0" // Adds runServer and runMojangMappedServer tasks for testing
}

group = bakamc.group
version = bakamc.version
description = bakamc.description

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":bakamc-common"))

    paperweight.foliaDevBundle("${bakamc.minecraftVersion}-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        isTransitive = false
    }

    //data base
    @Suppress("REDUNDANT_LABEL_WARNING")
    database@ apply {
        runtimeOnly("com.mysql:mysql-connector-j:${bakamc.mysqlVersion}")
        implementation("org.ktorm:ktorm-core:${bakamc.ktormVersion}")
        implementation("org.ktorm:ktorm-support-mysql:${bakamc.ktormVersion}")
        implementation("com.zaxxer:HikariCP:${bakamc.hikariVersion}")
    }
}

sourceSets {
    getByName("test") {
        kotlin.srcDir("src/test/kotlin")
    }
}

tasks {

    runServer {
        minecraftVersion(bakamc.minecraftVersion)
        runDirectory(File(project.projectDir, "run"))
    }

    runPaper {
        folia.registerTask()
    }

    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

sourceSets {
    getByName("test") {
        kotlin.srcDir("src/test/kotlin")
    }
}