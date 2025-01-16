import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.serialization)
    //Cocoapods
   // alias(libs.plugins.kotlin.cocoapods)

    id("maven-publish")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcf = XCFramework()
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())

    iosTargets.forEach {
        it.binaries.framework {
            baseName = "shared"
            freeCompilerArgs += "-Xbinary=bundleId=gr.sppzglou.easycompose"
            xcf.add(this)
        }
    }

//    cocoapods {
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
//        version = "1.0"
//        ios.deploymentTarget = "16.0"
//        //podfile = project.file("../iosApp/Podfile")
//        framework {
//            baseName = "shared"
//            isStatic = true
//        }
//
//        // pod("SDWebImage")
//    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.runtime.compose)

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
                implementation("io.github.qdsfdhvh:image-loader:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                implementation("io.github.kevinnzou:compose-webview-multiplatform:1.9.40")
                //logs
                implementation("co.touchlab:kermit:2.0.4")
                implementation("co.touchlab:stately-common:2.0.6")
                //Ktor
                implementation(libs.bundles.ktor)
                //Voyager Navigator
                implementation(libs.bundles.voyage)
                //DataStore
                implementation(libs.bundles.datastore)
                //Room
                implementation(libs.bundles.room.common)
                //Koin-di
                implementation(libs.bundles.koin)
            }
        }

        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:2.0.0") // για Android πλατφόρμες
            implementation("androidx.startup:startup-runtime:1.2.0")
        }

//        val iosX64Main by getting
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting

        iosMain {
//            dependsOn(commonMain.get())
//            iosX64Main.dependsOn(this)
//            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-ios:2.0.0") // για iOS πλατφόρμες

            }
        }
    }
}

android {
    namespace = "gr.sppzglou.easycompose"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
dependencies {
    implementation(libs.androidx.activity.compose)
}


publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ppzglou/easyCompose")
            credentials {
                username = System.getenv("GITHUB_USERNAME") ?: "ppzglou"
                password =
                    System.getenv("GITHUB_TOKEN") ?: "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"
            }
        }
    }
    publications {
        create<MavenPublication>("release") {
            from(components["kotlin"])
            groupId = "gr.sppzglou.easycompose"
            artifactId = "easycompose"
            version = "1.0.0"
        }
    }
}

/*
tasks.register("assembleXCFramework") {
    dependsOn(
        "linkReleaseFrameworkIosArm64",
        "linkReleaseFrameworkIosSimulatorArm64"
    )
    doLast {
        val buildDirectory = project.layout.buildDirectory
        val arm64Framework = buildDirectory.file("bin/iosArm64/releaseFramework/shared.framework").get().asFile
        val simulatorArm64Framework = buildDirectory.file("bin/iosSimulatorArm64/releaseFramework/shared.framework").get().asFile

        val outputDir = buildDirectory.dir("xcframework").get().asFile
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        exec {
            commandLine(
                "xcodebuild",
                "-create-xcframework",
                "-framework", arm64Framework,
                "-framework", simulatorArm64Framework,
                "-output", outputDir.resolve("shared.xcframework")
            )
        }
    }
}*/
