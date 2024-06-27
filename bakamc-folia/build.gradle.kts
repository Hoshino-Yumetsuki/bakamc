plugins {
    alias(libs.plugins.paperUserdev)
    alias(libs.plugins.runPaper)
}

dependencies {
    implementation(project(":bakamc-common"))

    paperweight.foliaDevBundle("${libs.versions.minecraftVersion.get()}-R0.1-SNAPSHOT")

    compileOnly(libs.vaultApi) { isTransitive = false }

    //data base
    runtimeOnly(libs.mysql)
    implementation(libs.bundles.ktorm)
    implementation(libs.hikari)
}

tasks {

    runServer {
        minecraftVersion(libs.versions.minecraftVersion.get())
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