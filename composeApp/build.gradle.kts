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
val juspayClientId = providers.gradleProperty("JUSPAY_CLIENT_ID").getOrElse("").trim()
val juspayMerchantId = providers.gradleProperty("JUSPAY_MERCHANT_ID").getOrElse("").trim()
val juspayRoutingId = providers.gradleProperty("JUSPAY_ROUTING_ID").getOrElse("").trim()
val juspayEnvironment = providers.gradleProperty("JUSPAY_ENVIRONMENT").getOrElse("").trim()
val juspaySdkVersion = providers.gradleProperty("JUSPAY_SDK_VERSION").getOrElse("").trim()
fun normalizedBaseUrl(value: String): String = value.trim().removeSuffix("/") + "/"
fun requireHttpsUrl(propertyName: String, value: String): String {
    require(value.startsWith("https://")) {
        "$propertyName must use HTTPS. Found: $value"
    }
    return value
}
val stagingBaseUrl = requireHttpsUrl(
    "STAGING_API_BASE_URL",
    normalizedBaseUrl(
        providers.gradleProperty("STAGING_API_BASE_URL")
            .orElse(providers.gradleProperty("ANDROID_API_BASE_URL_DEBUG"))
            .getOrElse("https://staging.habitgold.com/v1/")
    )
)
val preprodBaseUrl = requireHttpsUrl(
    "PREPROD_API_BASE_URL",
    normalizedBaseUrl(
        providers.gradleProperty("PREPROD_API_BASE_URL")
            .getOrElse(stagingBaseUrl)
    )
)
val prodBaseUrl = requireHttpsUrl(
    "PROD_API_BASE_URL",
    normalizedBaseUrl(
        providers.gradleProperty("PROD_API_BASE_URL")
            .orElse(providers.gradleProperty("ANDROID_API_BASE_URL_RELEASE"))
            .getOrElse("https://api.habitgold.com/v1/")
    )
)
val juspayEnvironmentStaging = providers.gradleProperty("JUSPAY_ENVIRONMENT_STAGING")
    .getOrElse(juspayEnvironment.ifBlank { "sandbox" })
    .trim()
val juspayEnvironmentPreprod = providers.gradleProperty("JUSPAY_ENVIRONMENT_PREPROD")
    .getOrElse(juspayEnvironmentStaging)
    .trim()
val juspayEnvironmentProd = providers.gradleProperty("JUSPAY_ENVIRONMENT_PROD")
    .getOrElse(juspayEnvironment.ifBlank { "production" })
    .trim()
val enableJuspayPlugin = providers.gradleProperty("ENABLE_JUSPAY_PLUGIN").getOrElse("false").toBoolean()
val isJuspayConfigured = juspayClientId.isNotBlank() && !juspayClientId.startsWith("REPLACE_WITH_")

if (enableJuspayPlugin && isJuspayConfigured) {
    apply(plugin = "hypersdk.plugin")
}

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
            implementation("androidx.biometric:biometric:1.1.0")
            implementation("androidx.fragment:fragment-ktx:1.6.2")
            implementation("com.google.zxing:core:3.5.4")
        }
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(compose.materialIconsExtended)
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
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
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
        buildConfigField("String", "APP_ENV", "\"prod\"")
        buildConfigField("String", "API_BASE_URL", "\"$prodBaseUrl\"")
        buildConfigField("Boolean", "ENABLE_NETWORK_LOGS", "false")
        buildConfigField("String", "JUSPAY_CLIENT_ID", "\"$juspayClientId\"")
        buildConfigField("String", "JUSPAY_MERCHANT_ID", "\"$juspayMerchantId\"")
        buildConfigField("String", "JUSPAY_ROUTING_ID", "\"$juspayRoutingId\"")
        buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"$juspayEnvironmentProd\"")
        buildConfigField("Boolean", "JUSPAY_ENABLED", "$enableJuspayPlugin")
    }
    flavorDimensions += "environment"
    productFlavors {
        create("staging") {
            dimension = "environment"
            versionNameSuffix = "-staging"
            resValue("string", "app_name", "HabitGold Staging")
            buildConfigField("String", "APP_ENV", "\"staging\"")
            buildConfigField("String", "API_BASE_URL", "\"$stagingBaseUrl\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"$juspayEnvironmentStaging\"")
        }
        create("preprod") {
            dimension = "environment"
            versionNameSuffix = "-preprod"
            resValue("string", "app_name", "HabitGold Preprod")
            buildConfigField("String", "APP_ENV", "\"preprod\"")
            buildConfigField("String", "API_BASE_URL", "\"$preprodBaseUrl\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"$juspayEnvironmentPreprod\"")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "HabitGold")
            buildConfigField("String", "APP_ENV", "\"prod\"")
            buildConfigField("String", "API_BASE_URL", "\"$prodBaseUrl\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"$juspayEnvironmentProd\"")
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
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

if (enableJuspayPlugin && isJuspayConfigured) {
    extensions.configure<Any>("hyperSdkPlugin") {
        withGroovyBuilder {
            setProperty("clientId", juspayClientId)
            setProperty("sdkVersion", juspaySdkVersion)
        }
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
