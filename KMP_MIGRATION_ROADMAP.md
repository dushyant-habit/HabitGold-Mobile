# HabitGold KMP Migration Roadmap

This document turns the migration plan into an execution checklist for rebuilding the production Android app in Kotlin Multiplatform for Android and iOS.

Use this together with [KMP_PROJECT_GUIDE.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROJECT_GUIDE.md:1).
Start-first master reference: [HABITGOLD_SOURCE_OF_TRUTH.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/HABITGOLD_SOURCE_OF_TRUTH.md:1)

Git workflow reference: [GIT_WORKFLOW.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/GIT_WORKFLOW.md:1)

## Goal

Build a production-grade KMP app that:

- matches the behavior and UI intent of the Android app
- improves architecture, file structure, and reuse
- uses MVI consistently
- has stronger test coverage
- keeps platform-specific code thin and isolated

## Source Of Truth

Android product source project:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android`

KMP target project:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile`

## Execution Rules

- Port behavior, not cluttered structure.
- Shared code should own product logic.
- Android and iOS code should mostly bind platform services.
- No feature is done without tests.
- Rebuild giant Android screens into smaller KMP sections and components.

## Target Shared Structure

Primary shared destinations in `composeApp/src/commonMain/kotlin/com/habit/gold/`:

```text
app/
core/
  config/
  di/
  designsystem/
  navigation/
  network/
  presentation/
  session/
  storage/
  util/
feature/
  auth/
  home/
  trade/
  sip/
  profile/
  history/
  rewards/
  referral/
  delivery/
  alerts/
  security/
```

Each feature should follow:

```text
feature/example/
  presentation/
    components/
    ExampleContract.kt
    ExampleViewModel.kt
    ExampleScreen.kt
  domain/
    model/
    repository/
    usecase/
  data/
    model/
    remote/
    local/
    mapper/
    repository/
```

## Phase Checklist

## Explicit Feature Flows

These are tracked explicitly so major product flows do not get lost inside broad phase names.

- [x] Auth Flow
- [x] Home / Portfolio Flow
- [ ] Buy Gold Flow
- [ ] Sell Gold Flow
- [ ] Transactions List Flow
- [ ] Transaction Details / Status Flow
- [ ] SIP / Savings Flow
- [ ] Profile Flow
- [ ] Personal Info Flow
- [ ] Nominee Flow
- [ ] KYC Flow
- [ ] Security / App Lock Flow
- [ ] UPI Autopay Management Flow
- [ ] UPI ID Management Flow
- [ ] Rewards Flow
- [ ] Referral Flow
- [ ] Alerts Flow
- [ ] Delivery / Get Coin Flow

### Phase 0: Project Scaffold

- [ ] Create shared package structure in `composeApp/src/commonMain/kotlin/com/habit/gold/`
- [ ] Create base MVI template: `State`, `Intent`, `Effect`
- [ ] Create initial shared DI layout by concern
- [ ] Create basic naming and file-splitting conventions in code
- [ ] Add a simple feature template folder for future use
- [ ] Align Android application ID, iOS app bundle ID, and shared framework bundle ID naming strategy

### Phase 1: Core Config, Session, Storage

- [x] Create `core/config/AppConfig`
- [x] Create environment model and base URL strategy
- [ ] Define target app identifiers for Android and iOS per environment
- [x] Create `core/session` models for auth/session/app startup
- [x] Create storage contracts for tokens, profile basics, flags, alerts
- [x] Create secure storage abstraction for sensitive values
- [x] Define logout cleanup and session restore rules
- [x] Replace in-memory session bindings with platform-backed persistent storage on Android and iOS
- [x] Add unit tests for config and session rules

### Phase 2: Network Foundation

- [x] Create shared `HttpClientFactory`
- [x] Create shared `ApiResult`
- [x] Create shared `NetworkError`
- [x] Create shared error parser and mapper
- [x] Create stable app-level error categories and user-safe error messages
- [x] Create auth header injection strategy
- [x] Port Android auth app headers: `x-app-version` and `x-app-platform`
- [x] Port Android-style single refresh retry for authenticated `401` responses
- [x] Create deterministic auth-expired and logout policy
- [x] Create public-request vs authenticated-request policy
- [x] Enable full debug API logging for non-production environments
- [x] Create idempotency strategy for financial and order-creating requests
- [x] Create log-redaction rules for tokens, PII, OTPs, and payment payloads
- [x] Create test utilities for fake/stubbed HTTP behavior
- [x] Add tests for timeouts, connectivity, auth header injection, backend error parsing, malformed payloads, and auth-expired behavior
- [x] Add tests for refresh retry behavior and refreshed-session token persistence

### Phase 3: Design System

- [x] Create shared colors
- [x] Create shared typography
- [x] Create shared spacing tokens
- [x] Create shared shapes and elevations
- [x] Create shared buttons, fields, cards, app bars
- [x] Create shared loading, empty, and error views
- [x] Define compact/medium/expanded layout groundwork
- [x] Add previews for core components
- [x] Add shared string-resource/localization strategy and extract current shell/auth strings

### Phase 4: App Shell And Navigation

- [x] Create shared app shell state
- [x] Create startup coordinator
- [x] Create auth gate and logged-in gate rules
- [x] Create typed navigation model for KMP app shell
- [x] Create bottom navigation shell
- [x] Keep Android and iOS entry points thin
- [x] Add tests for startup routing rules
- [ ] Finalize native Android and iOS launch layers with the shared splash experience

### Phase 5: Auth

- [x] Create `feature/auth` structure
- [x] Port auth DTOs needed for request/verify/profile flow
- [x] Create auth repository contract and implementation
- [x] Create auth use cases
- [x] Create auth MVI state/contracts
- [x] Extract auth user-facing copy into the shared localization approach
- [x] Rebuild login screen using shared design system
- [x] Rebuild OTP screen using shared design system
- [x] Rebuild basic-details onboarding screen
- [x] Add tests for auth reducers/viewmodels/repositories
- [x] Close Android-parity cleanup for auth interaction behavior and core iconography
- [ ] Port pending-referral attribution / locked referral-prefill as part of the later referral + platform integration phase

### Phase 6: Home And Portfolio

- [x] Complete strict Android parity audit for shell, toolbar, section order, and linked flows
- [x] Create `feature/home` structure
- [x] Port dashboard repository logic
- [x] Port recent transactions summary logic
- [x] Port force-update check logic
- [x] Create home MVI state
- [x] Rebuild Home top bar, balance card, and zero-balance start-journey states
- [x] Rebuild Home trust highlights and first recent-activity slice
- [x] Rebuild Home savings cards, active savings pager, Why HabitGold cards, and secured footer card sections
- [x] Rebuild Home gold-price chart sheet and Why HabitGold intro pager
- [x] Restore Home pull-to-refresh behavior and Android-style shell bottom-bar motion
- [x] Rebuild Home recent-activity drilldown into transaction details
- [x] Rebuild Home gold value details and support/help entry points
- [x] Replace temporary Home pending-action handoffs with typed downstream routes
- [x] Rebuild Home UI in Android-matching small sections instead of one giant file
- [x] Run post-parity cleanup pass to split oversized Home files and move route dependencies out of inline composable lookups
- [ ] Connect coupon handoff and SIP verification dialog to the true shared Trade / SIP feature routes when those phases land
- [ ] Connect typed Home downstream routes to the final shared Buy, Sell, Profile, Alerts, and Savings destinations as their phases land
- [x] Add tests for dashboard mapping and home state transitions

### Phase 7: Trade

- [ ] Create `feature/trade` structure
- [ ] Port buy-gold repository logic
- [ ] Port sell-gold repository logic
- [ ] Create shared transaction polling component/engine
- [ ] Create buy flow MVI state
- [ ] Create sell flow MVI state
- [ ] Build Buy Gold Flow explicitly
- [ ] Port Buy one-time and grams/rupees conversion behavior
- [ ] Port Buy coupons, promo validation, and auto-fetch behavior
- [ ] Port Buy payment launch and post-payment verification states
- [ ] Build Sell Gold Flow explicitly
- [ ] Port Withdrawal Mode entry flow into Buy vs Sell vs Delivery
- [ ] Port Sell payout UPI selection and short-poll pending behavior
- [ ] Port Sell locked-gold messaging and invoice access
- [ ] Build Transaction Details / Status Flow pieces shared with trade
- [ ] Rebuild buy UI
- [ ] Rebuild sell UI and sell outcome states
- [ ] Add tests for trade polling and status transitions

### Phase 8: SIP / Savings

- [ ] Create `feature/sip` structure
- [ ] Port savings plan setup/update APIs
- [ ] Port savings polling/status logic
- [ ] Create SIP MVI state
- [ ] Port Daily, Weekly, and Monthly savings setup variants
- [ ] Port SIP upgrade flow from Home existing-plan cards
- [ ] Port SIP resume flow triggered from Home
- [ ] Port UPI Autopay manage flow: list, filter, pause, resume, cancel
- [ ] Rebuild savings setup screens
- [ ] Add tests for SIP setup/status transitions

### Phase 9: Profile And Security

- [ ] Create `feature/profile` structure
- [ ] Port profile fetch/update/logout
- [ ] Port KYC and nominee flows
- [ ] Create `feature/security` shared contracts
- [ ] Add platform biometric bindings behind interfaces
- [ ] Build Profile Flow
- [ ] Build Personal Info Flow
- [ ] Build Nominee Flow
- [ ] Build KYC Flow
- [ ] Build Security / App Lock Flow
- [ ] Build UPI Autopay Management Flow
- [ ] Build UPI ID Management Flow
- [ ] Rebuild profile and security screens
- [ ] Add tests for profile state transitions and logout rules

### Phase 10: History, Rewards, Referral, Alerts

- [ ] Create `feature/history`
- [ ] Create `feature/rewards`
- [ ] Create `feature/referral`
- [ ] Create `feature/alerts`
- [ ] Build Transactions List Flow
- [ ] Port transaction history/details
- [ ] Port rewards milestones/history/redeem
- [ ] Port refer-and-earn flows
- [ ] Port Rewards history and Rewards redeem as separate subflows
- [ ] Port Refer & Earn detail, booster extension prompts, and buy/SIP deep links
- [ ] Port Referral status/history flows or intentionally redesign them
- [ ] Port alerts state and persistence behavior
- [ ] Add tests for each feature's core state and mapping

### Phase 11: Delivery / Get Coin

- [ ] Create `feature/delivery`
- [ ] Port catalog/cart/address book flow
- [ ] Port serviceability checks
- [ ] Port quote/order summary/order placement
- [ ] Port delivery tracking and invoice flow
- [ ] Port shortfall-to-buy-gold bridge from delivery catalog
- [ ] Port add/edit/delete address with OTP verification and serviceability refresh
- [ ] Port pending checkout restore, quote expiry refresh, and payment retry handling
- [ ] Rebuild delivery screens in smaller components
- [ ] Add tests for address, delivery, and order state transitions

## Android Audit Notes

These notes came from a deeper pass through the Android app and are here to prevent hidden flow loss during migration.

- `Home` is not only dashboard rendering. It also owns recent-transaction drilldown, gold value details entry, support/help entry, editorial entry, Home-triggered SIP create/upgrade/resume, and a SIP verification dialog.
- `Buy` is not only one-time purchase. It also includes a SIP tab, coupon auto-fetch/manual apply, embedded or external Juspay launch, payment-status polling, and invoice access after success.
- `Sell` depends on `WithdrawalMode` and on saved verified VPAs. It has a two-step create-then-execute flow, a very short polling window, a pending fallback that relies on History, and locked-gold messaging tied to sell availability.
- `Delivery / Get Coin` includes catalog, shortfall-to-buy bridge, shared address book, add/edit/delete address, address OTP verification, pincode serviceability, quote creation, payment launch, payment verification polling, order placed, and delivery tracking.
- `SIP` exists in three places: Home cards, Buy tab, and dedicated savings setup screens. It also has a separate autopay-management flow for pause/resume/cancel.
- `Refer & Earn` is broader than one screen: rewards home, refer detail, rewards history, rewards redeem, and referral status/history.
- `ReferralStatusScreen` and `ReferralHistoryScreen` currently look non-production or disconnected. `ReferralHistory` has a route but no obvious live navigation entry, and `ReferralStatusScreen` is driven by hardcoded dummy data. Treat both as redesign candidates unless product confirms they still matter.
- `TransactionDetails` is shared by trade and delivery because invoice rules differ between trade orders and delivery orders.

### Phase 12: Platform Integrations

- [ ] Add Android environment split strategy: `staging`, `preprod`, `prod`
- [ ] Decide iOS environment strategy: schemes/configurations or build settings equivalent
- [ ] Align environment-specific app IDs / bundle IDs and display names
- [ ] Android secure storage implementation
- [ ] iOS secure storage implementation
- [ ] Android push notifications integration
- [ ] iOS push notifications integration
- [ ] Android deep-link and referral capture integration
- [ ] iOS deep-link and referral capture integration
- [ ] Android payment SDK integration
- [ ] iOS payment integration strategy

### Phase 13: Hardening And QA

- [ ] Review dependencies before adding feature SDKs; avoid carrying over Android-only libraries without a concrete need
- [ ] Add and maintain minimal dependency set in shared code
- [ ] Add Android release hardening rules and expand Proguard/R8 rules only as integrations require them
- [ ] Verify cleartext remains disabled and backup/security manifest rules stay intact
- [ ] Add CI quality gates
- [ ] Add broader unit and repository tests
- [ ] Add viewmodel/reducer tests for critical features
- [ ] Add UI tests for critical flows
- [ ] Add analytics and crash-reporting hooks
- [ ] Perform Android parity QA screen by screen
- [ ] Verify both Android and iOS UX for each completed feature

## Android Source Mapping

Use the Android app as a reference source, not a copy source.

### Behavior Reference Only

These files should guide behavior and UX, but should not be ported structurally as-is:

- `app/src/main/java/com/habit/gold/MainActivity.kt`
- `app/src/main/java/com/habit/gold/navigation/NavGraph.kt`
- `app/src/main/java/com/habit/gold/screens/HomeScreen.kt`
- `app/src/main/java/com/habit/gold/screens/LoginScreen.kt`
- `app/src/main/java/com/habit/gold/screens/OtpScreen.kt`

### Good Shared Logic Candidates

These are good source references for shared KMP logic:

- `app/src/main/java/com/habit/gold/data/repository/AuthRepositoryImpl.kt`
- `app/src/main/java/com/habit/gold/data/repository/TradeRepositoryImpl.kt`
- `app/src/main/java/com/habit/gold/data/repository/SipRepositoryImpl.kt`
- `app/src/main/java/com/habit/gold/data/repository/RewardsRepositoryImpl.kt`
- `app/src/main/java/com/habit/gold/data/repository/DeliveryRepositoryImpl.kt`
- `app/src/main/java/com/habit/gold/data/repository/AddressRepositoryImpl.kt`
- `app/src/main/java/com/habit/gold/domain/usecase/*`

### Platform-Only Source References

These should become interfaces in shared code with platform implementations:

- `app/src/main/java/com/habit/gold/data/notifications/DeviceTokenSync.kt`
- `app/src/main/java/com/habit/gold/notifications/HabitGoldMessagingService.kt`
- `app/src/main/java/com/habit/gold/referral/ReferralAttributionManager.kt`
- `app/src/main/java/com/habit/gold/payments/juspay/*`

## First Milestone

The first milestone should end with:

- network layer complete
- session/storage contracts complete
- design system baseline complete
- app shell working
- auth fully migrated with tests

This is the first point where the KMP app becomes a real foundation rather than scaffolding.

## Recommended Build Order

Build in this order:

1. Phase 0
2. Phase 1
3. Phase 2
4. Phase 3
5. Phase 4
6. Phase 5
7. Phase 6
8. Phase 7
9. Phase 8
10. Phase 9
11. Phase 10
12. Phase 11
13. Phase 12
14. Phase 13

## Immediate Next Tasks

These should be the next concrete tasks in the repo:

- [ ] Create shared KMP package structure
- [ ] Create base MVI template
- [ ] Create core config/session/storage contracts
- [ ] Align Android/iOS/shared bundle identifiers and environment naming
- [ ] Create shared network foundation
- [ ] Add network-layer tests
- [ ] Port auth onto the new network/session foundation

## Completion Rule

A phase is only complete when:

- implementation is done
- tests are in place
- file structure remains maintainable
- Android parity is checked for affected flows
- both Android and iOS impact are understood
