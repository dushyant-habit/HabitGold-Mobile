# HabitGold KMP Source Of Truth

This is the single starting document for the HabitGold KMP project.

If you are resuming work after a break, joining the project, reviewing progress, or deciding how to build the next feature, start here first.

This file is the practical source of truth for:

- what we are building
- how we are building it
- what rules we follow
- how code should be structured
- how features should be documented
- how releases should be prepared
- how branches, commits, and docs should be managed

Supporting files still exist for detailed tracking and history:

- [KMP_PROJECT_GUIDE.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROJECT_GUIDE.md:1)
- [KMP_MIGRATION_ROADMAP.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_MIGRATION_ROADMAP.md:1)
- [KMP_PROGRESS_TRACKER.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROGRESS_TRACKER.md:1)
- [KMP_PRE_MIGRATION_AUDIT.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PRE_MIGRATION_AUDIT.md:1)
- [GIT_WORKFLOW.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/GIT_WORKFLOW.md:1)

But if there is one file to open first, it should be this one.

## 1. Product Goal

Build a production-grade KMP app for Android and iOS that:

- closely matches the production Android app in behavior and UI intent
- fixes the architectural mistakes from the Android codebase
- keeps business logic and as much UI as practical in shared code
- stays strongly tested
- remains easy to maintain as features grow

The old Android app is the product source of truth.

This KMP repo is the engineering source of truth.

Copy from Android:

- product behavior
- business rules
- backend contract expectations
- real user flow edge cases

Do not copy from Android:

- giant files
- mixed responsibilities
- weak testing habits
- inconsistent error handling
- hardcoded strings and styling everywhere

## 2. Non-Negotiables

- shared code should contain real product logic
- Android and iOS entry layers should stay thin
- no giant cluttered screen files
- no mixing UI, networking, and business logic in one class
- no important hardcoded user-facing strings left in migrated features
- no feature is complete without tests
- common UI should be extracted early when patterns repeat
- branches, commits, and docs must stay disciplined
- architecture should stay boring and predictable

## 3. Architecture Standard

Use feature-first architecture inside `commonMain`.

Target shared structure:

```text
composeApp/src/commonMain/kotlin/com/habit/gold/
  app/
  core/
    config/
    localization/
    di/
    network/
    navigation/
    storage/
    session/
    presentation/
    designsystem/
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

Feature internal structure:

```text
feature/<name>/
  presentation/
    components/
    <Feature>Contract.kt
    <Feature>ViewModel.kt
    <Feature>Screen.kt
  domain/
    model/
    repository/
    usecase/
  data/
    remote/
    local/
    mapper/
    repository/
```

Layer rules:

- `presentation` renders and manages UI state
- `domain` defines business rules and contracts
- `data` performs IO and mapping
- each feature should also carry a feature-level overview doc named `feature-<feature-name>-overview.md`
- that feature overview doc should live inside the feature folder and capture UI flow, APIs, state rules, parity decisions, tests, and known gaps
- app-level startup and shell areas should use an area overview doc named `<area>-overview.md`, for example `app-startup-overview.md`

## 4. MVI Standard

Use MVI for features.

Every non-trivial feature should aim for:

- immutable `State`
- explicit `Intent` or `Action`
- optional one-time `Effect`
- reducer-style updates in the viewmodel/store

Preferred flow:

1. UI emits intent
2. viewmodel handles intent
3. use case or repository work happens
4. new immutable state is produced
5. UI re-renders from state

## 5. Design System Standard

Use design tokens and shared primitives, not scattered screen-level styling.

Design system should own:

- colors
- typography
- spacing
- shapes
- elevations
- gradients when needed
- buttons
- text fields
- cards
- app bars
- loading, empty, and error views

Do not build the app around `sdp/ssp`.

Prefer:

- `dp` and `sp`
- shared tokens
- adaptive spacing/layout decisions
- responsive layout behavior

## 6. Testing Standard

Testing is required, not optional cleanup work.

Expected layers:

- unit tests for validators, mappers, reducers, use cases
- repository tests for backend + storage behavior
- state tests for screen transitions
- UI tests for critical flows when practical

High-priority flows:

- auth
- startup/session restore
- network error handling
- buy
- sell
- SIP
- delivery

## 7. Network Standard

Use one shared network approach across the app.

The shared layer should own:

- `HttpClientFactory`
- `ApiResult`
- `NetworkError`
- backend error parsing
- auth/public request policy
- deterministic auth-expired behavior
- log redaction

Important rule:

- API parity means endpoint, payload, headers, timeout, auth retry, and environment behavior
- if Android uses required app headers, KMP must send the same headers before feature validation
- if Android does not send a default header, KMP should not invent one at the shared client layer
- internal request markers used only for KMP plumbing must never leak to backend requests or logs
- for backend migrations, Android network behavior is the working reference unless we explicitly agree to improve or change it
- authenticated `401` handling must either refresh once or expire the session deterministically
- timeout values must stay consistent with Android unless there is an explicit decision to change them

Current auth network parity rules:

- send `x-app-version` on every request
- send `x-app-platform` on every request
- use 15 second request, connect, and socket timeouts
- retry one authenticated request after a successful `auth/refresh`
- send the previous bearer token to `auth/refresh` when available
- clear session state when refresh is missing or fails
- inspect the actual Android request path before copying auth payload formatting; display formatting like `+91` can differ from the API payload
- parity review must include input behavior, not just network behavior: typing limits, deletion rules, focus movement, and error timing
- parity review must also include feature-entry behavior: deep-link/referral-prefill sources, onboarding sources, and stored flags that change the first render

## 8. Localization Standard

Localization is now an explicit build rule.

Rules:

- user-facing strings must move through a shared string-resource or string-provider strategy
- migrated screens and viewmodels must use the shared `AppStrings` boundary instead of hardcoding copy
- completed migrated screens should not leave important UI copy hardcoded
- avoid fragile string concatenation
- support placeholders and plural-friendly formatting
- date, number, and currency formatting must be locale-aware
- layouts must tolerate longer translated strings

Current implementation rule:

- when a feature introduces new user-facing copy, update the shared localization boundary in the same task
- before considering a feature migrated, inventory its strings, drawables, icons, and input/keyboard behavior against Android

Target direction:

- shared feature strings should live in `composeResources/values/strings.xml`
- shared feature icons and images should move into `composeResources/drawable`
- native app-only resources such as app icons, launch assets, and platform plist/manifest assets should remain platform-specific
- `AppStrings` is now the adapter boundary over those resource-backed strings while existing shared features continue migrating
- new features should add strings to `composeResources` first, then expose them through the shared boundary only where a viewmodel or cross-feature adapter still needs that indirection
- until direct resource usage fully replaces the adapter where appropriate, copied feature strings in `AppStrings` and shared Material icon usage must still be traced back to Android resources during parity review

## 8A. Feature Migration Checklist

Every migrated feature should be checked for all of the following, not only API success:

- request and response contract parity
- header and timeout parity
- exact string and resource inventory
- validation timing parity
- input behavior parity
- keyboard and focus behavior parity
- back navigation parity
- loading, error, empty, and success states
- feature-level documentation updated in `feature-<feature-name>-overview.md`
- app/startup documentation updated when launch, splash, or top-level routing behavior changes

Feature documentation template rule:

- auth’s `feature-auth-overview.md` is the current reference format
- new features should reuse the same structure instead of inventing a new documentation style each time

Later foundation items:

- final native-launch and shared-splash parity polish
- secure platform-backed session persistence
- full iOS scheme/config parity for `staging`, `preprod`, and `prod`
- screenshot-based parity QA baseline
- native integration boundary inventory for platform SDK features

## 9. Security And Production Standard

Required production thinking:

- secure token storage
- redacted logs
- strict network configuration
- environment separation
- clean release behavior
- avoid unnecessary libraries
- R8 / Proguard hardening as integrations grow

Environment rule:

- Android should use real product flavors for `staging`, `preprod`, and `prod`
- iOS should mirror those environments with schemes / build configurations, not ad hoc hardcoded values
- no feature API validation is complete until Android and iOS are confirmed against the intended environment
- when storing URLs in `.xcconfig`, do not write raw `https://...` directly because `//` is treated as a comment; use an xcconfig-safe pattern like `https:$(SLASH)/...`

## 10. Git And Workflow Standard

Branch format:

- `codex/feature/<short-kebab-description>`
- `codex/fix/<short-kebab-description>`
- `codex/refactor/<short-kebab-description>`
- `codex/test/<short-kebab-description>`
- `codex/docs/<short-kebab-description>`
- `codex/chore/<short-kebab-description>`

Commit format:

- `<type>(<scope>): <summary>`

Examples:

- `feat(auth): add startup-aware auth shell routing`
- `fix(network): clear session on authenticated 401`
- `refactor(ui): extract shared design-system primitives`
- `test(session): add restore coverage`

Commit rules:

- one logical task per commit whenever practical
- do not mix unrelated work in one commit
- include tests with behavior changes when possible
- update docs when architecture, workflow, standards, or migration scope changes

## 11. Documentation Levels

This project should maintain documentation at four levels.

### Project-Level Documentation

Purpose:

- define standards, architecture, migration path, workflow, and progress

Examples:

- this file
- project guide
- migration roadmap
- progress tracker
- git workflow

When to update:

- phase starts or completes
- architecture changes
- workflow changes
- standards change

### Feature-Level Documentation

Purpose:

- explain the behavior and migration status of a feature

Should capture when needed:

- feature goal
- entry points
- subflows
- backend dependencies
- shared vs platform-specific parts
- known risks or redesign notes
- testing priorities

Use this especially for heavy flows like:

- auth
- home
- trade
- SIP
- delivery
- profile/security

### Code-Level Documentation

Purpose:

- make important code understandable without over-commenting

Use:

- good naming first
- short KDoc for important classes and methods
- short inline comments only where logic is not obvious

#### Important Method Documentation Rule

If a method/function is important, add a short KDoc above it.

Important means it has one or more of these:

- business-critical behavior
- non-obvious side effects
- session, auth, payment, or navigation impact
- error handling or retry behavior
- tricky mapping or validation logic
- platform boundary behavior

Recommended KDoc should briefly state:

- what it does
- important inputs
- important side effects
- special failure or edge-case behavior

Example:

```kotlin
/**
 * Restores persisted session state and updates the in-memory auth model.
 *
 * Clears back to a logged-out session when stored tokens or metadata are missing.
 */
suspend fun restore(): AuthSession
```

Do not add noisy comments to obvious getters, setters, or trivial UI helpers.

### Release-Level Documentation

Purpose:

- make builds and releases predictable

Should capture:

- environments
- app ids / bundle ids
- flavor and scheme behavior
- release checklist
- security checks
- dependency audit notes
- analytics/crash/reporting decisions
- rollout/rollback notes if needed

## 12. Current Completed Foundations

Already in place:

- Phase 0 scaffold
- Phase 1 session and storage boundaries
- Phase 2 network foundation
- Phase 3 design system foundation
- Phase 4 app shell and startup routing

## 13. Current Next Step

Next major focus:

- Phase 5 auth cleanup and migration review

That means:

- review current auth structure against MVI and architecture rules
- extract remaining hardcoded auth/app-shell strings
- tighten feature boundaries
- expand auth tests

## 14. How To Use This File

When starting work:

1. read this file first
2. check the current phase in [KMP_PROGRESS_TRACKER.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROGRESS_TRACKER.md:1)
3. check detailed task order in [KMP_MIGRATION_ROADMAP.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_MIGRATION_ROADMAP.md:1)
4. if working on a risky feature, review [KMP_PRE_MIGRATION_AUDIT.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PRE_MIGRATION_AUDIT.md:1)
5. follow the branch and commit rules from [GIT_WORKFLOW.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/GIT_WORKFLOW.md:1)

If this file and another file ever drift, update them so this file remains the easiest place to regain context quickly.
