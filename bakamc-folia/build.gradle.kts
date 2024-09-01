plugins {
    alias(libs.plugins.paperUserdev)
    alias(libs.plugins.runPaper)
}

repositories {
    maven { url = uri("https://maven.moliatopia.icu/repository/maven-public/") }
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    implementation(project(":bakamc-common"))

//    paperweight.foliaDevBundle("${libs.versions.minecraftVersion.get()}-R0.1-SNAPSHOT")
    paperweight.devBundle(group = "me.earthme.luminol", artifactId = "dev-bundle", version = "1.21.1-R0.1-20240825.043711-6")

    compileOnly(libs.vaultApi) { isTransitive = false }
    implementation(libs.adventureExtraKotlin)

//    compileOnly(fileTree("${projectDir.absolutePath}/libs"))

    //data base
    runtimeOnly(libs.mysql)
    implementation(libs.bundles.ktorm)
    implementation(libs.hikari)
}

val props = mapOf(
    "name" to project.name,
    "version" to project.version,
    "description" to project.description,
    "apiVersion" to "1.21"
)

tasks {

    register<Copy>("pluginJar") {
        dependsOn(reobfJar)
        mustRunAfter(reobfJar)
        val outPath = "$rootDir/pluginJars/$version"
        val name = reobfJar.get().outputJar.get().asFile.name
        val newName = "${project.name}-$version-minecraft.${libs.versions.minecraftVersion.get()}.jar"
        from("build/libs")
        into(outPath)
        include(name)
        doLast {
            delete(file("$outPath/$newName"))
            file("$outPath/$name").renameTo(file("$outPath/$newName"))
        }
    }

    runServer {
        minecraftVersion(libs.versions.minecraftVersion.get())
    }

//    runPaper {
//        folia.registerTask()
//    }

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