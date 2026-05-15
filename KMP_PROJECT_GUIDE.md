# HabitGold KMP Project Guide

This document is the reference standard for building HabitGold as a high-quality Kotlin Multiplatform app for Android and iOS.

Start-first master reference: [HABITGOLD_SOURCE_OF_TRUTH.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/HABITGOLD_SOURCE_OF_TRUTH.md:1)

The goal is not only to replicate the production Android app, but to rebuild it in a cleaner, more scalable, more testable way while preserving feature parity and visual quality.

## Primary Goal

Build a production-grade KMP app that:

- matches the existing Android app behavior and UI closely
- shares business logic and as much UI as practical across Android and iOS
- avoids the structural mistakes from the original Android codebase
- remains easy to maintain as features grow
- is strongly tested
- follows clear architectural standards

## Non-Negotiables

- Shared code should contain product logic, not just helper utilities.
- Android and iOS layers should stay as thin as possible.
- No large cluttered screen files.
- No mixing UI, business rules, and networking in the same class.
- No hardcoded design values scattered across features.
- No hardcoded user-facing strings left inside feature UI once a screen is considered migrated.
- No feature should be considered complete without tests.
- Reuse common components early instead of duplicating similar UI.
- Prefer boring, predictable architecture over clever abstractions.
- Branches, commits, and project docs should stay disciplined and descriptive.

## Product Source Of Truth

The existing production Android app is the product source of truth for:

- feature behavior
- user journeys
- UI content and states
- backend integration contracts
- edge cases discovered in production

This KMP project is the engineering source of truth for:

- improved architecture
- shared code strategy
- stronger testing
- reusable design system
- maintainable screen/component structure

Do not copy old code blindly.

Copy:

- behavior
- business requirements
- visual intent
- backend contract expectations

Do not copy:

- cluttered files
- tightly coupled logic
- duplicated UI
- weak testing practices
- ad hoc sizing patterns

## Architectural Direction

Use a feature-first shared architecture inside `commonMain`.

Recommended structure:

```text
composeApp/src/commonMain/kotlin/com/habit/gold/
  core/
    config/
    localization/
    di/
    network/
    storage/
    presentation/
    designsystem/
    navigation/
    util/
  feature/
    auth/
      presentation/
        components/
        AuthContract.kt
        AuthReducer.kt
        AuthViewModel.kt
        AuthScreen.kt
      domain/
        model/
        repository/
        usecase/
      data/
        remote/
        local/
        mapper/
        repository/
    home/
    profile/
    portfolio/
    buy/
    sell/
    kyc/
```

Every migrated feature should include a feature-level overview file named:

- `feature-<feature-name>-overview.md`

That file should live inside the feature folder and document:

- screens and user flow
- APIs and payload rules
- state management shape
- platform or Android-parity decisions
- tests
- known gaps

Template reuse rule:

- reuse the auth feature overview format for future features
- do not create a new documentation structure for each feature unless there is a real need
- feature docs should stay consistent enough that someone can jump between features without relearning the doc format
- for cross-cutting app areas like startup, use a stable area overview doc such as `app-startup-overview.md`

## Git Workflow Standard

Use a simple, consistent git workflow.

Branch naming:

- `codex/feature/<short-kebab-description>`
- `codex/fix/<short-kebab-description>`
- `codex/refactor/<short-kebab-description>`
- `codex/test/<short-kebab-description>`
- `codex/docs/<short-kebab-description>`
- `codex/chore/<short-kebab-description>`

Commit naming:

- use conventional-commit style
- format: `<type>(<scope>): <summary>`

Examples:

- `feat(auth): add startup-aware auth shell routing`
- `fix(network): clear session on authenticated 401`
- `refactor(ui): extract shared design-system primitives`
- `test(session): add restore coverage`
- `docs(workflow): add git and documentation standards`

Rules:

- one task or bug should map to one logical commit whenever practical
- do not hide unrelated work inside a giant commit
- include tests with behavior changes when possible
- update documentation when standards, architecture, workflow, or migration scope changes
- if the working tree already contains mixed changes, do not fake clean history retroactively without choosing a safe split strategy first

## Architecture Principles

### 1. Thin Platform Layers

Android and iOS code should mostly do:

- app bootstrap
- platform-specific dependency binding
- secure storage bridging
- permissions
- lifecycle integration
- platform SDK integrations

Business logic should not live in platform entry points.

### 2. Feature-First Packaging

Organize by feature, not by technical layer across the whole app.

Good:

- `feature/auth/...`
- `feature/portfolio/...`

Avoid:

- one giant `screens/`
- one giant `viewmodels/`
- one giant `components/`

### 3. Clear Layer Separation

Each feature should separate:

- `presentation`: UI, state, intents, effects, reducers, viewmodels
- `domain`: business models, use cases, repository contracts
- `data`: DTOs, mappers, API calls, persistence, repository implementations

### 4. Shared Core

Keep these in shared code whenever feasible:

- API clients
- repositories
- use cases
- state management
- UI models
- design system
- screen composables
- validation logic

Keep these platform-specific when necessary:

- secure token storage
- biometric auth
- push notifications
- app links / deep links integration details
- native SDK wrappers

### 5. KMP Boundary Rules

Keep platform boundaries simple and explicit.

Rules:

- prefer interfaces plus DI for most abstractions
- use `expect/actual` only for true platform APIs or platform-only behavior
- do not create `expect/actual` wrappers for ordinary business logic
- shared code should not know Android classes or iOS framework details
- if a dependency differs by platform, bind it at the platform or DI layer
- feature parity review must include interaction behavior such as typing limits, deletion behavior, keyboard movement, focus behavior, and validation timing
- feature audit should not be a one-pass read; it must include separate structure, visual, and interaction passes before implementation
- audit checklists must explicitly cover:
  - default collapsed or expanded states
  - loading treatment such as shimmer
  - bottom-bar or inset overlap
  - alignment and badge sizing
  - sheet, pager, and close-dismiss behavior

## MVI Standard

Yes, use MVI for this project.

MVI gives:

- explicit screen state
- predictable updates
- easier tests
- less UI-side logic leakage
- better scaling as features become more complex

Recommended screen contract:

```kotlin
data class ExampleState(...)

sealed interface ExampleIntent

sealed interface ExampleEffect

class ExampleViewModel(...)
```

Preferred flow:

1. UI emits `Intent`
2. ViewModel handles intent
3. Domain/use case work happens
4. State is reduced into a new immutable state
5. UI re-renders from state
6. One-time events are emitted as effects

### MVI Rules

- `State` must be immutable.
- UI should render from state, not derive hidden logic ad hoc.
- User actions must go through intents/actions.
- Navigation/snackbar/toast style one-off actions should use effects, not state flags that linger forever.
- Keep reducer-style state updates clean and traceable.

## SOLID Guidance

Use SOLID where it improves maintainability, not as ceremony.

### Single Responsibility

- composables render UI
- viewmodels manage screen state
- use cases hold business actions
- repositories coordinate data sources
- data sources perform IO

### Open/Closed

Build extensible components and contracts, but do not abstract too early.

### Liskov

Keep interface implementations behaviorally consistent.

### Interface Segregation

Avoid giant service interfaces that do everything.

### Dependency Inversion

Presentation depends on domain contracts, not concrete remote classes.

## DI Standard

Use DI modules split by concern.

Recommended modules:

- `coreModule`
- `networkModule`
- `storageModule`
- `authModule`
- `homeModule`
- `portfolioModule`

Rules:

- DI wires dependencies only.
- Business logic should not live in DI setup.
- Prefer interface-based injection at important boundaries.
- Test code must be able to replace dependencies easily.
- Platform-specific bindings should be isolated cleanly.

## Concurrency Standard

Keep concurrency boring and predictable.

Rules:

- inject dispatchers instead of hardcoding them deep in business logic
- keep cancellation friendly flows and suspend functions
- do not launch uncontrolled background work from composables
- long-running work should have explicit ownership
- retries should be limited and intentional
- state updates should happen from one clear path inside the viewmodel/store

Prefer:

- `viewModelScope` for screen-bound work
- repository and use case APIs that are easy to test
- explicit loading and cancellation states for important flows

## File Size And Component Rules

One major mistake to avoid is oversized files.

### File Hygiene Rules

- Screen files should focus on screen assembly only.
- Extract repeated UI into `components/`.
- Extract large logic into separate viewmodels, reducers, or use cases.
- Avoid files that mix models, business logic, and composables together.
- If a composable section is meaningful and reusable, move it out.

### Practical Rule Of Thumb

If a file becomes hard to scan in one sitting, split it.

Typical split:

- `FeatureScreen.kt`
- `FeatureContent.kt`
- `FeatureContract.kt`
- `FeatureViewModel.kt`
- `components/FeatureHeader.kt`
- `components/FeatureForm.kt`
- `components/FeatureCard.kt`

## UI And Design System Standard

Do not rely on raw per-screen styling everywhere.

Build a shared design system in `commonMain`.

Recommended areas:

- colors
- typography
- spacing
- shapes
- elevations
- icons usage rules
- buttons
- text fields
- app bars
- cards
- chips
- dialogs
- loading states
- empty states
- error states

### Design Tokens First

Use tokens like:

- spacing: `4, 8, 12, 16, 20, 24, 32`
- radii: `8, 12, 16, 24`
- type scale: label, body, title, headline

Avoid ad hoc values unless necessary.

### About `sdp/ssp`

Do not make `sdp/ssp` the base sizing system.

Prefer:

- `dp` for layout
- `sp` for text
- design tokens for consistency
- adaptive rules for compact/medium/expanded layouts
- minimum accessible touch sizes
- max width constraints where needed

Reason:

- Compose already handles density through `dp` and `sp`
- adaptive layouts usually scale better than uniformly scaling every value
- this translates more naturally to iOS as well

### Responsive UI Rules

For smaller devices:

- reduce decorative spacing before reducing usability
- preserve tap target comfort
- prefer vertical stacking over cramped horizontal layouts
- allow scrolling instead of compressing content excessively

For larger devices:

- cap content widths where appropriate
- add breathing room intentionally
- use multi-column layouts only when useful

### Accessibility Rules

- support font scaling
- keep contrast strong
- ensure touch targets are comfortable
- add semantics for key actions
- avoid using color alone to indicate meaning

### Localization Rules

Keep localization readiness simple from the start.

Rules:

- all user-facing copy should move through a shared string-resource or string-provider strategy
- migrated shared screens and viewmodels should use the shared `AppStrings` boundary instead of adding new hardcoded copy
- no completed feature screen should keep important UI copy hardcoded in composables or viewmodels
- do not build important user-facing strings through fragile concatenation
- support placeholders and plural-friendly formatting patterns instead of manual string assembly
- keep strings easy to locate and review
- plan for date, currency, and number formatting to be locale aware
- avoid hardcoding text that will likely need translation later
- keep layouts resilient to longer strings
- when migrating a feature, inventory strings, icons, and drawable assets before calling the UI parity pass complete
- parity review should also cover feature-entry states such as deep-link/referral-prefill sources, onboarding sources, and stored flags that alter the first render

Target direction:

- shared strings should live in `composeResources/values/strings.xml`
- shared icons and images should live in `composeResources/drawable`
- platform-only resources should stay native
- keep using `AppStrings` only as the adapter boundary where a viewmodel or cross-feature abstraction still needs resolved strings
- for new migrated features, add strings to `composeResources` first and avoid introducing new hardcoded string holders
- until direct resource usage fully replaces the adapter where appropriate, copied feature strings in `AppStrings` and shared Material icon usage should still be traced back to Android resources during parity review
- review empty, error, loading, CTA, and legal text too, not just main labels
- when migrating a feature, inventory strings, icons, and drawable assets before calling the UI parity pass complete

### Performance Rules

Do not over-optimize early, but do protect the basics.

Rules:

- prefer simple composables and clear state flow
- avoid unnecessary recomposition triggers
- keep large lists lazy
- be careful with expensive effects, shadows, and animations on low-end devices
- prefer stable image-loading and pagination patterns when those features arrive
- verify key screens on at least one smaller/older target device profile

## Navigation Standard

Do not keep the whole app in one enum-based screen switch forever.

Use a proper navigation approach as features grow.

Rules:

- feature flows should own their local navigation state cleanly
- app-level navigation should be centralized
- auth state, onboarding state, and logged-in state should be explicit

## Data And Networking Standard

Recommended separation:

- remote DTOs
- local entities
- domain models
- mappers between them

Rules:

- UI must not depend on DTOs
- repositories should map raw backend contracts into app-safe domain models
- centralize error handling
- normalize backend inconsistencies before they reach UI
- define timeout, retry, and auth token strategies clearly

### Network And API Error Handling Rules

This needs to be much better than the Android app.

Requirements:

- every network call should return through one consistent result model
- do not throw raw backend or Ktor exceptions into presentation code
- classify failures into stable app-level types:
  - connectivity
  - timeout
  - unauthorized / session expired
  - forbidden
  - not found
  - validation / business-rule failure
  - rate limit
  - server error
  - unknown / unexpected
- parse backend error bodies in one place only
- preserve useful backend business messages when safe to show
- fall back to safe default user messages when backend payloads are malformed
- never let raw JSON blobs or transport exception text reach the UI
- token refresh and logout-on-auth-failure rules must be deterministic
- retry only where the operation is safe and idempotent
- requests that create financial side effects must use explicit idempotency strategy
- network logging must redact tokens, PII, payment payloads, and OTP-related data
- feature parity checks must include request headers and timeout values, not only endpoint paths and bodies
- if Android sends app headers like `x-app-version` and `x-app-platform`, KMP must send them too before API validation is considered complete
- if Android does not send a default header, KMP should not add that header in the shared client by default
- internal KMP-only markers for auth/public request policy must never leak to backend requests or debug logs
- for auth flows, verify the exact Android request payload from the call site and repository path; UI display formats and API payload formats may differ

Testing expectations:

- verify error-body parsing for array, object, primitive, and malformed payloads
- verify timeout and no-network mapping
- verify auth-expired behavior
- verify refresh success and refresh failure paths
- verify repository fallback behavior for partial or inconsistent payloads
- verify user-visible error copy for critical flows like auth, buy, sell, SIP, and delivery

### Session And Storage

Session state should not remain in-memory only for production.

Plan for:

- secure token storage
- session restore on app start
- local user cache if needed
- clear logout cleanup
- explicit session expiry handling
- deterministic storage cleanup rules
- migration paths if storage models change

### Local Persistence

When features need caching or offline behavior, prefer a shared persistence approach.

Candidates:

- SQLDelight
- DataStore-style abstractions for preferences
- secure platform-backed storage for secrets

### Release And Environment Safety

Keep release management practical and easy to reason about.

Rules:

- maintain clear `development`, `staging`, and `production` behavior
- Android should use real product flavors and iOS should mirror them with schemes / build configurations
- `.xcconfig` files treat `//` as comments, so URL values must use an xcconfig-safe form such as `https:$(SLASH)/domain/path`
- secrets must not be committed to source control
- feature flags should be used only when they reduce release risk
- risky backend-dependent features should have a rollback path
- debug logging must be gated by environment

### Feature Migration Checklist

Before marking a feature complete, check:

- API endpoint and payload parity
- header and timeout parity
- string parity
- icon and drawable parity
- validation timing parity
- input behavior parity
- keyboard and focus behavior parity
- back navigation parity
- feature-level overview documentation updated

Later foundation items:

- secure platform-backed session persistence
- full iOS scheme/config parity for `staging`, `preprod`, and `prod`
- screenshot-based parity QA baseline
- native integration boundary inventory for platform SDK features

### Observability Rules

Build only the basics first, but do not skip them entirely.

Baseline:

- analytics for critical user journeys
- crash reporting
- structured logs for important failures
- no sensitive data in logs, analytics, or crashes
- stable event names once analytics are introduced

### Backend Contract Safety

Protect the app from backend inconsistency.

Rules:

- mapper tests should cover null, missing, and partially filled fields
- repository tests should verify safe fallback behavior
- do not let raw backend response shape leak into UI
- backend contract changes should fail loudly in tests before they fail in production

## Testing Standard

Testing is mandatory, not optional cleanup work.

### Test Pyramid

Include:

- unit tests
- reducer/viewmodel tests
- repository tests
- mapper tests
- integration tests where useful
- UI tests for critical flows

### What Must Be Tested

For every feature, cover:

- happy path
- validation errors
- loading states
- API failure states
- retry behavior
- partial/missing backend data
- navigation or effect emission where relevant

### High-Value Test Targets

- validators
- use cases
- reducers
- viewmodels
- repository mapping logic
- auth/session restoration
- buy/sell/payment state transitions

### MVI Test Rule

Given state + intent -> expected state/effect.

This should become a standard testing pattern.

### Definition Of Done For Features

A feature is not done unless:

- implementation is complete
- loading/error/empty/success states are handled
- shared components are extracted where appropriate
- tests are added
- strings and design values are not hardcoded randomly
- code review would consider the file structure maintainable

## CI And Quality Gates

Keep CI small but strict.

Minimum required checks:

- shared test suite passes
- Android build passes
- iOS shared build passes
- lint or static analysis passes once added

Nice to add later:

- coverage reporting
- screenshot or snapshot verification for critical UI
- dependency update checks

## Common Mistakes To Avoid

- giant files with too many responsibilities
- copying Android-specific patterns directly into KMP without adaptation
- building screens before a design system exists
- putting DTOs straight into UI state
- weak error handling
- no session restoration strategy
- not testing reducers/viewmodels/use cases
- hardcoded dimensions and colors inside feature screens
- duplicated UI pieces that should be shared
- writing platform-specific logic inside common business flows

## Recommended Porting Strategy

Do not port the old Android app screen by screen without structure.

Recommended order:

1. define design tokens and core reusable components
2. define MVI template and feature folder structure
3. define DI modules and storage strategy
4. audit the Android app feature by feature
5. port one feature at a time into KMP
6. add tests during porting, not after
7. verify both Android and iOS behavior for each completed feature

### Android Replica QA Checklist

For every ported screen or flow, verify:

- layout hierarchy matches the Android product intent
- spacing, type, and component behavior are visually close
- loading, error, empty, and success states all exist
- interactions match Android behavior
- backend payloads and validations behave the same
- Android and iOS both produce acceptable UX, not just visual similarity

## Code Review Checklist

Before merging, ask:

- Is this logic in the right layer?
- Is state explicit and testable?
- Is the file too large or doing too much?
- Are common components being reused properly?
- Are design values coming from tokens/theme?
- Are success, loading, and error states all handled?
- Are test cases sufficient?
- Is anything Android-specific leaking into shared code?
- Would this still feel clean after 10 more screens are added?

## Project Conventions

### Naming

- use descriptive feature names
- keep contracts explicit
- prefer `State`, `Intent`, `Effect`, `ViewModel`, `UseCase`, `Repository`

### Strings

- avoid burying important user-facing strings inside deep UI logic
- centralize or at least keep them easy to find and review

### Comments

- use comments sparingly
- explain intent where logic is non-obvious
- do not narrate trivial code

### Team Process

Keep process light.

Rules:

- use this guide as the default engineering standard
- create a short ADR only for decisions that affect multiple features or platform boundaries
- keep pull requests reviewable in size when possible
- add or update tests in the same change where behavior changes
- upgrade dependencies intentionally, not randomly

## Ideal Feature Template

```text
feature/example/
  presentation/
    components/
      ExampleHeader.kt
      ExampleForm.kt
      ExampleCard.kt
    ExampleContract.kt
    ExampleViewModel.kt
    ExampleScreen.kt
  domain/
    model/
      ExampleModel.kt
    repository/
      ExampleRepository.kt
    usecase/
      SubmitExampleUseCase.kt
  data/
    remote/
      ExampleApi.kt
      ExampleDtos.kt
    local/
      ExampleLocalDataSource.kt
    mapper/
      ExampleMapper.kt
    repository/
      ExampleRepositoryImpl.kt
```

## Quality Bar

This app should feel:

- visually polished
- architecturally calm
- easy to extend
- safe to refactor
- well tested
- consistent across Android and iOS

If a solution feels quick but messy, it is probably not acceptable unless it is explicitly temporary and tracked.

## Working Rule For This Repo

When implementing any new feature or screen:

1. check this guide first
2. decide the right feature boundary
3. create or reuse design-system components
4. implement using MVI
5. keep files small and focused
6. add tests before calling it done
7. verify Android and iOS impact

## Living Document

This guide should evolve as the codebase grows.

Update it when:

- a better project convention is adopted
- a new shared architectural pattern is introduced
- a repeated mistake appears in reviews
- a new testing/design/navigation standard is agreed upon
