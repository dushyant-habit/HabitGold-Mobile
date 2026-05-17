# Feature Home Overview

## Status
- Phase: `Phase 6`
- Current status: core Home sections and Home-owned sheets are restored with Android-style shell, balance, savings, chart, Why HabitGold pager, and footer cards; cross-feature handoffs are still pending
- Source audit: [feature-home-audit.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/home/feature-home-audit.md:1)
- Last verified: `2026-05-16`
- Verification command: `./gradlew :composeApp:allTests :composeApp:compileKotlinIosSimulatorArm64`
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
  - recent activity handoff to the `History` tab
  - explicit snackbar messaging for still-deferred cross-feature routes
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

## Still Pending For Phase 6
- transaction drilldown navigation beyond the current `History` tab handoff
- gold value details, help/support destination, and editorial entry points
- coupon handoff and SIP verification dialog parity
- real feature-route wiring for buy, sell, profile, alerts, and savings details once those features are migrated
- final side-by-side Android parity review for spacing, borders, and motion

## Known Notes
- this slice intentionally keeps `Buy Gold`, `Sell Gold`, profile, alerts, and support/detail destinations as explicit pending handoffs until the linked feature routes are rebuilt
- recent-activity cards are mapped from the shared transaction preview model and currently hand off to the `History` tab until transaction-details routing lands
- Home now also soft-loads `GET sip/mandates` so the savings cards and active mandate pager can render off shared state
- the Daily savings row now has a shared animated border treatment to stay closer to Android’s highlighted Daily card
- Home chart history caches the 1Y series briefly and slices shorter ranges locally, then appends the current live price as the trailing chart point
- there are non-blocking warnings from:
  - existing deprecated `ClickableText` in auth
  - deprecated `kotlinx.datetime` APIs used in Home date formatting that should be cleaned up later
