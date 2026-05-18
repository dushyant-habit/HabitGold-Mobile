# Feature Rewards Overview

## Status

- Phase: `Phase 10`
- Current status: `shared rewards home, refer detail, rewards history, and rewards redeem are live; referral product decisions still pending`
- Source audit: [feature-rewards-audit.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/rewards/feature-rewards-audit.md:1)

## Scope

This feature boundary covers:

- Rewards Home
- Refer & Earn Detail
- Rewards History
- Rewards Redeem

This feature explicitly does **not** currently include:

- Referral Status
- Referral History

Those two Android screens are dummy/local-state driven and remain deferred until product confirms whether they should exist in shared form.

## Locked Decisions

- `MainTab.Rewards` should open shared Rewards Home
- Rewards Home should own navigation to:
  - Refer & Earn Detail
  - Rewards History
  - Rewards Redeem
- Rewards Redeem is rewards-routed but should reuse shared Trade buy/payment foundations
- Rewards surfaces are mostly Compose-native and string-driven rather than asset-heavy
- shared implementation should reuse:
  - Trade live price and buy plumbing
  - Auth referral backend contract where relevant
  - shared keyboard/focus-dismiss rule for inputs

## Current Shared Slice

- `feature/rewards` data/domain/repository/use cases
- real `MainTab.Rewards` route
- Rewards Home
- Rewards History
- Refer & Earn Detail
- Rewards Redeem
- share invite and QR code flow on Android and iOS
- buy / SIP deep links from Refer & Earn

## Pending Slice

1. referral status/history product decision
2. final micro-parity pass on rewards home / refer detail / redeem
3. maintainability cleanup for oversized Rewards UI files before Phase 10 closure

## Quality Rule

Before Phase 10 Rewards is marked complete:

- keep file-size and responsibility under control while writing
- avoid inventing extra sections or placeholder cards not present in Android
- extract shared helpers early if Rewards begins duplicating Trade formatting or payment logic
- verify keyboard/focus behavior on redeem amount entry
- preserve exact Android colors and gradients:
  - use exact theme/custom tokens from Android
  - preserve gradient stop order and direction
  - do not use “close enough” color replacements
- do not ignore file-size drift:
  - `RewardsHomeSections.kt` and `RewardsReferDetailSections.kt` still need an explicit maintainability review before closure
- keep docs aligned with the verified implementation state
