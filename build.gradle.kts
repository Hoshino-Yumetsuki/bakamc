import cn.bakamc.refrigerator.bakamc
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

apply(plugin = "cn.bakamc.refrigerator")

plugins {
    java
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "cn.bakamc.refrigerator")
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    version = bakamc.version
    group = bakamc.group
    description = bakamc.description

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
        maven { url = uri("https://maven.forpleuvoir.moe/snapshots") }
        maven { url = uri("https://jitpack.io") }
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    kotlin {
        compilerOptions{
            jvmTarget.set(JvmTarget.JVM_21)
            suppressWarnings = true
        }
    }

    tasks {

        withType<JavaCompile>().configureEach {
            this.options.release.set(21)
            this.options.encoding = "UTF-8"
            targetCompatibility = JavaVersion.VERSION_21.toString()
            sourceCompatibility = JavaVersion.VERSION_21.toString()
        }

    }

}