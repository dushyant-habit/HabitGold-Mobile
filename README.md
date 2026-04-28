## HabitGold Mobile

This repository is set up as a production-grade Kotlin Multiplatform starter for Android and iOS.

### What is included

- Shared Compose UI in `composeApp/src/commonMain`
- Common app configuration contract for both platforms
- Koin-based dependency bootstrap
- Ktor HTTP client with Android and iOS engines
- Android release hardening with shrinking, backup restrictions, and HTTPS-only network policy
- iOS SwiftUI host that initializes the shared dependency graph before rendering Compose

### Project structure

- `composeApp/src/commonMain/kotlin/com/habit/gold/core`
  Shared config, DI, networking, and utilities
- `composeApp/src/commonMain/kotlin/com/habit/gold/feature`
  Shared feature presentation logic
- `composeApp/src/androidMain`
  Android application bootstrap, manifest, and platform config
- `composeApp/src/iosMain`
  iOS platform config and Darwin HTTP engine binding
- `iosApp/iosApp`
  SwiftUI container that renders the shared Compose UI

### Build Android

```shell
./gradlew :composeApp:assembleDebug
```

### Build iOS framework

```shell
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### Configuration

Android build values live in `gradle.properties`:

- `APP_NAME`
- `ANDROID_APP_ID`
- `ANDROID_APP_ENV_DEBUG`
- `ANDROID_APP_ENV_RELEASE`
- `ANDROID_API_BASE_URL_DEBUG`
- `ANDROID_API_BASE_URL_RELEASE`

The iOS default config lives in `composeApp/src/iosMain/kotlin/com/habit/gold/core/config/PlatformConfig.ios.kt`.
