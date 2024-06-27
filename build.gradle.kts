import org.jetbrains.kotlin.gradle.dsl.JvmTarget

apply(plugin = "cn.bakamc.refrigerator")

plugins {
    java
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "cn.bakamc.refrigerator")
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://maven.forpleuvoir.moe/snapshots") }
        maven { url = uri("https://maven.forpleuvoir.moe/releases") }
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    kotlin {
        compilerOptions.suppressWarnings = true
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

    tasks {

        test {
            useJUnitPlatform()
        }

        withType<JavaCompile>().configureEach {
            this.options.release.set(21)
            this.options.encoding = "UTF-8"
            targetCompatibility = JavaVersion.VERSION_21.toString()
            sourceCompatibility = JavaVersion.VERSION_21.toString()
        }
    }

}