import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.example.issue5083repro"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.issue5083repro"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            configure<CrashlyticsExtension> {
                nativeSymbolUploadEnabled = true
                unstrippedNativeLibsDir = "build/intermediates/merged_native_libs/release/out/lib"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
//    WORKAROUND-2: Restoreing this block also fixes the issue
//    externalNativeBuild {
//        cmake {
//            path = file("src/main/cpp/CMakeLists.txt")
//            version = "3.22.1"
//        }
//    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":nativelib"))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// WORKAROUND-1: Restoreing this block also fixes the issue
//tasks.forEach {task ->
//    if (task.name.startsWith("injectCrashlyticsBuildIds")) {
//        task.dependsOn("merge" + Regex("^injectCrashlyticsBuildIds(.+)$").matchEntire(task.name)!!.groups[0]!!.value + "NativeLibs")
//    }
//}