pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val composeVersion = extra["compose.wasm.version"] as String
        kotlin("multiplatform").version(kotlinVersion)
        id("org.jetbrains.compose").version(composeVersion)
    }
}

rootProject.name = "MagicMatrix"