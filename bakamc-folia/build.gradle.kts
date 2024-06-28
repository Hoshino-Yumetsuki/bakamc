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

val props = mapOf(
    "name" to project.name,
    "version" to project.version,
    "description" to project.description,
    "apiVersion" to "1.20"
)

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
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val templateSource = file("${projectDir}/src/main/templates")

val templateDest: Provider<Directory> = layout.buildDirectory.dir("${projectDir}/generated/sources/templates")

val generateTemplates by tasks.registering(Copy::class) {
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets["main"].java.srcDir(generateTemplates.map { it.outputs })