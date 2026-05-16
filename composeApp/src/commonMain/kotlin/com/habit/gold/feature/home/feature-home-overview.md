# Feature Home Overview

## Status
- Phase: `Phase 6`
- Current status: Home is complete as a shared Phase 6 feature slice with Android-style shell, animated bottom bar behavior, pull-to-refresh, balance, savings, chart, Why HabitGold pager, footer cards, Home-owned child routes, and typed downstream handoff routes
- Source audit: [feature-home-audit.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/home/feature-home-audit.md:1)
- Last verified: `2026-05-16`
- Verification command: `./gradlew :composeApp:allTests :composeApp:compileKotlinIosSimulatorArm64 :composeApp:compilePreprodDebugKotlinAndroid`
- Verification result: `BUILD SUCCESSFUL`

## Scope Of Current Slice
- restore Android-parity shell tabs: `Home`, `Rewards`, `History`
- add Home read-model/data-state foundation
- rebuild the main Android-matching Home sections on top of that foundation

## Completed In This Slice
- bottom-nav product flow now matches Android tab names: `Home`, `Rewards`, `History`
- shell placeholder copy no longer points to `Transactions` and `Profile` as bottom tabs
- Home DTOs, remote data source, repository, use case, and initial MVI contract are in place
- Home route is now wired into the shared main shell instead of a generic placeholder card
- Home top bar now mirrors the Android toolbar structure with:
  - initials avatar
  - greeting and compact name
  - live gold price pill
  - alerts chip
- Home now renders the first Android-matching content states:
  - loading card
  - error card with retry
  - zero-balance start-journey card
  - invested-balance card with visibility and expand toggles
  - trust highlights carousel
  - gold savings habit cards
  - active savings pager section
  - recent activity section
  - Why HabitGold horizontal intro cards using shared copied Android assets
  - support + trust footer section with secured-gold badges and SafeGold branding
- Home card color treatment now uses the Android HabitGold purple family rather than the earlier off-brand approximation
- Home lower resource slice now includes copied shared Android assets:
  - `img_habitgold_intro`
  - `img_bis_safety`
  - `img_liquid_accessible`
  - `img_proven_growth`
  - `safegold_image`
- Home foundation APIs covered:
  - `GET portfolio`
  - `GET trade/transactions?page=1&limit=5`
  - `GET user/features`
  - `GET gold/price/history?days=...`
- Home-owned behavior now restored:
  - live gold price chart bottom sheet
  - Why HabitGold intro pager bottom sheet
  - trust highlight cards opening the intro pager
  - Android-style pull-to-refresh gesture while keeping loaded content visible during refresh
  - Android-style bottom-bar enter/exit motion timing at the shell level
  - recent activity detail route from each Home card
  - gold value details route
  - help center route from the Home footer
  - typed downstream handoff routes for Buy, Sell, Profile, Alerts, and Savings
  - persisted balance-visibility preference through shared app preferences
- Home post-parity cleanup is complete for the Phase 6 scope:
  - oversized Home presentation was split into smaller concern-based files
  - child screens were split out of the old route-heavy file
  - Home route dependencies are now assembled in the shell instead of being looked up inline inside the composable
  - Home strings, docs, and verification now reflect the final Phase 6 state
- Home foundation tests are added for:
  - repository mapping
  - summary loading rules
  - viewmodel load state

## Folder Structure
```text
feature/home/
  data/
    model/
    remote/
    repository/
  di/
  domain/
    model/
    usecase/
  presentation/
    components/
      HomeBottomSheets.kt
      HomeGoldPriceSheet.kt
      HomeIntroPagerSheet.kt
      HomeSavingsAndFooterSections.kt
      HomeSectionTokens.kt
      HomeStateCards.kt
      HomeTopBar.kt
      HomeTrustAndActivitySections.kt
    HomeChildScreens.kt
    HomeDestination.kt
    HomeHelpCenterScreen.kt
    HomeTransactionAndHandoffScreens.kt
  feature-home-audit.md
  feature-home-overview.md
```

## API Coverage In Foundation Slice
- `GET portfolio`
- `GET trade/transactions?page=1&limit=5`
- `GET user/features`
- `GET sip/mandates`
- `GET gold/price/history?days=...`

## Behavior Rules In Foundation Slice
- dashboard is the hard dependency
- recent transactions are a soft dependency and fall back to empty
- force-update is a soft dependency and falls back to `null`
- Home visual parity work must use the audit file as the primary implementation reference

## Remaining Follow-ups Outside Phase 6
- connect Buy and Sell handoff routes to the true shared Trade feature when Phase 7 lands
- connect Savings handoff routes to the true shared SIP feature when Phase 8 lands
- connect Profile and Alerts handoff routes to their shared feature destinations when those phases land
- add any future editorial or coupon-specific Home entry surfaces only if the Android app keeps exposing them

## Known Notes
- this slice now restores the Home-owned child screens directly inside the Home feature, and uses typed handoff routes for destinations owned by later phases
- Home now also soft-loads `GET sip/mandates` so the savings cards and active mandate pager can render off shared state
- the Daily savings row now has a shared animated border treatment to stay closer to Android’s highlighted Daily card
- Home chart history caches the 1Y series briefly and slices shorter ranges locally, then appends the current live price as the trailing chart point
- Home refresh state now distinguishes first-load shimmer from swipe-refresh so the screen does not blank out during a manual refresh
- Home route dependency ownership is now explicit through a dedicated `HomeRouteDependencies` object passed from the app shell
