import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

val appName = providers.gradleProperty("APP_NAME").getOrElse("HabitGold").trim()
val androidAppId = providers.gradleProperty("ANDROID_APP_ID").getOrElse("com.habit.gold").trim()
val clarityProjectId = providers.gradleProperty("CLARITY_PROJECT_ID").getOrElse("").trim()
val enableClarity = providers.gradleProperty("ENABLE_CLARITY").getOrElse("false").toBoolean()
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
            .getOrElse("https://preprod.habitgold.com/v1/")
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
val stagingClarityProjectId = providers.gradleProperty("CLARITY_PROJECT_ID_STAGING")
    .getOrElse(clarityProjectId)
    .trim()
val preprodClarityProjectId = providers.gradleProperty("CLARITY_PROJECT_ID_PREPROD")
    .getOrElse(clarityProjectId)
    .trim()
val prodClarityProjectId = providers.gradleProperty("CLARITY_PROJECT_ID_PROD")
    .getOrElse(clarityProjectId)
    .trim()
val enableClarityStaging = providers.gradleProperty("ENABLE_CLARITY_STAGING")
    .getOrElse(enableClarity.toString())
    .toBoolean()
val enableClarityPreprod = providers.gradleProperty("ENABLE_CLARITY_PREPROD")
    .getOrElse(enableClarity.toString())
    .toBoolean()
val enableClarityProd = providers.gradleProperty("ENABLE_CLARITY_PROD")
    .getOrElse(enableClarity.toString())
    .toBoolean()
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
            implementation(libs.androidx.core.ktx)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.okhttp)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.1.0"))
            implementation("androidx.biometric:biometric:1.1.0")
            implementation("androidx.fragment:fragment-ktx:1.8.9")
            implementation("androidx.security:security-crypto:1.1.0")
            implementation("com.android.installreferrer:installreferrer:2.2")
            implementation("com.google.android.gms:play-services-auth-api-phone:18.3.0")
            implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")
            implementation("com.google.firebase:firebase-crashlytics")
            implementation("com.google.firebase:firebase-perf")
            implementation("com.microsoft.clarity:clarity-compose:3.8.2")
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
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation("io.github.g0dkar:qrcode-kotlin:4.5.0")
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
        versionCode = 23
        versionName = "1.0.23"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "APP_NAME", "\"$appName\"")
        buildConfigField("String", "APP_ENV", "\"prod\"")
        buildConfigField("String", "API_BASE_URL", "\"$prodBaseUrl\"")
        buildConfigField("Boolean", "ENABLE_NETWORK_LOGS", "false")
        buildConfigField("Boolean", "CLARITY_ENABLED", "$enableClarityProd")
        buildConfigField("String", "CLARITY_PROJECT_ID", "\"$prodClarityProjectId\"")
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
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            resValue("string", "app_name", "Staging HabitGold")
            buildConfigField("String", "APP_NAME", "\"Staging HabitGold\"")
            buildConfigField("String", "APP_ENV", "\"staging\"")
            buildConfigField("String", "API_BASE_URL", "\"$stagingBaseUrl\"")
            buildConfigField("Boolean", "CLARITY_ENABLED", "$enableClarityStaging")
            buildConfigField("String", "CLARITY_PROJECT_ID", "\"$stagingClarityProjectId\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"$juspayEnvironmentStaging\"")
        }
        create("preprod") {
            dimension = "environment"
            applicationIdSuffix = ".preprod"
            versionNameSuffix = "-preprod"
            resValue("string", "app_name", "Preprod HabitGold")
            buildConfigField("String", "APP_NAME", "\"Preprod HabitGold\"")
            buildConfigField("String", "APP_ENV", "\"preprod\"")
            buildConfigField("String", "API_BASE_URL", "\"$preprodBaseUrl\"")
            buildConfigField("Boolean", "CLARITY_ENABLED", "$enableClarityPreprod")
            buildConfigField("String", "CLARITY_PROJECT_ID", "\"$preprodClarityProjectId\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"$juspayEnvironmentPreprod\"")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "HabitGold")
            buildConfigField("String", "APP_NAME", "\"HabitGold\"")
            buildConfigField("String", "APP_ENV", "\"prod\"")
            buildConfigField("String", "API_BASE_URL", "\"$prodBaseUrl\"")
            buildConfigField("Boolean", "CLARITY_ENABLED", "$enableClarityProd")
            buildConfigField("String", "CLARITY_PROJECT_ID", "\"$prodClarityProjectId\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"$juspayEnvironmentProd\"")
        }
    }
    buildTypes {
        getByName("debug") {
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
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.10.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.10.5")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}
