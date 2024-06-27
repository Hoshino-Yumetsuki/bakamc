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

const val bakaRuntimeOnlyName = "bakaRuntimeOnly"

fun bakaRuntimeOnly(project: Project) {
    project.configurations.create(bakaRuntimeOnlyName)
    project.afterEvaluate {
        bakaImplementation.extendsFrom(configurations.getByName("runtimeOnly"))
        configurations.getByName("runtimeClasspath").let { runtimeClasspath ->
            if (!runtimeClasspath.extendsFrom.contains(bakaRuntimeOnly)) runtimeClasspath.extendsFrom(bakaRuntimeOnly)
        }
    }
}

val Project.bakaRuntimeOnly get() = configurations[bakaRuntimeOnlyName]

fun DependencyHandler.bakaRuntimeOnly(dependencyNotation: Any) = add(bakaRuntimeOnlyName, dependencyNotation)

fun DependencyHandler.bakaRuntimeOnly(
    dependencyNotation: Any,
    dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(this, bakaRuntimeOnlyName, dependencyNotation, dependencyConfiguration)

fun DependencyHandler.bakaRuntimeOnly(
    dependencyNotation: String,
    dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(
    this, bakaRuntimeOnlyName, dependencyNotation, dependencyConfiguration
)

fun DependencyHandler.bakaRuntimeOnly(
    dependencyNotation: Provider<*>,
    dependencyConfiguration: Action<ExternalModuleDependency>
): Unit = addConfiguredDependencyTo(
    this, bakaRuntimeOnlyName, dependencyNotation, dependencyConfiguration
)

fun DependencyHandler.bakaRuntimeOnly(
    dependencyNotation: ProviderConvertible<*>,
    dependencyConfiguration: Action<ExternalModuleDependency>
): Unit = addConfiguredDependencyTo(
    this, bakaRuntimeOnlyName, dependencyNotation, dependencyConfiguration
)

fun DependencyHandler.bakaRuntimeOnly(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: Action<ExternalModuleDependency>? = null
): ExternalModuleDependency = addExternalModuleDependencyTo(
    this, bakaRuntimeOnlyName, group, name, version, configuration, classifier, ext, dependencyConfiguration
)

fun <T : ModuleDependency> DependencyHandler.bakaRuntimeOnly(
    dependency: T,
    dependencyConfiguration: T.() -> Unit
): T = add(bakaRuntimeOnlyName, dependency, dependencyConfiguration)

fun DependencyConstraintHandler.bakaRuntimeOnly(constraintNotation: Any): DependencyConstraint =
    add(bakaRuntimeOnlyName, constraintNotation)


fun DependencyConstraintHandler.bakaRuntimeOnly(constraintNotation: Any, block: DependencyConstraint.() -> Unit): DependencyConstraint =
    add(bakaRuntimeOnlyName, constraintNotation, block)

fun ArtifactHandler.bakaRuntimeOnly(artifactNotation: Any): PublishArtifact =
    add(bakaRuntimeOnlyName, artifactNotation)

fun ArtifactHandler.bakaRuntimeOnly(
    artifactNotation: Any,
    configureAction: ConfigurablePublishArtifact.() -> Unit
): PublishArtifact =
    add(bakaRuntimeOnlyName, artifactNotation, configureAction)
