dependencies {
    api(libs.bundles.kotlin)
    api(libs.nebula) {
        exclude("moe.forpleuvoir", "nebula-event")
    }
    compileOnly(libs.adventureApi)
    implementation(libs.adventureExtraKotlin){ isTransitive = false }
    implementation(libs.slf4j)
    testImplementation(kotlin("test"))
}