import cn.bakamc.refrigerator.bakaImplementation
import cn.bakamc.refrigerator.bakaRuntimeOnly
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.runVelocity)
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly(libs.velocityApi)
    annotationProcessor(libs.velocityApi)

    compileOnly(libs.miraiMCVelocity)

    bakaImplementation(project(":bakamc-common"))

    bakaImplementation(libs.nebula) {
        exclude("moe.forpleuvoir", "nebula-event")
        exclude("com.google.code.gson", "gson")
    }

    bakaImplementation(libs.kotlinCoroutines)

    bakaRuntimeOnly(libs.mysql)
    bakaImplementation(libs.bundles.ktorm)
    bakaImplementation(libs.hikari)

    testImplementation(kotlin("test"))
    testImplementation(libs.nebula)
    testImplementation(libs.kotlinCoroutines)
}


tasks {

    runVelocity {
        velocityVersion(libs.versions.velocityVersion.get())
        downloadPlugins {
            github("DreamVoid", "MiraiMC", "v${libs.versions.miraiMCVersion.get()}", "MiraiMC-Velocity.jar")
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        from(sourceSets["main"].output)
        configurations = listOf(
            bakaImplementation,
            bakaRuntimeOnly
        )
        exclude("org/intellij/**")
        exclude("org/jetbrains/**")
        exclude("META-INF/com.android.tools/**")
    }


}

val templateSource = file("${projectDir}/src/main/templates")

val templateDest: Provider<Directory> = layout.buildDirectory.dir("${projectDir}/generated/sources/templates")

val generateTemplates by tasks.registering(Copy::class) {
    val props = mapOf(
        "version" to project.version,
        "name" to project.name,
        "description" to project.description
    )
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets["main"].java.srcDir(generateTemplates.map { it.outputs })
