# Auth Feature

This file is the feature-level source of truth for shared auth in the KMP app.

Use this document when you need to understand:

- what the auth feature does
- which APIs it calls
- how the UI flow is structured
- how session state changes across the flow
- which Android behaviors must be preserved
- where to update code when auth changes

Project-wide rules still live in:

- [HABITGOLD_SOURCE_OF_TRUTH.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/HABITGOLD_SOURCE_OF_TRUTH.md:1)
- [KMP_PROJECT_GUIDE.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROJECT_GUIDE.md:1)

## Goal

The shared auth feature currently covers:

- `Login`
- `OTP verification`
- `Basic Details / onboarding`
- `Session handoff into the app shell`

The goal is to keep the auth flow behavior aligned with the working Android app while using the KMP architecture we defined for the new codebase.

## Folder Structure

```text
feature/auth/
  data/
    model/
    remote/
    repository/
  di/
  domain/
    usecase/
  presentation/
    components/
```

Main files:

- contract: [AuthContract.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/AuthContract.kt:1)
- viewmodel: [AuthFlowViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/AuthFlowViewModel.kt:1)
- screen router: [AuthFlowScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/AuthFlowScreen.kt:1)
- repository implementation: [AuthRepositoryImpl.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/data/repository/AuthRepositoryImpl.kt:1)
- remote API layer: [AuthRemoteDataSource.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/data/remote/AuthRemoteDataSource.kt:1)

## Flow Overview

Shared auth flow:

1. user enters a mobile number
2. app calls `auth/send-otp`
3. backend returns `refId`
4. user enters OTP
5. app calls `auth/verify-otp`
6. backend returns tokens and onboarding flags
7. if onboarding is needed, app shows `Basic Details`
8. if onboarding is not needed, app fetches `user/profile`
9. session is saved in shared session state
10. auth moves to `Handoff` and the app shell routes to the next destination

Screen states:

- `Login`
- `Otp`
- `BasicDetails`
- `Handoff`

Defined in [AuthContract.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/AuthContract.kt:1).

## UI Screens

### Login

File:

- [AuthLoginScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/components/AuthLoginScreen.kt:1)

Responsibilities:

- collect 10-digit phone number
- show auth logo / hero copy
- show validation feedback
- call `RequestOtp`
- render auth benefits and terms footer

Important behavior:

- UI displays `+91`
- API request uses raw 10-digit number
- `Get OTP` stays disabled until phone number is valid
- phone input keeps the first 10 digits and must not shift digits while typing
- invalid phone error should not appear while the user is still typing toward 10 digits

### OTP

File:

- [AuthOtpScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/components/AuthOtpScreen.kt:1)

Responsibilities:

- collect 6-digit OTP
- show phone number summary
- verify OTP automatically when all 6 digits are entered
- support resend countdown
- support back navigation to login

Important behavior:

- OTP is stored as shared state in `uiState.otpCode`
- UI uses six visual fields
- deleting from a field should move focus backward like the Android screen
- resend timing lives in the shared viewmodel, not in platform UI code

### Basic Details

File:

- [AuthBasicDetailsScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/components/AuthBasicDetailsScreen.kt:1)

Responsibilities:

- collect legal name
- collect pincode only when backend requires it
- collect optional referral code
- submit profile completion

Important behavior:

- email is currently not part of the shared onboarding request
- pincode field is conditional based on `pincodeRequired`
- successful submission updates session profile state and exits auth

### Handoff

File:

- [AuthHandoffScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/components/AuthHandoffScreen.kt:1)

Purpose:

- transient shared state while the app shell routes away from auth
- prevents auth from faking a return to `Login` or `Otp` after success

## State Management

Auth uses shared MVI:

- state: `AuthFlowUiState`
- intents: `AuthIntent`
- effects: `AuthEffect`

Files:

- [AuthContract.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/AuthContract.kt:1)
- [AuthFlowViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/presentation/AuthFlowViewModel.kt:1)

The viewmodel owns:

- phone input normalization
- OTP input normalization
- resend countdown
- request/verify/submit loading state
- error message state
- syncing auth UI with shared `SessionStore`

## Domain Models

Files:

- [AuthModels.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/domain/AuthModels.kt:1)
- [AuthRepository.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/domain/AuthRepository.kt:1)
- [AuthValidators.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/domain/AuthValidators.kt:1)

Key models:

- `AuthenticatedUser`
- `OtpRequestResult`
- `VerifyOtpResult`

Validation rules:

- phone: first 10 digits only, valid at length `10`
- OTP: first 6 digits only, valid at length `6`
- pincode: first 6 digits only, valid at length `6`
- legal name: trimmed length must be at least `2`
- referral code: uppercase, trimmed

## Use Cases

Files:

- [RequestOtpUseCase.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/domain/usecase/RequestOtpUseCase.kt:1)
- [VerifyOtpUseCase.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/domain/usecase/VerifyOtpUseCase.kt:1)
- [SubmitBasicDetailsUseCase.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/domain/usecase/SubmitBasicDetailsUseCase.kt:1)

These are currently thin wrappers over the repository. That is acceptable for now because the business orchestration already lives in the repository.

If auth business rules grow further, move more orchestration into dedicated use cases.

## API Contracts

DTOs:

- [AuthDtos.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/data/model/AuthDtos.kt:1)

### `POST auth/send-otp`

Request:

```json
{
  "mobileNumber": "9876543210"
}
```

Response:

```json
{
  "message": "OTP sent successfully",
  "refId": "..."
}
```

Important parity rule:

- Android UI shows `+91`
- Android API sends raw 10-digit mobile number
- KMP must preserve that exact behavior

### `POST auth/verify-otp`

Request:

```json
{
  "mobileNumber": "9876543210",
  "otp": "123456",
  "referralCode": null
}
```

Response:

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "user": {
    "id": "...",
    "mobileNumber": "9876543210"
  },
  "newUser": true,
  "showOnboarding": true,
  "pincodeRequired": true
}
```

Important flags:

- `newUser`
- `showOnboarding`
- `pincodeRequired`

These flags decide whether the app moves to `BasicDetails` or directly into the app shell.

### `GET user/profile`

Used after successful OTP verification for returning users when onboarding is not required.

Purpose:

- hydrate the best available user profile
- avoid routing a returning user through onboarding unnecessarily

### `PUT user/profile`

Used by basic-details submission.

Current request shape:

```json
{
  "name": "Legal Name",
  "email": null,
  "pinCode": "560001"
}
```

### `POST user/referral`

Used only after profile update when the user entered a referral code.

### `POST auth/refresh`

Used by the shared network layer for authenticated `401` recovery.

File:

- [AuthRefreshRemoteDataSource.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/data/remote/AuthRefreshRemoteDataSource.kt:1)

## Repository Behavior

File:

- [AuthRepositoryImpl.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/data/repository/AuthRepositoryImpl.kt:1)

Repository responsibilities:

- map auth API responses into domain models
- decide whether basic details are required
- fetch profile for returning users
- persist auth tokens and user session into `SessionStore`
- submit referral after profile completion

Important rules:

- if `verify-otp` says onboarding is required:
  - save tokens
  - save lightweight user snapshot
  - keep `isProfileComplete = false`
- if onboarding is not required:
  - fetch `user/profile` using the fresh access token
  - save resolved user
  - set `isProfileComplete = true`
- if profile fetch fails after verify:
  - fall back to the lightweight user from the verify response

## Network And Header Rules

Auth network must stay aligned with the Android app.

Current shared rules:

- `x-app-version` header is sent
- `x-app-platform` header is sent
- request timeout is `15s`
- connect timeout is `15s`
- socket timeout is `15s`
- no extra invented default client headers should be added unless Android also sends them
- public auth requests use `skipAuthentication()`
- authenticated `401` can refresh once, then retry once

Files:

- [HttpClientFactory.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/core/network/HttpClientFactory.kt:1)
- [AuthTokenRefreshHandler.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/auth/data/repository/AuthTokenRefreshHandler.kt:1)

## Session Behavior

Shared session is updated through:

- [SessionStore.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/core/session/SessionStore.kt:1)

Auth writes:

- `accessToken`
- `refreshToken`
- `AuthenticatedUser`
- `isProfileComplete`
- `isPinCodeRequired`

Auth reads:

- current user state during restore
- current phone number for profile completion updates

## Android Parity Notes

These rules are important because auth validation previously drifted from the Android app:

- `send-otp` request payload must be raw 10 digits, not `+91...`
- `verify-otp` also uses raw digits
- `+91` is a UI formatting detail, not an API payload rule
- OTP resend behavior should feel the same as Android
- back navigation patterns for `Otp` and `Basic Details` should follow Android screens
- iconography and strings should be copied from Android resources during parity cleanup
- input behavior matters too: typing, deletion, focus movement, keyboard interaction, and inline validation timing must be checked against Android
- feature-entry behavior matters too: login source, OTP source, onboarding source, and referral-prefill source must be audited before calling auth parity-complete

## Tests

Relevant tests:

- [AuthFlowViewModelTest.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonTest/kotlin/com/habit/gold/feature/auth/presentation/AuthFlowViewModelTest.kt:1)
- [AuthRepositoryImplTest.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonTest/kotlin/com/habit/gold/feature/auth/data/repository/AuthRepositoryImplTest.kt:1)
- [AuthValidatorsTest.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonTest/kotlin/com/habit/gold/feature/auth/domain/AuthValidatorsTest.kt:1)

Current test focus:

- validation rules
- request/verify/profile-completion state transitions
- repository behavior for onboarding vs returning user
- token/session persistence behavior

## Latest Verification

Last verified on:

- `2026-05-16`

Command:

```bash
./gradlew :composeApp:allTests :composeApp:compileKotlinIosSimulatorArm64 :composeApp:compilePreprodDebugKotlinAndroid
```

Result:

- `BUILD SUCCESSFUL`
- shared auth tests passed across the current Android variants and iOS simulator test targets exercised by `:composeApp:allTests`
- latest verification included the final auth parity cleanup for shared referral/security iconography and documentation updates
- latest verification also included the move of shared auth and app-shell copy into `composeResources/values/strings.xml`
- latest verification included platform-backed session persistence wiring so auth session restore no longer depends on in-memory DI bindings

Known non-blocking warnings during the last run:

- none relevant to the shared auth flow during the last verification pass

## Known Gaps / Follow-Up

The auth feature is functional, but these areas still need continued follow-up work:

- auth strings now live in shared `composeResources`, but future locale directories and broader direct-resource adoption should continue from this base
- auth visuals now match the Android iconography closely; any future shared drawable consolidation is optional cleanup, not a Phase 5 blocker
- iOS-specific interaction polish may still be needed for keyboard behavior
- platform SMS Retriever behavior is intentionally not shared in KMP
- Android pending-referral attribution and locked referral-prefill behavior are not yet ported into shared auth; that dependency is deferred to the later referral + platform integration phase and must be included there
- current session persistence is platform-backed on Android and iOS, but token-storage hardening can still be improved further later if we decide to move beyond the current private preference-backed approach

## When To Update This File

Update this file whenever auth changes in a way that affects:

- API contract
- screen flow
- onboarding conditions
- session persistence behavior
- Android parity decisions
- important validations
- test coverage expectations
