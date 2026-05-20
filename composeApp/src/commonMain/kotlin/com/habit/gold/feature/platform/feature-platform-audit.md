# Phase 12: Platform Integrations Audit

## Audit Status

- Status: `strict audit complete, later implementation slices documented`
- Branch: `codex/feature/phase12-platform-integrations`
- Implementation state: `latest verified slice implemented`

## Audit Goal

Lock the exact platform-owned boundaries before any Phase 12 implementation so SDK wiring does not drift into shared UI/business logic and so environment/storage/notification work is done once, not repeatedly patched.

## Source Inventory Reviewed

### Current KMP Project

- `composeApp/build.gradle.kts`
- `composeApp/src/commonMain/kotlin/com/habit/gold/core/config/AppConfig.kt`
- `composeApp/src/androidMain/kotlin/com/habit/gold/core/config/PlatformConfig.android.kt`
- `composeApp/src/iosMain/kotlin/com/habit/gold/core/config/PlatformConfig.ios.kt`
- `composeApp/src/commonMain/kotlin/com/habit/gold/core/storage/*`
- `composeApp/src/commonMain/kotlin/com/habit/gold/core/session/SessionStore.kt`
- `composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/components/AuthOtpScreen.kt`
- `composeApp/src/commonMain/kotlin/com/habit/gold/app/AppShellScreen.kt`
- `iosApp/Configuration/*.xcconfig`
- `iosApp/iosApp/Info.plist`
- `iosApp/iosApp/iOSApp.swift`

### Legacy Android Source

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/build.gradle.kts`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/AndroidManifest.xml`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/HabitGoldApplication.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/MainActivity.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/OtpScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/local/DataStoreManager.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/notifications/DeviceTokenSync.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/notifications/HabitGoldMessagingService.kt`

## Decision Summary

### 1. Environment Strategy

Decision:

- Android keeps `staging`, `preprod`, `prod` product flavors.
- iOS should mirror Android with explicit `Debug`, `Preprod`, and `Release` configurations plus shared schemes for fast switching.
- Recommended iOS strategy: `Staging`, `Preprod`, `Prod` configurations backed by xcconfig files, not ad hoc runtime toggles.

Why:

- KMP Android already has three environments in `composeApp/build.gradle.kts`.
- iOS now exposes `APP_ENV=staging` in `Debug.xcconfig`, `APP_ENV=preprod` in `Preprod.xcconfig`, and `APP_ENV=prod` in `Release.xcconfig`.
- Shared `iosApp Preprod` and `iosApp Prod` schemes now make switching visible in Xcode instead of relying on local user schemes.

Required implementation:

- keep `Preprod.xcconfig`
- wire matching Xcode build configuration(s)
- align Info.plist-driven `APP_ENV` / `API_BASE_URL`

### 2. App IDs / Bundle IDs / Display Names

Decision:

- Android and iOS should align environment-specific display names.
- non-prod Android builds should use flavor-specific app-id suffixes for coexistence.
- iOS preprod should use its own bundle id so prod and preprod can coexist on device.

Current KMP state:

- Android:
  - base app id from `ANDROID_APP_ID`, default `com.habit.gold`
  - staging suffix: `.staging`
  - preprod suffix: `.preprod`
  - debug suffix only: `.debug`
- iOS:
  - prod bundle id: `com.habit.gold`
  - preprod bundle id: `com.habit.gold.preprod`
  - explicit preprod product/app naming in `Preprod.xcconfig`
  - current display names:
    - staging: `Staging HabitGold`
    - preprod: `Preprod HabitGold`
    - prod: `HabitGold`

Current gap:

- staging iOS still shares the base bundle id today
- final product call is still needed on whether staging should remain developer-only or get its own installable bundle id

### 2A. App Icon / Launcher Branding

Decision:

- app icons stay platform-specific resources but should align visually across Android and iOS
- Android should reuse the verified launcher assets from the legacy Android project
- iOS should use a complete Apple-style asset catalog export instead of a hand-assembled partial icon set

Current KMP state:

- Android launcher assets now mirror the legacy Android launcher icon set, including adaptive foreground/background/monochrome layers
- iOS `AppIcon.appiconset` now uses the IconKitchen-exported HabitGold icon pack

Remaining caution:

- if iOS system surfaces still show stale icons after reinstall, inspect the compiled asset catalog in the built `.app` bundle rather than only source assets

### 3. Version Strategy

Decision:

- KMP Android versioning must be aligned before production rollout.
- Shared `x-app-version` headers must reflect a production-real version strategy, not placeholder `1.0`.

Current KMP state:

- Android KMP:
  - `versionCode = 1`
  - `versionName = "1.0"`
- Legacy Android:
  - `versionCode = 19`
  - `versionName = "1.0.19"`

Required implementation:

- align composeApp Android versioning with the intended rollout number strategy
- align iOS `CURRENT_PROJECT_VERSION` and `MARKETING_VERSION`
- verify shared header parity through `AppConfig`

### 4. Secure Storage

Decision:

- current shared storage boundary is correct, but both platform actuals are not secure enough for Phase 12 completion.
- keep the shared `SecureStorage` contract and replace platform implementations only.

Current KMP state:

- Android secure storage actual uses plain `SharedPreferences`
- iOS secure storage actual uses `NSUserDefaults`

Audit conclusion:

- This is acceptable as an early scaffold, but not acceptable for a completed platform-integration phase.

Required implementation:

- Android: encrypted storage backed by Android Keystore
- iOS: Keychain-backed storage
- preserve the same shared interfaces:
  - `SecureStorage`
  - `AuthTokenStorage`
  - `ProfileSecurityStore`

### 5. Android OTP Auto-Read / iOS OTP Autofill

Decision:

- Android SMS Retriever belongs in Phase 12 platform code and should feed the shared auth OTP state through a small bridge.
- iOS should not try to mimic Android SMS Retriever; document and support OTP autofill expectations separately.

Android source findings:

- legacy Android `OtpScreen.kt`:
  - starts `SmsRetriever`
  - registers `BroadcastReceiver`
  - extracts six-digit OTP
  - auto-fills and auto-submits

Current KMP state:

- shared `AuthOtpScreen.kt` only supports manual entry
- no Android actual bridge exists
- iOS OTP autofill behavior was undocumented before this Phase 12 pass

Required implementation:

- Android:
  - add `OtpAutoFillController` / similar platform bridge
  - deliver six-digit code to shared auth flow
- iOS:
  - document native OTP autofill expectations explicitly:
    - Android uses automatic SMS retrieval where supported
    - iOS relies on the system OTP keyboard suggestion / autofill behavior
  - document `UITextContentType.oneTimeCode` / native autofill expectations
  - decide whether Compose Multiplatform text fields can surface the needed platform hint cleanly or need a platform wrapper

### 6. Push Notifications And Device Tokens

Decision:

- notification registration contract should be shared
- push receive callbacks remain platform-owned
- alert persistence stays shared/local-storage-backed

Android source findings:

- Firebase Messaging service exists
- `DeviceTokenSync`:
  - registers token after login
  - updates token on refresh if logged in
  - unregisters token before logout
- `HabitGoldMessagingService`:
  - shows notifications
  - persists alerts into local storage

Current KMP state:

- shared Alerts feature exists
- Android FCM service, token sync, and alert persistence are now wired
- shared device-token registration contract is now wired
- iOS APNs hooks are now wired
- iOS Firebase Messaging delegate + FCM token capture are now wired

Remaining implementation:

- iOS:
  - final push capability / entitlement verification
  - on-device APNs token + alert persistence verification
  - confirm FCM registration token reaches backend successfully after login
  - note that personal-team signed local builds cannot verify real APNs delivery while using empty dev entitlements

### 7. Deep Links And Referral Capture

Decision:

- deep-link parsing and referral attribution state should be shared
- OS entry hooks remain platform-owned

Android source findings:

- manifest handles:
  - `habitgold://refer`
  - `https://habitgold.com/refer`
- `MainActivity` captures referral data from `intent`
- `ReferralAttributionManager` captures both deep-link and install-referrer paths

Current KMP state:

- Rewards uses referral links/QR output
- shared auth still documents pending referral capture as deferred
- no deep-link entry hooks in current KMP Android/iOS shell
- iOS app delegate only handles Juspay redirect URLs

Required implementation:

- shared:
  - referral-attribution contract
  - pending referral persistence target
- Android:
  - app intent capture
  - install-referrer capture bridge
- iOS:
  - custom-scheme and universal-link capture
  - explicit decision on attribution non-equivalent areas

### 8. Install Referrer / iOS Attribution

Decision:

- Android Install Referrer integration should be ported.
- iOS does not need a fake equivalent; document the non-equivalent path if no trustworthy source exists.

Android source findings:

- `DataStoreManager` persists `install_referrer_captured`
- `MainActivity` calls `captureInstallReferrerOnce()`

Current KMP state:

- no install-referrer integration
- no shared attribution-complete marker

Required implementation:

- add shared attribution state storage
- Android bridge updates shared state once captured
- iOS explicitly documents the absence or alternate attribution strategy

### 9. Clarity / Firebase / Crash / Performance

Decision:

- Android should retain Firebase + Clarity parity because the legacy app already relies on them.
- iOS should also retain Firebase parity, with device/runtime verification kept as the remaining step.
- iOS Clarity wiring can remain in place, but it should not block Phase 12 closure while product/SDK fit is revisited.

Android source findings:

- plugins:
  - `com.google.gms.google-services`
  - `com.google.firebase.crashlytics`
  - `com.google.firebase.firebase-perf`
- `HabitGoldApplication`:
  - initializes Firebase
  - conditionally initializes Clarity
- `MainActivity` updates current Clarity screen name

Current KMP state:

- no Firebase plugins in composeApp Android module
- no Clarity project-id wiring in composeApp
- no iOS analytics/crash/performance hooks

Required implementation:

- Android:
  - Firebase Messaging plugin/runtime setup
  - Crashlytics plugin/runtime setup
  - Firebase Performance setup
  - Clarity init + screen-name updates
- iOS:
  - explicit parity decision for crash/performance/notification SDKs
  - Clarity integration and project-id wiring

## Must-Match Android Behavior

- Android flavor env names: `staging`, `preprod`, `prod`
- Android and iOS product naming now uses:
  - `Staging HabitGold`
  - `Preprod HabitGold`
  - `HabitGold`
- manifest deep-link hosts/schemes for referral entry
- Android OTP auto-read behavior
- FCM token registration lifecycle:
  - after login
  - on token refresh
  - before logout unregister
- alert persistence from push receive path
- Clarity conditional init using env/build config

## Things That Must Not Be Faked

- secure storage must not stay on plain preferences/defaults
- iOS push handling must not be documented as “done” without APNs registration hooks
- referral attribution must not be approximated only from QR/share URLs
- preprod env parity must not be implied on iOS until an actual configuration exists

## Recommended Package / Ownership Structure

- `core/config`
  - environment/version/app-id strategy
- `core/storage`
  - secure storage actuals
- `core/platform/notifications`
  - token registration contract
  - platform callbacks
- `core/platform/auth`
  - OTP auto-read/autofill bridge
- `core/platform/referral`
  - deep-link/referrer capture bridge
- `core/platform/analytics`
  - Clarity / Firebase / crash/perf initialization boundary

## Implementation Order

1. environment + version parity
2. secure storage hardening
3. OTP bridge
4. push token registration + alerts bridge
5. deep-link + referral capture
6. install-referrer / iOS attribution decision
7. Clarity / Firebase / crash-performance wiring

## Testing Expectations

- Android and iOS environment values resolve correctly from build config / plist
- secure storage survives restart and is not plain shared prefs/defaults for sensitive values
- Android OTP auto-read fills shared auth OTP state correctly
- iOS OTP autofill expectations are documented and verified
- FCM/APNs token registration follows login/logout lifecycle
- push notifications persist Alerts entries correctly
- Android deep links and iOS deep links both feed shared referral attribution
- install-referrer capture is idempotent
- Clarity / Firebase init is gated correctly by env/build config

## Deliverables Before Implementation Can Be Called Complete

- `feature-platform-overview.md`
- this audit doc
- updated roadmap/progress docs
- explicit iOS parity decisions where Android-only integrations remain non-equivalent
  - install-referrer style attribution remains the one open non-equivalent decision

## Current Implementation Checkpoint

Verified in this branch:

- `./gradlew :composeApp:compilePreprodDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinIosSimulatorArm64`
- `./gradlew :composeApp:lintPreprodDebug`

Current reality after implementation:

- Android:
  - env/version/app-config parity is materially improved
  - encrypted secure storage is live
  - SMS Retriever bridge is live
  - referral deep links, install referrer, FCM token capture, alert persistence, Clarity hooks, and Firebase plugin/runtime parity are live
- iOS:
  - Clarity hooks are wired, though practical SDK fit is still deferred from phase closure
  - APNs registration/token capture, Firebase Messaging FCM token capture, and referral URL capture hooks are live
  - alert persistence into shared alert storage keys is live
  - preprod config file and Xcode/shared-scheme wiring are live
  - environment Firebase plist selection is live
  - Keychain-backed secure storage is live
  - app naming and icon branding alignment are live
  - project-level associated domains / push capability setup is live
- Firebase pods are wired on iOS, but device/runtime verification is still pending
