tasks.register("forceClean") {
    doFirst {
        println("🧹 Force cleaning build and Gradle cache folders...")
        val dirs = listOf(
            file("app/build"),
            file(".gradle"),
            file("build")
        )
        dirs.forEach {
            if (it.exists()) {
                it.deleteRecursively()
                println("Deleted: ${it.absolutePath}")
            }
        }
    }
}


//@file:Suppress("DEPRECATION")
//
//tasks.register<Exec>("stopGradleDaemon") {
//    commandLine("cmd", "/c", "gradlew", "--stop")
//}
//
//
//

//// simple task: delete this module's build folder
//tasks.register<Delete>("autoCleanBuild") {
//    group = "cleanup"
//    description = "Delete the module build folder (run this before building when needed)."
//    delete(layout.buildDirectory)
//}
//
//
//tasks.named("preBuild").configure {
//    dependsOn("autoCleanBuild")



//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.compose)
//    id("org.jetbrains.kotlin.kapt") // ✅ This enables 'kapt'
//     // optional if using serialization
//}
//android {
//    namespace = "com.example.lop"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "com.example.lop"
//        minSdk = 21
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//
//    kotlinOptions {
//        jvmTarget = "17"
//    }
//
//    buildFeatures {
//        compose = true
//    }
//}
//
//dependencies {
//    // AndroidX + Jetpack
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//
//    // ✅ AppCompat for normal activities
//    implementation("androidx.appcompat:appcompat:1.7.0")
//
//    // ✅ Room Database (for storing profiles or business cards)
//    implementation("androidx.room:room-runtime:2.6.1")
//    kapt("androidx.room:room-compiler:2.6.1")
//
//    // ✅ Optional – Room Kotlin extensions
//    implementation("androidx.room:room-ktx:2.6.1")
//
//    // Testing
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt") // Needed for Room

//    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}


android {
    namespace = "com.example.lop"
    compileSdk = 36
    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
    }

    defaultConfig {
        applicationId = "com.example.lop"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        viewBinding = true // ✅ for XML-based layouts
    }
    buildToolsVersion = "35.0.0"
}

dependencies {
    val room_version = "2.7.0-alpha02"
    // --- Jetpack / Compose (from your version catalog) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- AppCompat for normal XML Activities ---
    implementation(libs.androidx.appcompat)

    // --- Material Components (dialogs, etc.) ---
    implementation("com.google.android.material:material:1.13.0")

    // --- Lifecycle / Coroutine support ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.activity:activity-ktx:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
//
//    // --- Room Database (for saving profiles) ---
//    implementation("androidx.room:room-runtime:2.8.2")
////    ksp("androidx.room:room-compiler:2.6.1")
//    kapt("androidx.room:room-compiler:2.6.1")
//    implementation("androidx.room:room-ktx:2.8.2")


    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

// Optional – Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")


    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// 🔧 Auto-delete build folder before each build to keep things clean
//tasks.register<Delete>("autoCleanBuild") {
//    delete(layout.buildDirectory)
//}
//
//// Ensure the cleanup happens before Gradle starts building
//tasks.named("preBuild").configure {
//    dependsOn("autoCleanBuild")
//}
//
//
//
//        tasks.register("smartAutoClean") {
//            doFirst {
//                val buildDir = layout.buildDirectory.asFile.get()
//                println("🧹 Attempting to clean build folder safely...")
//
//                // 1️⃣ Stop Gradle daemons (releases file locks)
//                println("🛑 Stopping Gradle daemons...")
//                exec { commandLine("cmd", "/c", "gradlew --stop") }
//
//                // 2️⃣ Kill stray Java processes (optional but strong)
//                try {
//                    println("💀 Killing stray java.exe processes...")
//                    exec { commandLine("cmd", "/c", "taskkill /F /IM java.exe /T") }
//                } catch (e: Exception) {
//                    println("⚠️ Could not kill Java processes — ${e.message}")
//                }
//
//                // 3️⃣ Wait a moment for handles to close
//                Thread.sleep(2000)
//
//                // 4️⃣ Force delete build folder
//                if (buildDir.exists()) {
//                    try {
//                        println("🧨 Forcing delete of ${buildDir.path}")
//                        buildDir.walkBottomUp().forEach { it.delete() }
//                        println("✅ Build folder cleaned successfully.")
//                    } catch (e: Exception) {
//                        println("❌ Failed to delete: ${e.message}")
//                    }
//                }
//            }
//        }
//
//// Ensure it runs before builds
//tasks.named("preBuild").configure {
//    dependsOn("smartAutoClean")
//}


