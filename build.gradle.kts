import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://gitlab.com/api/v4/projects/47538655/packages/maven")
    maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    google()
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")

            dependencies {
                implementation(compose.html.core)
                implementation(compose.runtime)
                implementation(libs.trixnity.messenger)
                implementation(libs.koin.compose)
                implementation(libs.kobweb.silk)
            }
        }
    }
}