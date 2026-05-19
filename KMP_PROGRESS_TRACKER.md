# HabitGold KMP Progress Tracker

This file tracks implementation progress phase by phase while rebuilding HabitGold in Kotlin Multiplatform.

Use this together with:

- [HABITGOLD_SOURCE_OF_TRUTH.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/HABITGOLD_SOURCE_OF_TRUTH.md:1)
- [KMP_PROJECT_GUIDE.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROJECT_GUIDE.md:1)
- [KMP_MIGRATION_ROADMAP.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_MIGRATION_ROADMAP.md:1)
- [GIT_WORKFLOW.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/GIT_WORKFLOW.md:1)

## Current Status

- Current phase: `Phase 12`
- Phase 9 status: `checkpointed, not fully closed`
- Phase 10 status: `checkpointed through shared History, Alerts, and Rewards; referral decision work still pending`
- Phase 11 status: `shared Delivery / Get Coin checkpoint is live; final device QA and any remaining invoice/detail parity follow-up are still pending`
- Phase 12 status: `implementation checkpoint is live; remaining work is mostly iOS runtime verification plus attribution decision`
- Current focus: `finish the remaining iOS runtime/device verification gaps after env/storage/otp/push/referral/app-branding setup`
- Next milestone: `complete iOS Firebase / Crashlytics / Performance and APNs / associated-domain verification on a properly provisioned device build`

## Phase Status

### Planning

- [x] Target KMP structure defined
- [x] Migration roadmap documented
- [x] Project guide documented
- [x] App identifier alignment started

### Implementation

- [x] Phase 0: Project Scaffold
- [x] Phase 1: Core Config, Session, Storage
- [x] Phase 2: Network Foundation
- [x] Phase 3: Design System
- [x] Phase 4: App Shell And Navigation
- [x] Phase 5: Auth
- [x] Phase 6: Home And Portfolio
- [ ] Phase 7: Trade
- [ ] Phase 8: SIP / Savings
- [ ] Phase 9: Profile And Security
- [ ] Phase 10: History, Rewards, Referral, Alerts
- [ ] Phase 11: Delivery / Get Coin
- [~] Phase 12: Platform Integrations
- [ ] Phase 13: Hardening And QA

## Phase 6: Home

- [x] Create fresh Android source audit for Home shell, sections, and linked flows
- [x] Create `feature/home` package structure
- [x] Port portfolio dashboard domain/data layer
- [x] Build Home MVI contract and state model
- [x] Rebuild Home top bar, zero-balance state, and invested-balance card
- [x] Rebuild Home trust highlights and recent activity first slice
- [x] Restore Home savings cards, active savings pager, Why HabitGold cards, and footer card sections
- [x] Restore Home gold-price chart sheet and Why HabitGold intro pager
- [x] Restore Home pull-to-refresh behavior and Android-style shell bottom-bar motion
- [x] Complete remaining Home UI sections with Android parity
- [x] Add Home tests and feature overview doc
- [x] Rebuild Home savings cards and active savings pager
- [x] Rebuild remaining Home linked navigation and cross-feature handoffs
- [x] Run post-parity cleanup pass to split oversized Home files and remove inline route-level dependency lookups

## Phase 7: Trade

- [x] Complete strict Android parity audit for Trade flows, APIs, platform boundaries, and design
- [x] Create `feature/trade` structure
- [x] Add shared Trade repository contract, DTOs, remote data source, and repository implementation
- [x] Add shared polling policy and polling use case
- [x] Add shared payment-launch contract for native Juspay bridging later
- [x] Add initial Buy and Sell MVI contracts
- [x] Add Trade foundation tests and verify build success
- [x] Add first functional shared `WithdrawalMode`
- [x] Connect Home buy/sell entry points to real Trade-owned routes
- [x] Add first functional shared `Buy`
- [x] Add first functional shared `Sell`
- [x] Add first functional trade-owned transaction details and invoice viewer
- [x] Add Android Juspay launcher binding behind the shared Trade payment contract
- [x] Add iOS Juspay launcher binding behind the shared Trade payment contract
- [x] Finish strict Android visual parity for `WithdrawalMode`
- [x] Finish strict Android visual parity for `Buy`
- [x] Finish strict Android visual parity for `Sell`
- [ ] Finish strict Android visual parity for trade-owned transaction details and invoice viewer in the later Trade review pass

## Phase 8: SIP / Savings

- [x] Complete strict Android audit for savings setup, upgrade, mandate management, and API ownership
- [x] Create `feature/savings` structure
- [x] Port shared savings DTOs, remote data source, repository, and use-case foundation
- [x] Replace the Home savings placeholder with a real shared mandate-management route
- [x] Add shared mandate list, status filter, and pause / resume / cancel actions
- [x] Add first shared Savings tests for repository and viewmodel coverage
- [x] Rebuild daily / weekly / monthly setup and upgrade screens
- [x] Port savings execution-day selectors and mandate-session payment handoff
- [x] Port savings polling, pending, success, and failure states
- [ ] Finish exact compounding bottom-sheet parity
- [ ] Finish final setup spacing / density micro-parity
- [ ] Finish savings-specific coupon and promo UX refinement
- [ ] Finish manage-autopay strict visual parity
- [ ] Rebuild any deferred savings detail / execution-history surfaces if product needs them
- [ ] Run final end-to-end Savings QA

## Phase 9: Profile And Security

- [x] Complete strict Android audit for Profile, KYC, Nominee, Security, Logout, UPI Autopay, and UPI ID management
- [x] Resolve ownership conflict: `Manage Autopay` stays Savings-owned and Profile-linked
- [x] Resolve ownership boundary: `Manage UPI IDs` stays trade/payments-backed and Profile-routed
- [x] Create `feature/profile` structure
- [~] Port shared profile fetch/update/logout foundation
- profile fetch/update/logout/delete-account foundation is live
- KYC verify and nominee save request paths are live
- biometric/security parity is intentionally deferred
- [x] Rebuild Profile landing hub
- [x] Rebuild Personal Info flow
- [x] Rebuild KYC flow
- [x] Rebuild Nominee flow
- [ ] Rebuild Security / App Lock setup and verification flows
- [x] Link Profile to Savings-owned `Manage Autopay`
- [~] Rebuild trade-backed `Manage UPI IDs` under Profile route ownership
- Profile route ownership is live; final strict parity is still pending
- [~] Add tests for profile state transitions, validations, and logout/delete-account rules
- repository/logout/delete-account and profile input/date tests are live
- deeper child-flow/state coverage is still pending
- [ ] Run mandatory Phase 9 hardening and code-quality closure pass before marking the phase done
- this later pass must cover parity review, deferred biometric/security work, unused-code sweep, duplication review, file-size/ownership review, and final end-to-end QA
- [ ] Evaluate scoped edge-swipe back as a later navigation improvement
- if added later, apply it only to child/pushed screens such as Help, Contact Us, detail screens, and nested flows
- do not apply swipe-back to root tab screens by default, and verify gesture conflicts on screens with horizontal interactions

## Phase 11: Delivery / Get Coin

- [x] Complete strict Android audit for delivery / get coin ownership, checkout rules, address flows, and tracking
- [x] Create `feature/delivery`
- [x] Port catalog/cart/address book flow
- [x] Port serviceability checks
- [x] Port quote/order summary/order placement
- [x] Port delivery tracking
- [x] Port shortfall-to-buy-gold bridge from delivery catalog
- [x] Port add/edit/delete address with OTP verification and serviceability refresh
- [x] Port pending checkout restore, quote expiry refresh, and payment retry handling
- [x] Rebuild delivery screens in smaller components
- [x] Add targeted tests for delivery DTO mapping and catalog state transitions
- [ ] Finish any remaining delivery-specific invoice/detail parity follow-up if product still needs a dedicated shared invoice surface
- [ ] Run final end-to-end Delivery QA

## Feature Flow Coverage

This section keeps the important product flows visible explicitly, even when they are grouped under broader phases.

### Core Product Flows

- [x] Auth Flow
- [x] Home / Portfolio Flow
- [x] Buy Gold Flow
- [x] Sell Gold Flow
- [x] Transactions List Flow
- [x] Transaction Details / Status Flow
- [ ] SIP / Savings Flow
- [ ] Profile / KYC / Logout Flow
- [x] Rewards Flow
- [ ] Referral Flow
- [x] Alerts Flow
- [x] Delivery / Get Coin Flow
- [~] Platform Integrations Flow

### Hidden Subflows To Preserve

- [x] Home savings create / upgrade / resume handoff
- [x] Withdrawal Mode gateway
- [x] Trade polling-driven payment launch and status transitions
- [x] Rewards Redeem Flow
- [x] Rewards History Flow
- [ ] Referral Status / History decision
- [x] Delivery address OTP + serviceability flow
- [x] Delivery pending-checkout restore flow
- [x] VPA selection / payout flow

### Flow To Phase Mapping

- `Auth Flow` -> `Phase 5: Auth`
- `Home / Portfolio Flow` -> `Phase 6: Home And Portfolio`
- `Buy Gold Flow` -> `Phase 7: Trade`
- `Sell Gold Flow` -> `Phase 7: Trade`
- `Transactions List Flow` -> `Phase 10: History, Rewards, Referral, Alerts`
- `Transaction Details / Status Flow` -> `Phase 7: Trade` and `Phase 10: History, Rewards, Referral, Alerts`
- `SIP / Savings Flow` -> `Phase 8: SIP / Savings`
- `Profile / KYC / Logout Flow` -> `Phase 9: Profile And Security`
- `Rewards Flow` -> `Phase 10: History, Rewards, Referral, Alerts`
- `Referral Flow` -> `Phase 10: History, Rewards, Referral, Alerts`
- `Alerts Flow` -> `Phase 10: History, Rewards, Referral, Alerts`
- `Delivery / Get Coin Flow` -> `Phase 11: Delivery / Get Coin`
- `Platform Integrations Flow` -> `Phase 12: Platform Integrations`

## Phase 12: Platform Integrations

- [x] Complete strict Phase 12 audit
- [x] Align shared Android and iOS base app version values to the current Android production version line
- [x] Add Android encrypted secure storage implementation
- [x] Add Android SMS Retriever bridge behind the shared auth OTP screen
- [x] Document iOS OTP behavior as system keyboard suggestion autofill instead of Android-style SMS auto-read
- [x] Add shared platform bridge storage for pending referral and device-token state
- [x] Add shared device-token sync manager and shared alert recorder
- [x] Add Android referral deep-link capture
- [x] Add Android Install Referrer capture
- [x] Add Android FCM messaging-service callback path
- [x] Add Android Clarity init and shared route screen-name updates
- [x] Add iOS Clarity init and build-config/project-id wiring
- [x] Add iOS APNs registration/token capture hooks
- [x] Add iOS referral URL capture hooks
- [x] Add iOS shared-alert persistence hook from notification callbacks
- [x] Finish true iOS preprod Xcode configuration wiring
- [x] Add Android Firebase plugin/runtime parity and env `google-services.json` assets
- [x] Add iOS Firebase pod/runtime wiring plus env plist selection
- [x] Add iOS Firebase Messaging delegate / FCM registration-token wiring
- [x] Finish iOS keychain-backed secure storage
- [x] Align Android and iOS environment-specific display names
- [x] Align Android and iOS app icon assets to current HabitGold branding
- [x] Move iOS Firebase bootstrap earlier into SwiftUI app startup so runtime init happens before downstream Firebase usage
- [ ] Verify iOS Firebase / Crashlytics / Performance on device
- [~] Finalize iOS push capability / associated-domain project setup and verify on device
- personal-team local builds still require empty dev entitlements, so true APNs delivery verification remains tied to paid-team provisioning
- [ ] Finalize iOS attribution equivalent or explicitly document the non-equivalent path
- [~] Keep iOS Clarity wiring in place, but do not block Phase 12 closure on Clarity dashboard validation

## Completed So Far

- [x] Added project architecture guide
- [x] Added migration roadmap
- [x] Added progress tracker
- [x] Added explicit git workflow documentation and helper scripts for branch/commit naming
- [x] Added free-by-default PR automation with template and PR creation helper script
- [x] Aligned Android app id and iOS app bundle id to `com.habit.gold`
- [x] Documented pre-migration audit for Home, Buy, Sell, Delivery, SIP, and Refer & Earn
- [x] Added shared app composition root for future app-shell growth
- [x] Added base MVI contracts and reusable `MviViewModel`
- [x] Split Koin bootstrap into `core`, `network`, and `auth` modules
- [x] Added KMP feature template reference
- [x] Verified scaffold changes with `./gradlew :composeApp:allTests`
- [x] Added session auth/startup models and startup destination rules
- [x] Added storage contracts for tokens, user profile, session metadata, and app preferences
- [x] Added generic secure-storage abstraction with in-memory implementation for now
- [x] Added session restore and logout cleanup behavior to `SessionStore`
- [x] Replaced in-memory runtime session bindings with platform-backed persistent storage on Android and iOS
- [x] Added session rule tests and re-verified with `./gradlew :composeApp:allTests`
- [x] Added shared `ApiResult` / `NetworkError` with stable app-level error categories
- [x] Added shared HTTP client hardening with auth-header injection, public-request skip-auth policy, and log redaction
- [x] Added deterministic auth-expired session-clearing behavior for authenticated `401` responses
- [x] Added Android-parity auth network headers: `x-app-version` and `x-app-platform`
- [x] Added Android-parity auth refresh flow with one retry after successful `auth/refresh`
- [x] Persist refreshed auth tokens back into shared session state
- [x] Added network test coverage for timeout, connectivity, auth header policy, malformed payloads, and auth-expired handling
- [x] Added network test coverage for refresh retry behavior
- [x] Enabled full Ktor API logging for debug-style environments
- [x] Verified Phase 2 with `./gradlew :composeApp:allTests`
- [x] Added shared startup coordinator and typed top-level route model
- [x] Added shared splash, authentication gate, and logged-in shell routing in `AppRoot`
- [x] Added placeholder bottom-navigation shell for Home, Transactions, and Profile
- [ ] Finalize branded shared splash UI plus native Android and iOS launch-layer parity
- [x] Added startup-area overview documentation in `app-startup-overview.md`
- [x] Added startup routing tests and re-verified with `./gradlew :composeApp:allTests`
- [x] Added shared design-system tokens for colors, typography, spacing, shapes, elevations, and gradients
- [x] Added shared primitives for buttons, text fields, cards, app bars, and state views
- [x] Added design-system preview coverage and adopted the primitives in the current auth and app-shell UI
- [x] Added shared `AppStrings` localization boundary and extracted current auth and app-shell copy
- [x] Moved current shared auth and app-shell copy into resource-backed `composeResources/values/strings.xml`
- [x] Split auth UI into smaller screen/component files and migrated auth onto the shared MVI base
- [x] Added auth handoff-state coverage so auth routing stays aligned with the app shell
- [x] Verified Phase 3 with `./gradlew :composeApp:allTests`
- [x] Extract current auth and app-shell hardcoded user-facing strings into the shared localization approach
- [x] Ported Android auth onboarding contract including `showOnboarding`, `pincodeRequired`, and referral submission
- [x] Rebuilt shared Login, OTP, and Basic Details screens to mirror Android auth behavior closely
- [x] Added shared auth use cases and repository tests for onboarding, profile fallback, and referral submission
- [x] Added the shared HabitGold auth logo resource for Android and iOS
- [x] Added Android `staging`, `preprod`, and `prod` product flavors to the KMP app module
- [x] Aligned iOS runtime environment config with Xcode debug/release settings
- [x] Confirmed Android auth connectivity permission parity with `INTERNET` and `ACCESS_NETWORK_STATE`
- [x] Removed the final auth emoji placeholders and aligned shared referral/security visuals closer to Android
- [x] Verified the auth migration with `./gradlew :composeApp:allTests`
- [x] Restarted Phase 6 on a fresh audit branch
- [x] Added detailed Home audit for shell, sections, borders, animations, APIs, and strings
- [x] Corrected shared bottom-nav product flow to `Home`, `Rewards`, `History`
- [x] Added Home foundation data layer, state model, feature overview doc, and tests
- [x] Verified the first Home foundation slice with `./gradlew :composeApp:allTests :composeApp:compileKotlinIosSimulatorArm64`
- [x] Replaced the placeholder Home tab with a real shared Home route and Android-matching first UI slice
- [x] Added shared Home strings for toolbar, balance, trust, and recent activity sections
- [x] Copied shared Home intro/footer assets and restored Why HabitGold plus secured-gold footer cards
- [x] Aligned Home card purple treatment and buy/sell-related Home iconography closer to Android
- [x] Restored Home-owned gold-price chart sheet backed by shared price-history API
- [x] Restored Why HabitGold intro pager and wired trust-highlight cards to it
- [x] Added explicit Home pending-action messaging for still-deferred cross-feature routes
- [x] Verified the new Home UI slice with `./gradlew :composeApp:allTests :composeApp:compileKotlinIosSimulatorArm64`

## Current Phase Breakdown

### Phase 0: Project Scaffold

Status: `Completed for routing and shell structure`

Definition of done:

- base shared package structure exists
- MVI template exists
- initial DI layout exists
- feature folders are ready for implementation

Immediate tasks:

- [x] create `app/`, `core/`, and `feature/` shared folder structure
- [x] create base MVI contract template
- [x] create initial DI structure by concern
- [x] create feature template reference
- [ ] expand remaining empty destination packages only when real code lands

### Phase 1: Core Config, Session, Storage

Status: `Completed`

Definition of done:

- app config is explicit
- session model is defined
- storage contracts are defined
- secure storage boundary exists

Immediate tasks:

- [x] define session/auth/app-startup models
- [x] define token/profile/flags storage interfaces
- [x] define secure storage abstraction
- [x] define session restore and logout cleanup behavior
- [x] add session-focused common tests
- [x] align environment-specific identifiers when flavors/schemes are introduced

### Phase 2: Network Foundation

Status: `Completed`

Definition of done:

- shared HTTP client exists
- auth/public request policy exists
- API error model exists
- auth refresh flow matches Android behavior for headers, timeout, and single-retry token refresh
- app-level error categories are defined and tested
- auth-expired handling is deterministic
- network tests exist

Immediate tasks:

- [x] create `ApiResult` / `NetworkError`
- [x] create `HttpClientFactory`
- [x] create auth token provider contract
- [x] create public-request skip-auth mechanism
- [x] define stable error categories and user-safe copy policy
- [x] define idempotency and auth-expired handling rules
- [x] create network test plan and first tests
- [x] port Android auth app headers into shared KMP networking
- [x] port Android-style single refresh retry for authenticated `401` responses

### Phase 3: Design System

Status: `Completed`

Definition of done:

- shared colors exist
- shared typography exists
- shared spacing tokens exist
- shared shapes and elevations exist
- shared buttons, fields, cards, and app bars exist
- shared loading, empty, and error views exist
- adaptive layout groundwork exists through centralized spacing and shape primitives
- previews exist for core components

Immediate tasks:

- [x] create shared colors
- [x] create shared typography
- [x] create shared spacing tokens
- [x] create shared shapes and elevations
- [x] create shared buttons, fields, cards, app bars
- [x] create shared loading, empty, and error views
- [x] define compact/medium/expanded layout groundwork
- [x] add previews for core components
- [x] add shared string-resource/localization strategy and migrate current shell/auth copy

### Phase 4: App Shell And Navigation

Status: `Completed`

Definition of done:

- shared app shell state exists
- startup coordinator exists
- auth gate and logged-in gate rules exist
- typed navigation model exists
- bottom navigation shell exists
- Android and iOS entry points stay thin
- startup routing tests exist

Immediate tasks:

- [x] create shared app shell state
- [x] create startup coordinator
- [x] create auth gate and logged-in gate rules
- [x] create typed navigation model for KMP app shell
- [x] create bottom navigation shell
- [x] keep Android and iOS entry points thin
- [x] add tests for startup routing rules
- [ ] finalize native Android and iOS launch layers with the shared splash experience

### Phase 5: Auth

Status: `Completed`

Definition of done:

- shared auth flow matches Android login, OTP, and basic-details behavior closely
- auth uses shared MVI and use-case boundaries
- auth API integration handles onboarding flags and error cases deterministically
- shared auth strings are localized through `AppStrings`
- auth viewmodel and repository tests exist

Immediate tasks:

- [x] create `feature/auth` structure
- [x] port auth DTOs needed for request/verify/profile flow
- [x] create auth repository contract and implementation
- [x] create auth use cases
- [x] create auth MVI state/contracts
- [x] extract auth user-facing copy into the shared localization approach
- [x] rebuild login screen using shared design system
- [x] rebuild OTP screen using shared design system
- [x] rebuild basic-details onboarding screen
- [x] add tests for auth reducers/viewmodels/repositories

### Phase 7: Trade

Status: `Checkpointed, not fully closed`

Status: `In progress with end-to-end Buy and Sell flows live, Android+iOS Juspay binding in place, and remaining detail-route parity still underway`

Definition of done:

- Buy Gold Flow works end to end
- Sell Gold Flow works end to end
- shared transaction polling/status logic is implemented
- trade state handling is tested

Immediate tasks:

- [x] create strict Android source audit for Buy, Sell, Withdrawal Mode, VPA, transaction details, invoice viewer, and Juspay boundaries
- [x] port buy-gold repository logic
- [x] port sell-gold repository logic
- [x] create shared transaction polling engine
- [x] define shared payment-launch contract and typed payment result mapping before wiring Juspay
- [x] build Buy Gold Flow
- [x] build Sell Gold Flow
- [x] add trade flow tests

### Phase 10: History, Rewards, Referral, Alerts

Status: `Checkpointed through shared History, Alerts, and Rewards`

Mandatory implementation rule:

- keep code-quality review active during Phase 10 implementation, not only during cleanup
- do not create a Phase 10 commit until a pre-commit code-quality pass has been done
- color and gradient parity is mandatory for Phase 10 and later UI-heavy phases:
  - extract exact Android color tokens before implementation
  - use exact gradients instead of approximate replacements
  - preserve gradient stop order and direction from Android
- the mandatory Phase 10 pre-commit pass must cover:
  - file-size / responsibility review
  - duplication and shared-helper review
  - unused-code / stale-route sweep
  - keyboard/focus behavior review on interactive screens
  - localization review
  - color and gradient parity review against Android
  - docs alignment with the verified implementation

Definition of done:

- Transactions List Flow works
- Transaction Details / Status Flow works
- rewards and alerts flows are implemented
- referral status/history decision is explicitly documented
- history-related state handling is tested

Immediate tasks:

- [x] build Transactions List Flow
- [x] build Transaction Details / Status Flow
- [x] locate strict Android Rewards source of truth
- [x] complete strict Rewards/Referral audit from Android project files
- [x] port rewards flows
- [~] port referral flows
- [x] port alerts flow
- [~] add history/rewards/referral/alerts tests

Audit decisions locked:

- split Phase 10 into four shared feature boundaries:
  - `feature/history`
  - `feature/rewards`
  - `feature/referral`
  - `feature/alerts`
- treat `RewardsRedeem` as a rewards-routed Trade checkout variant
- keep alerts persistence shared/local while leaving push receive bindings platform-specific
- create a shared referral-attribution contract, but keep Install Referrer and deep-link capture platform-specific
- do not faithfully port `ReferralStatusScreen` and `ReferralHistoryScreen` until product confirms they should exist in shared form

Remaining order:

1. referral decision work
2. final Rewards micro-parity + maintainability cleanup before closure

## Start Here

Recommended implementation start:

1. Phase 0: create only the scaffold needed for network/session work
2. Phase 1: create session and storage contracts
3. Phase 2: build the shared network foundation with tests
4. Phase 5: use auth as the first real consumer of the network layer

This is the best starting path because it gives us:

- consistent backend handling
- reusable auth/session behavior
- a testable foundation
- less rework when porting real features

## Current Working Order

The next execution order should be:

1. review the current code against the architecture and UI rules
2. migrate auth flow onto the shared MVI + network foundation more fully
3. add auth flow state and repository tests
4. begin Home / Portfolio migration on the stabilized foundation
5. refine the bottom-shell presentation as real feature content lands

## Decisions Log

### Confirmed Decisions

- [x] Use MVI for KMP features
- [x] Keep app-level Android package and iOS bundle id consistent as `com.habit.gold`
- [x] Avoid porting giant Android screen files structurally
- [x] Keep KMP dependency set lean until features require more
- [x] Prefer design tokens and adaptive layouts over making `sdp/ssp` the core sizing system
- [x] Keep token refresh as an explicit network boundary until backend refresh API details are confirmed

### Open Decisions

- [ ] final iOS environment split strategy
- [ ] shared secure storage implementation choice
- [ ] exact backend refresh-token contract and retry policy
- [ ] whether to keep shared framework bundle id as `com.habit.gold.shared`
- [ ] whether `ReferralStatus` / `ReferralHistory` should be preserved as-is or redesigned

## Audit Findings

- `Home`, `Buy`, and `Delivery` are broader than their top-level route names suggest.
- `Sell` depends on `WithdrawalMode`, saved VPAs, and History for pending outcomes.
- `SIP` spans Home, Buy, dedicated savings setup, and autopay management.
- `Savings` setup / upgrade and mandate-management should be migrated as separate slices even though they belong to the same phase.
- `RewardsRedeem` is a trade checkout variant, not a standalone payment system.
- `ReferralStatusScreen` and `ReferralHistoryScreen` need product confirmation before faithful migration.
- Android app network error handling was inconsistent, so KMP must use a stricter shared error model from the start.

## Documentation Rule

Update this file when:

- a phase starts
- a phase completes
- a major decision changes
- a new blocker appears
- the next milestone changes

Keep updates short and factual.
