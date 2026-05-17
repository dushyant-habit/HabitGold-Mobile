## Summary

- 

## Scope

- 

## Verification

- [ ] `./gradlew :composeApp:allTests`
- [ ] `./gradlew :composeApp:compilePreprodDebugKotlinAndroid`
- [ ] `./gradlew :composeApp:lintPreprodDebug`
- [ ] `./gradlew :composeApp:compileKotlinIosSimulatorArm64`
- [ ] `xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build`

## Review Focus

- Android parity:
- iOS behavior:
- State / navigation / back handling:
- Loading / polling / result states:
- Docs updated:

## Deferred Items

- 
