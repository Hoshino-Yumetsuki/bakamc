package cn.bakamc.refrigerator

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.kotlin.dsl.accessors.runtime.addConfiguredDependencyTo
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.gradle.kotlin.dsl.accessors.runtime.addExternalModuleDependencyTo
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.get

const val bakaImplementationName: String = "bakaImplementation"

fun bakaImplementation(project: Project) {
    project.configurations.create(bakaImplementationName)
    project.afterEvaluate {
        bakaImplementation.extendsFrom(configurations.getByName("implementation"))
        configurations.getByName("compileClasspath").let { runtimeClasspath ->
            if (!runtimeClasspath.extendsFrom.contains(bakaImplementation)) runtimeClasspath.extendsFrom(bakaImplementation)
        }
    }
}

val Project.bakaImplementation get() = configurations[bakaImplementationName]

fun DependencyHandler.bakaImplementation(dependencyNotation: Any) = add(bakaImplementationName, dependencyNotation)

fun DependencyHandler.bakaImplementation(
    dependencyNotation: Any,
    dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(this, bakaImplementationName, dependencyNotation, dependencyConfiguration)

fun DependencyHandler.bakaImplementation(
    dependencyNotation: String,
    dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(
    this, bakaImplementationName, dependencyNotation, dependencyConfiguration
)

fun DependencyHandler.bakaImplementation(
    dependencyNotation: Provider<*>,
    dependencyConfiguration: Action<ExternalModuleDependency>
): Unit = addConfiguredDependencyTo(
    this, bakaImplementationName, dependencyNotation, dependencyConfiguration
)

fun DependencyHandler.bakaImplementation(
    dependencyNotation: ProviderConvertible<*>,
    dependencyConfiguration: Action<ExternalModuleDependency>
): Unit = addConfiguredDependencyTo(
    this, bakaImplementationName, dependencyNotation, dependencyConfiguration
)

fun DependencyHandler.bakaImplementation(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: Action<ExternalModuleDependency>? = null
): ExternalModuleDependency = addExternalModuleDependencyTo(
    this, bakaImplementationName, group, name, version, configuration, classifier, ext, dependencyConfiguration
)

fun <T : ModuleDependency> DependencyHandler.bakaImplementation(
    dependency: T,
    dependencyConfiguration: T.() -> Unit
): T = add(bakaImplementationName, dependency, dependencyConfiguration)

fun DependencyConstraintHandler.bakaImplementation(constraintNotation: Any): DependencyConstraint =
    add(bakaImplementationName, constraintNotation)


fun DependencyConstraintHandler.bakaImplementation(constraintNotation: Any, block: DependencyConstraint.() -> Unit): DependencyConstraint =
    add(bakaImplementationName, constraintNotation, block)

fun ArtifactHandler.bakaImplementation(artifactNotation: Any): PublishArtifact =
    add(bakaImplementationName, artifactNotation)

fun ArtifactHandler.bakaImplementation(
    artifactNotation: Any,
    configureAction: ConfigurablePublishArtifact.() -> Unit
): PublishArtifact =
    add(bakaImplementationName, artifactNotation, configureAction)
