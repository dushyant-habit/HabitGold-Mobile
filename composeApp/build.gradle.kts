import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

val appName = providers.gradleProperty("APP_NAME").getOrElse("HabitGold").trim()
val androidAppId = providers.gradleProperty("ANDROID_APP_ID").getOrElse("com.habit.gold").trim()
val androidDebugEnvironment = providers.gradleProperty("ANDROID_APP_ENV_DEBUG").getOrElse("development").trim()
val androidReleaseEnvironment = providers.gradleProperty("ANDROID_APP_ENV_RELEASE").getOrElse("production").trim()
val androidDebugBaseUrl = providers.gradleProperty("ANDROID_API_BASE_URL_DEBUG")
    .getOrElse("https://staging.habitgold.com/v1/")
    .trim()
val androidReleaseBaseUrl = providers.gradleProperty("ANDROID_API_BASE_URL_RELEASE")
    .getOrElse("https://api.habitgold.com/v1/")
    .trim()

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            binaryOption("bundleId", "com.habit.gold.shared")
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinxJson)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.habit.gold"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = androidAppId
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "APP_NAME", "\"$appName\"")
        buildConfigField("String", "APP_ENV", "\"$androidReleaseEnvironment\"")
        buildConfigField("String", "API_BASE_URL", "\"$androidReleaseBaseUrl\"")
        buildConfigField("Boolean", "ENABLE_NETWORK_LOGS", "false")
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("String", "APP_ENV", "\"$androidDebugEnvironment\"")
            buildConfigField("String", "API_BASE_URL", "\"$androidDebugBaseUrl\"")
            buildConfigField("Boolean", "ENABLE_NETWORK_LOGS", "true")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
