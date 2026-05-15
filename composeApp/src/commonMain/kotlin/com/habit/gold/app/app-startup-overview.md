# App Startup Overview

This file documents the shared startup and splash behavior for the HabitGold KMP app.

Use it together with:

- [HABITGOLD_SOURCE_OF_TRUTH.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/HABITGOLD_SOURCE_OF_TRUTH.md:1)
- [KMP_PROGRESS_TRACKER.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROGRESS_TRACKER.md:1)

## Scope

This area owns:

- native launch presentation on Android and iOS
- shared Compose splash content
- startup session restore
- top-level route handoff into auth or the main shell

This area does not own:

- auth flow details
- home feature content
- platform SDK integrations beyond launch/bootstrap concerns

## Status

Status: `In progress`

Current state:

- shared startup routing is complete
- native launch hooks exist on Android and iOS
- first branded splash implementation exists

Still pending before startup/splash can be considered complete:

- final visual parity review on real iOS and Android runs
- iOS native launch polish beyond the current minimal branded launch screen
- final transition review between native launch and shared Compose splash
- final decision on whether any additional startup animation or branding refinement is needed

## Startup Layers

Startup is intentionally split into two layers.

### 1. Native launch layer

Purpose:

- prevent blank or mismatched startup flashes before Compose is ready
- make iOS and Android feel polished from process launch

Current implementation:

- Android launch theme:
  - [AndroidManifest.xml](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/androidMain/AndroidManifest.xml:1)
  - [themes.xml](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/androidMain/res/values/themes.xml:1)
  - [themes.xml](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/androidMain/res/values-v31/themes.xml:1)
- iOS launch screen:
  - [Info.plist](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/iosApp/iosApp/Info.plist:1)
  - [LaunchScreen.storyboard](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/iosApp/iosApp/LaunchScreen.storyboard:1)

Rules:

- native launch should be visually compatible with the shared splash
- do not put business logic in native launch files
- keep native launch assets platform-owned

### 2. Shared Compose splash layer

Purpose:

- show the branded HabitGold startup experience while shared session restore and route decisions happen

Current implementation:

- [AppSplashScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/app/AppSplashScreen.kt:1)
- [AppRoot.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/app/AppRoot.kt:1)
- [AppRootViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/app/AppRootViewModel.kt:1)
- [AppStartupCoordinator.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/app/AppStartupCoordinator.kt:1)

Current visual goals:

- Android-inspired purple splash surface
- centered brand mark
- tagline
- loading badge
- no white flash between launch and Compose

## Route Flow

Shared startup route flow:

1. native launch screen appears
2. shared app boots
3. `SessionStore.restore()` resolves persisted session state
4. `AppStartupCoordinator` selects the destination
5. app routes to:
   - splash while loading
   - auth when logged out or onboarding is required
   - main shell when session is valid

## Resource Ownership

### Shared

- shared splash strings:
  - [strings.xml](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/composeResources/values/strings.xml:1)
- shared splash brand mark:
  - [ic_habit_gold_white_transparent_bg.xml](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/composeResources/drawable/ic_habit_gold_white_transparent_bg.xml:1)

### Platform-specific

- Android splash theme resources:
  - [colors.xml](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/androidMain/res/values/colors.xml:1)
  - [ic_splash_transparent.xml](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/androidMain/res/drawable/ic_splash_transparent.xml:1)
- iOS native launch layout:
  - [LaunchScreen.storyboard](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/iosApp/iosApp/LaunchScreen.storyboard:1)

## Important Rules

- startup and splash should be treated as part of app architecture, not as throwaway UI
- if native launch changes, shared splash should be checked too
- if shared splash copy changes, update `composeResources` in the same task
- Android and iOS launch layers should stay thin and static
- route decisions must stay in shared code

## Verification

Latest verified on: `2026-05-15`

Commands:

```bash
./gradlew :composeApp:allTests :composeApp:compileKotlinIosSimulatorArm64
```

Notes:

- if iOS startup fails before Compose, check `.xcconfig`-driven env values first
- if Xcode still shows the old generated launch screen, clean the build folder and rerun
- this area should currently be treated as usable but not finished
