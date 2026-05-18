# Phase 10 Rewards And Referral Audit

## Status

Audit status: `strict Android-source audit complete`

Implementation status: `shared rewards home, refer detail, rewards history, and rewards redeem are implemented`

Android source of truth used for this audit lives in:

- [ReferEarnScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnScreen.kt:1)
- [ReferEarnViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnViewModel.kt:1)
- [ReferEarnDetailScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnDetailScreen.kt:1)
- [ReferEarnDetailViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnDetailViewModel.kt:1)
- [RewardsHistoryScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/RewardsHistoryScreen.kt:1)
- [RewardsHistoryViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/RewardsHistoryViewModel.kt:1)
- [RewardsRedeemScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/RewardsRedeemScreen.kt:1)
- [RewardsRepositoryImpl.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/repository/RewardsRepositoryImpl.kt:1)
- [RewardsMilestonesDto.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/model/RewardsMilestonesDto.kt:1)
- [RewardsHistoryDto.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/model/RewardsHistoryDto.kt:1)
- [ReferDetailsDto.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/model/ReferDetailsDto.kt:1)
- [RewardsFlowToolbar.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/components/RewardsFlowToolbar.kt:1)
- [SwipeToRedeemTrack.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/components/SwipeToRedeemTrack.kt:1)
- [Routes.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/navigation/Routes.kt:1)
- [NavGraph.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/navigation/NavGraph.kt:1)
- [ApiService.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/remote/ApiService.kt:1)

## Main Conclusion

Rewards is not a thin placeholder tab. It is a product area with four real subflows and two explicit non-goals:

- `Rewards Home` is the bottom-tab root
- `Refer & Earn Detail` is a real backend-backed child route
- `Rewards History` is a real backend-backed child route
- `Rewards Redeem` is a real checkout flow, but should reuse the shared Trade buy/payment foundation
- `Referral Status` is dummy/local-state only
- `Referral History` is dummy/local-state only

That means shared Phase 10 should port:

1. `Rewards Home`
2. `Refer & Earn Detail`
3. `Rewards History`
4. `Rewards Redeem`

And should **not** blindly port:

- `ReferralStatusScreen`
- `ReferralHistoryScreen`

Those two remain redesign/defer decisions, not faithful migration targets.

## Route Ownership

Android routes:

- `Route.ReferEarn`
- `Route.ReferEarnDetail`
- `Route.RewardsHistory`
- `Route.RewardsRedeem`
- `Route.ReferralStatus`
- `Route.ReferralHistory`

Shared ownership decisions locked from this audit:

- `MainTab.Rewards` should open shared `Rewards Home`
- `Rewards Home` owns navigation to:
  - `Refer & Earn Detail`
  - `Rewards History`
  - `Rewards Redeem`
- `Refer & Earn Detail` owns:
  - history action in toolbar
  - share action
  - QR dialog
  - deep links into Buy Gold / Weekly SIP
- `Rewards Redeem` is rewards-routed but Trade-powered
- `ReferralStatus` and `ReferralHistory` should not be added to the active shared route graph until product confirms they still matter

## Current Shared Implementation

Implemented from this audit:

- Rewards tab now opens shared Rewards Home
- Rewards Home shimmer/header switching and milestone mapping are shared
- Rewards History is shared
- Refer & Earn Detail is shared

Still pending from this audit:

- product decision on Referral Status / Referral History
- final micro-parity hardening on Rewards Home / Refer & Earn Detail / Rewards Redeem
- explicit maintainability cleanup for oversized Rewards UI files before commit

## API And Logic Inventory

Rewards-specific backend APIs:

- `GET rewards/milestones`
- `GET rewards/history`
- `GET rewards/refer-details`

Cross-feature backend reuse:

- `GET user/features`
- `POST trade/buy`
- `GET trade/orders/{orderId}`

### Rewards home logic

Source:
- [ReferEarnViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnViewModel.kt:1)

Important rules:

- loads two data sources:
  - rewards milestones
  - user features
- both are cached with TTL:
  - rewards milestones: `60s`
  - user features: `5 min`
- first load uses blocking shimmer only when there is no prior success state
- refresh keeps old content visible with `isRefreshing = true`
- rewards home refreshes on nav resume
- header switches from `PreJourney` to `PostJourney` when `totalEarnedInr > 0`
- milestone journey only appears when:
  - `milestonesActive == true`
  - and feature flag `rewards.isActive == true`
- milestone progress is cumulative:
  - active row progress = `totalPaidGoldGrams / cumulativeThreshold`
- lifetime booster state depends on:
  - all milestone rows completed
  - `boosterActive`

### Refer detail logic

Source:
- [ReferEarnDetailViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnDetailViewModel.kt:1)

Important rules:

- loads once on init
- uses backend defaults with local fallbacks on failure
- `referralCode` resolution order:
  - default active code from `referralList`
  - first non-blank code from `referralList`
  - top-level `referralCode`
  - local fallback `"SAVEGOLD20"`
- calculator uses a cashback fraction derived from `currentPercentage`, fallback `0.5%`
- extension cards are backend-driven:
  - buy threshold
  - referral first buy
  - SIP min amount
- QR dialog content is generated locally from `referralInviteLink(referralCode)`

### Rewards history logic

Source:
- [RewardsHistoryViewModel.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/RewardsHistoryViewModel.kt:1)

Important rules:

- loads immediately on entry
- refresh preserves previous items
- no pagination despite DTO having `nextCursor` and `hasMore`
- title and chip mapping come from `kind`, `source`, `debitType`, and `direction`
- expiry label rules:
  - `Expired` when `expired == true`
  - `Expiring <date>` only for `CREDIT` rows with `expiresAt`
- date formatting:
  - `createdAt` -> `dd MMM, yyyy`

### Rewards redeem logic

Source:
- [RewardsRedeemScreen.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/RewardsRedeemScreen.kt:1)

Important rules:

- depends on both:
  - rewards milestone state for redeemable balance
  - Trade buy flow for checkout and order confirmation
- `redeemableMaxInr` is parsed from rewards home `redeemableDisplay`
- auto-seeds amount to max redeemable when empty
- hard minimum redeem amount:
  - `₹10`
- blocks start when:
  - rewards still loading
  - no redeemable balance
  - amount below `₹10`
  - live price refreshing / expired / missing rate id
- amount field behavior:
  - numeric decimal keyboard
  - `Done` clears focus
  - preset chips clear focus before applying
- no outside-tap focus dismissal is visible in Android source; we should still apply the shared KMP rule consistently
- checkout result handling reuses Buy Gold state machine:
  - `CheckoutReady`
  - `Success`
  - `Processing`
  - `Error`
- success refreshes Rewards Home state afterward

## UI Surface Map

### 1. Rewards Home

Top bar:

- custom `RewardsHomeTopBar`
- left: title `Rewards`
- right: history icon only

Initial load:

- full-screen shimmer, not spinner
- shimmer structure includes:
  - header placeholder
  - extra reward card placeholder
  - refer win card placeholder
  - milestone placeholder sequence

Content order:

1. animated header:
   - `PreJourneyHeroCard`
   - or `TopRewardsCard`
2. `ExtraGoldRewardCard`
3. `ReferWinCard`
4. `SwipeToRedeemTrack` only when post-journey header is active
5. `MilestoneRewardsSection`
6. `GrandRewardCard` / lifetime booster row

Behavior notes:

- pull-to-refresh spinner is under toolbar
- content uses nested scroll for pull-to-refresh
- no generic placeholder cards should be invented outside this section order

### 2. Refer & Earn Detail

Top bar:

- centered title `Refer & Earn`
- back on left
- history icon on right

Body order:

1. `TopWinAssuredCard`
2. `BoosterActiveCard`
3. `EstimateEarningsCalculator`
4. `ReferralCodeSection`
5. bottom fixed CTA row:
   - `Share invite`
   - `My QR`

Special UI behaviors:

- QR is a modal dialog with generated QR image
- referral code copy shows toast
- calculator is expandable/collapsible
- extension info is structured as three extension cards inside the booster section

### 3. Rewards History

Top bar:

- centered title `Rewards history`
- back only

Body states:

- loading spinner in center
- error center state with retry text button
- success list
- empty text inside success list

Success order:

1. optional refresh linear progress
2. helper intro copy
3. list rows

Row structure:

- icon badge
- title
- source chip
- date
- optional expiry label
- signed amount on right

### 4. Rewards Redeem

Top bar:

- centered title `Redeem rewards`
- back only

Body order:

1. `RewardsRedeemHeroCard`
2. `RewardsRedeemAmountCard`
3. `RewardsRedeemSummaryCard`
4. spacer for bottom bar

Bottom bar:

1. live price bar
2. inline minimum redeem error when needed
3. row with:
   - `Cancel`
   - primary CTA `Redeem <amount>`

Success state:

- success icon
- title/subtitle
- summary card
- fixed bottom CTA `Back to rewards`

Processing state:

- non-dismissible dialog
- title and subtitle variant based on `Polling` vs initial processing

## Strings And Asset Ledger

String-heavy surfaces:

- rewards home section headings
- milestone journey copy
- redeem messages
- refer detail calculator labels
- referral code copy / QR text
- rewards history helper and expiry labels

Important string groups in Android:

- [strings.xml rewards entries](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/res/values/strings.xml:165)

Asset verdict:

- no dedicated rewards drawable pack was found in `res/`
- rewards surfaces are mostly Compose-native:
  - gradients
  - Material icons
  - shape/background styling
  - generated QR bitmap
- one reusable component exists for the swipe track:
  - [SwipeToRedeemTrack.kt](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/components/SwipeToRedeemTrack.kt:1)

## Must Match Android Exactly

These should match Android closely in the first shared implementation pass:

- exact color tokens and gradients:
  - no approximate replacements for hero purples, gold gradients, chip fills, or track colors
  - preserve gradient stop colors, stop order, and direction from Android
- Rewards Home top bar, shimmer, section order, and header switching
- `SwipeToRedeemTrack`
- Refer Detail section order and fixed bottom CTA row
- Rewards History top bar, helper copy placement, and row mapping
- Rewards Redeem hero, amount card, summary card, bottom bar, and success/processing states

These should reuse shared KMP foundations instead of being reimplemented:

- Trade live price + buy checkout plumbing for `RewardsRedeem`
- shared focus-dismiss rule on interactive text inputs
- shared Trade order confirmation / invoice boundaries
- shared referral submission contract from Auth

These are intentionally **not** implementation targets yet:

- `ReferralStatusScreen`
- `ReferralHistoryScreen`

## Phase 10 Rewards Implementation Order

1. build `feature/rewards` foundation:
   - DTOs
   - remote data source
   - repository
   - use cases
2. replace `MainTab.Rewards` placeholder with shared Rewards Home
3. add shared `Refer & Earn Detail`
4. add shared `Rewards History`
5. integrate `Rewards Redeem` using Trade buy foundations
6. leave `ReferralStatus` and `ReferralHistory` deferred until product confirms them

## Audit Verdict

Rewards is now decision-complete for implementation.

The next safe implementation slice is:

- `feature/rewards` data/domain foundation
- then `Rewards Home` only

Do **not** start with `Rewards Redeem` first, and do **not** migrate `ReferralStatus` / `ReferralHistory` as if they were production-backed flows.
