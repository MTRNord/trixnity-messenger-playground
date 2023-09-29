import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    maven("https://gitlab.com/api/v4/projects/47538655/packages/maven")
    maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    google()
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "MagicMatrix.js"
            }
        }
        binaries.executable()
    }
    wasm {
        moduleName = "MagicMatrix"
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
                    static = (devServer?.static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                    },
                )
            }
        }
        binaries.executable()
    }
    sourceSets {
        val jsWasmMain by creating {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
        val jsMain by getting {
            dependsOn(jsWasmMain)
            dependencies {
                implementation(npm("copy-webpack-plugin", "11.0.0"))
                implementation(libs.compose.html)
                implementation(compose.runtime)
                implementation(libs.trixnity.messenger)
                implementation(libs.kobweb.silk)
                implementation(libs.decompose)
                implementation(libs.decompose.extensions.compose.jetbrains)
            }
        }

        val wasmMain by getting {
            dependsOn(jsWasmMain)
        }
    }
}

compose.experimental {
    web.application {}
}

compose {
    val composeVersion = project.property("compose.wasm.version") as String
    kotlinCompilerPlugin.set(composeVersion)
    val kotlinVersion = project.property("kotlin.version") as String
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=$kotlinVersion")
}

configurations.all {
    val conf = this
    // Currently it's necessary to make the android build work properly
    conf.resolutionStrategy.eachDependency {
        val isWasm = conf.name.contains("wasm", true)
        val isJs = conf.name.contains("js", true)
        val isComposeGroup = requested.module.group.startsWith("org.jetbrains.compose")
        val isComposeCompiler = requested.module.group.startsWith("org.jetbrains.compose.compiler")
        if (isComposeGroup && !isComposeCompiler && !isWasm && !isJs) {
            val composeVersion = project.property("compose.version") as String
            useVersion(composeVersion)
        }
        if (requested.module.name.startsWith("kotlin-stdlib")) {
            val kotlinVersion = project.property("kotlin.version") as String
            useVersion(kotlinVersion)
        }
    }
}