# Phase 12: Platform Integrations Overview

## Status

- Current status: `latest implementation and code-quality slice live, phase still in progress`
- Branch: `codex/feature/phase12-platform-integrations-pr-clean`

## Scope

Phase 12 is the platform-integration phase for items that must stay partly or fully platform-owned even after feature migration is shared.

Primary scope:

- Android / iOS environment configuration parity
- app id / bundle id / display name alignment
- app icon / launcher-branding alignment
- versioning and app-version header parity
- secure storage hardening
- Android OTP auto-read and iOS OTP autofill expectations
- push notifications and device-token registration
- deep links and referral capture
- Android Install Referrer and iOS attribution decision
- Android Clarity / Firebase integration
- iOS parity decision for notifications / crash / performance / analytics SDKs

## Key Audit Decisions

- Phase 12 should **not** be one giant feature package with screen UI. It is mostly:
  - `core/config`
  - `core/storage`
  - `core/platform`
  - small platform-owned entry hooks in Android and iOS app bootstrap
- Shared code should own the contracts and persistence targets where possible, but the following stay platform-owned:
  - Android SMS Retriever
  - Android FCM service callbacks
  - Android Install Referrer client
  - Android runtime notification permission request
  - iOS APNs registration callbacks
- iOS universal-link / custom-scheme entry hooks
- iOS OTP autofill behavior
- Android notification-token registration and referral capture should be exposed to shared code through small platform bridges, not duplicated feature logic.
- iOS needs the same platform-bridge discipline as Android, but its APNs and referral hooks are now partially wired in the shared app bootstrap.

## Current KMP Reality

Already present:

- Android flavor split: `staging`, `preprod`, `prod`
- iOS config split: `Debug`, `Preprod`, and `Release` xcconfig-backed configurations with shared schemes
- shared app-config contract
- shared session restore
- shared secure-storage abstraction boundary
- Android/iOS Juspay integration
- shared biometric flow using the current secure-storage boundary

Missing or incomplete:

- iOS OTP autofill remains expectation/documentation-driven, not special SMS interception
- iOS Firebase runtime still needs final environment-level verification on device
- iOS APNs / associated-domain behavior still needs final on-device verification
- iOS Crashlytics / Performance verification is still pending after pod/runtime wiring
- iOS local personal-team builds still cannot verify real APNs / associated-domain delivery because they must use empty dev entitlements for signing
- iOS attribution still needs either a real equivalent path or an explicit documented non-equivalent decision

## Implemented In This Slice

- Android app version parity updated to `1.0.19 (19)`
- iOS base version parity updated to `1.0.19 (19)`
- Android `staging` / `preprod` builds now use flavor-specific application IDs for side-by-side install
- iOS `Preprod` configuration is now wired into Xcode with shared `iOSAppStaging` / `iOSAppPreprod` / `iOSAppProd` schemes
- Android and iOS display names are now aligned for local switching:
  - `Staging HabitGold`
  - `Preprod HabitGold`
  - `HabitGold`
- Android launcher icons now match the legacy Android project assets
- iOS app icon catalog now uses the current IconKitchen-exported HabitGold icon set
- Android secure storage moved to encrypted shared preferences
- iOS secure storage moved to Keychain-backed storage
- shared OTP screen now supports Android SMS Retriever autofill
- OTP platform rule is now explicit:
  - Android uses automatic SMS retrieval where supported
  - iOS relies on the system OTP keyboard suggestion / autofill flow
- Android Firebase plugin/runtime parity is now wired:
  - Google Services
  - Crashlytics
  - Firebase Performance
  - environment `google-services.json` assets
- shared platform bridge store added for:
  - pending referral code
  - install-referrer captured state
  - current device token
  - last registered device token
- shared device-token sync manager added
- shared alert recorder added for platform push callbacks
- Android app bootstrap now includes:
  - storage init in `Application`
  - referral deep-link capture
  - install referrer capture
  - notification permission request
  - initial FCM token sync
  - FCM messaging service
  - Clarity init and shared route screen naming
- iOS app delegate now includes:
  - earlier Firebase bootstrap from the SwiftUI app initializer plus idempotent app-delegate startup wiring
  - Clarity init gated by `ENABLE_CLARITY` + `CLARITY_PROJECT_ID`
  - APNs permission request and registration
  - APNs token capture without storing the raw APNs token as the backend registration candidate
  - Firebase Messaging delegate + FCM registration-token capture
  - custom-scheme referral capture through the shared referral parser
  - universal-link referral capture through the shared referral parser
  - alert persistence routed into the shared alerts recorder bridge
  - Firebase plist selection by `APP_ENV`
    - `prod` -> `GoogleService-Info-Prod.plist`
    - `preprod` / `staging` -> `GoogleService-Info-Staging.plist`
  - Crashlytics env/device-token logging hooks
  - Firebase Performance enablement hooks
  - push + associated-domain entitlements via `iosApp.entitlements`
- Phase 12 cleanup pass is now also live:
  - shared referral parsing extracted so Android and iOS no longer drift
  - iOS runtime bridge extracted so Swift no longer hardcodes shared storage keys
  - device-token sync now only marks registration/unregistration complete on real `2xx` responses
  - targeted regression tests added for referral parsing and device-token sync behavior

## Android Source Of Truth

Primary Android references:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/build.gradle.kts`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/AndroidManifest.xml`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/HabitGoldApplication.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/MainActivity.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/OtpScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/local/DataStoreManager.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/notifications/DeviceTokenSync.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/notifications/HabitGoldMessagingService.kt`

## iOS Source Of Truth

Current KMP iOS references:

- `iosApp/Configuration/Base.xcconfig`
- `iosApp/Configuration/Debug.xcconfig`
- `iosApp/Configuration/Preprod.xcconfig`
- `iosApp/Configuration/Release.xcconfig`
- `iosApp/iosApp.xcodeproj/xcshareddata/xcschemes/iOSAppStaging.xcscheme`
- `iosApp/iosApp.xcodeproj/xcshareddata/xcschemes/iOSAppPreprod.xcscheme`
- `iosApp/iosApp.xcodeproj/xcshareddata/xcschemes/iOSAppProd.xcscheme`
- `iosApp/iosApp/GoogleService-Info-Staging.plist`
- `iosApp/iosApp/GoogleService-Info-Prod.plist`
- `iosApp/iosApp/Info.plist`
- `iosApp/iosApp/iOSApp.swift`

Current finding:

- iOS bootstrap now handles:
  - Juspay redirect URLs
  - APNs permission + token registration
  - Firebase Messaging token capture and shared token persistence
  - notification delegate persistence into shared alerts
  - custom-scheme referral capture
  - universal-link referral capture
- remaining iOS gaps are now:
  - on-device Firebase / Crashlytics / Performance verification
  - on-device APNs / associated-domain verification
  - iOS attribution equivalent or explicit documented non-equivalent path
  - paid-team entitlement/provisioning verification for real push delivery

## Implementation Order

1. environment and version parity
2. secure storage hardening
3. OTP platform behavior
4. push notifications + device-token contract
5. deep-link + referral capture
6. Install Referrer / iOS attribution decision
7. Clarity / Firebase / crash-performance parity decisions and wiring

## Mandatory Rules For Implementation

- Do not start SDK wiring before the platform entry hooks are documented.
- Keep shared contracts small and explicit; platform code should push results back into shared state instead of owning product logic.
- Do not reuse plain preferences as “secure storage” once Phase 12 starts implementing hardening.
- For iOS integrations, verify the actual `.xcworkspace` runtime path when CocoaPods-backed SDKs are involved.
