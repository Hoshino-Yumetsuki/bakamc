import cn.bakamc.refrigerator.bakamc

dependencies {
    api(kotlin("reflect"))
    api(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${bakamc.coroutinesVersion}")
    api("moe.forpleuvoir:nebula:${bakamc.nebulaVersion}") {
        exclude("moe.forpleuvoir", "nebula-event")
    }
    compileOnly("net.kyori:adventure-api:${bakamc.kyoriVersion}")
    implementation("net.kyori:adventure-extra-kotlin:${bakamc.kyoriVersion}")
}