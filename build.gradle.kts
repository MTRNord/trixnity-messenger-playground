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
            dependencies {
                implementation(npm("copy-webpack-plugin", "11.0.0"))
                implementation(compose.html.core)
                implementation(compose.runtime)
                implementation(libs.trixnity.messenger)
                implementation(libs.kobweb.silk)
                implementation(libs.decompose)
                implementation(libs.decompose.extensions.compose.jetbrains)
            }
        }
    }
}