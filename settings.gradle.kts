pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }

    plugins {
        kotlin("multiplatform").version("1.9.10")
        id("org.jetbrains.compose").version("1.5.2")
    }
}

rootProject.name = "MagicMatrix"