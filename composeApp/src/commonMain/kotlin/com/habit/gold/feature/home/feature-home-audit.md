# Feature Home Audit

## Status
- Audit status: approved source audit for restarting Phase 6
- Migration status: implementation not started on this branch
- Goal: rebuild Home as a close Android replica before expanding into related flows

## Audit Process Rule
Every feature audit must be run multiple times before implementation begins.

Minimum audit passes:
1. structure pass
   - routes
   - sections
   - callbacks
   - data sources
2. visual pass
   - colors
   - shapes
   - borders
   - spacing
   - iconography
   - alignment
   - empty/loading states
3. interaction pass
   - default expanded/collapsed states
   - bottom-sheet behavior
   - pager behavior
   - scroll behavior
   - bottom-bar overlap/insets
   - keyboard behavior if applicable
   - repeated taps and dismissal behavior

Do not treat a feature audit as complete after a single read of the Android screen file.
The audit must be revisited after the first implementation outline so issues like wrong default state, extra bottom spacing, non-working pager interactions, missing shimmer, or alignment drift are caught before coding deepens.

## Android Sources Audited
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/HomeScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/home/HomeViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/components/BottomNavigationBar.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/MainActivity.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/navigation/NavGraph.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/navigation/Routes.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/components/HomeScreenFooter.kt`

## Main Conclusion
Home is not a simple dashboard. It is the main logged-in entry shell that combines:
- bottom navigation
- top toolbar and alerts
- live portfolio summary
- zero-balance acquisition state
- trust education cards
- gold savings entry cards
- active savings overview
- recent transaction drilldown
- footer support entry
- force-update checks
- live gold-price bottom sheet
- HabitGold intro bottom sheet

Phase 6 must start by matching the Android shell and Home structure closely. It should not begin as a simplified reinterpretation.

## Pre-Implementation Audit Checklist
- verify section order from the Android screen
- verify exact bottom-nav items and shell visibility behavior
- verify default collapsed/expanded states
- verify loading treatment such as shimmer vs static placeholders
- verify bottom inset handling against the shell and bottom bar
- verify footer alignment, badge sizing, and lower-section spacing
- verify all Home-owned sheets, pagers, charts, and close/dismiss affordances
- verify cross-feature entry points are mapped even if the destination feature is not yet migrated
- verify assets, icons, and copy are traced back to Android resources

## Shell Parity Requirements

### Bottom Navigation
Android bottom navigation is defined in `BottomNavigationBar.kt`.

Current Android tabs:
- `Home`
- `Rewards`
- `History`

Important rules:
- tab routes are `Route.Home`, `Route.ReferEarn`, `Route.History`
- the bar is white with rounded top corners and a border
- selected state uses `HabitGoldPrimary`
- it is wrapped in `AnimatedVisibility` from `MainActivity.kt`
- it is not a generic placeholder shell

Migration rule:
- KMP app shell must not invent new tabs like `Transactions` or `Profile`
- Phase 6 must restore Android tab identity first before deeper Home content work

### Bottom Bar Visibility
Android shows the bar through `AnimatedVisibility` in `MainActivity.kt`.

Important behavior:
- fade + vertical slide animation
- shell decides when bar is visible
- `Splash`, auth, and several leaf screens are outside the bottom-nav experience

Migration rule:
- do not model the bar as permanently visible by default
- visibility logic belongs to the shared shell, not inside Home itself

## Home Route Responsibilities
`Route.Home` in Android is wired in `NavGraph.kt`.

The Home route launches `HomeScreen(...)` with these linked callbacks:
- profile
- alerts
- buy gold
- start journey
- sell gold
- gold value details
- UPI autopay manage
- editorial insights
- help center
- history
- savings setup for daily / weekly / monthly
- transaction details

Migration rule:
- Home must be audited and implemented as a navigation hub, not only a render-only screen

## Home Data Dependencies
Android `HomeViewModel` owns these data sources and behaviors:

### Data loaded
- portfolio dashboard from `authRepository.getPortfolioDashboard()`
- recent transactions from `tradeRepository.getTransactions(page = 1, limit = 5)`
- SIP mandates from `sipRepository.getSipMandates()`
- SIP coupon preview from `GetAvailableCouponsUseCase`
- user features from `GetUserFeaturesUseCase`
- buy gold flow start from `BuyGoldUseCase`
- SIP setup flow start from `sipRepository.createSipMandateSession(...)`
- buy order status polling
- SIP mandate status polling
- SIP resume action

### Cached / polling behavior
- dashboard cache TTL: `60_000ms`
- force update cache TTL: `15 min`
- buy status polling: `10` polls every `2s`
- SIP status polling: `5` polls every `3s`

### Shared external dependencies
- `LivePriceManager.priceFlow`
- `DataStoreManager.isBalanceVisible`
- `ForceUpdateManager`

Migration rule:
- Home state is not just one API call
- Phase 6 should separate shell, dashboard, recent activity, SIP state, and force-update concerns in KMP, but preserve visible behavior

## Screen Composition Order
Android `HomeScreen.kt` renders the page in this order:

1. `HomeTopBar`
2. `HomeContentList`
3. optional bottom sheet

Inside `HomeContentList`, the major order is:

1. shimmer sections while loading
2. either zero-balance state or balance card
3. trust highlights + gold savings cards when balance exists
4. active savings pager section when active mandates exist
5. recent activity
6. why Habit Gold
7. footer / support block

Migration rule:
- section order should stay consistent with Android unless there is an explicit product change

## Top Bar Audit
Android top bar is defined in `HomeTopBar(...)`.

Important visual details:
- white background
- bottom divider line
- `statusBarsPadding()`
- `16dp` horizontal padding and `12dp` vertical padding
- left side:
  - circular avatar tile
  - `Hello,`
  - compact display name with truncation
- right side:
  - live gold price pill when rate is available
  - alerts circular button

Live gold price pill details:
- purple-tinted surface
- light border
- `LiveWaveIndicator`
- tiny two-line label
- opens gold-price bottom sheet

Migration rule:
- toolbar must be rebuilt from this exact Android structure
- do not replace it with a generic app bar

### Exact top-bar visual ledger
- container: white row with custom bottom divider drawn in `drawBehind`
- padding: `16dp` horizontal, `12dp` vertical, plus `statusBarsPadding()`
- avatar:
  - `36dp` circle
  - purple-tinted background
  - initials text `14sp`, extra bold
- greeting block:
  - `Hello,` at `12sp`
  - username at `16sp`
  - compact line height with `PlatformTextStyle(includeFontPadding = false)`
- live price pill:
  - `Surface`
  - shape `RoundedCornerShape(24dp)`
  - purple-tinted background
  - border `1dp`
  - internal padding `16dp` horizontal and `6dp` vertical
  - live-wave indicator `12dp`
  - first line `8sp`
  - second line `10sp`
- alerts chip:
  - `36dp` circle
  - slate background
  - notification icon `20dp`

## Zero-Balance State Audit
When `totalGoldBalance <= 0`, Android shows `HomeZeroBalanceSection`.

Contents:
- `HomeStartJourneyCard`
- trust highlights carousel
- gold savings plans card

The start journey card includes:
- purple primary background
- gold tower illustration
- strong CTA text
- chevron
- buy/start-journey navigation

Migration rule:
- zero-balance Home is a first-class state, not an empty placeholder

### Zero-balance visual ledger
- `HomeStartJourneyCard`
  - shape `RoundedCornerShape(18dp)`
  - height `84dp`
  - full-width row with `16dp` horizontal outer padding
  - purple background
  - border `1dp` using `HabitGoldPrimaryLight`
  - left illustration lane width `74dp`
  - title text `20sp`
  - chevron size `30dp`
- sequence under zero balance:
  - start journey card
  - trust highlights carousel
  - gold savings plans card

## Balance State Audit
When balance exists, Android shows `HomeBalanceCard`.

Important behaviors:
- large total gold balance display
- balance visibility toggle
- expansion/collapse toggle
- invested value and current value breakdown
- profit badge
- view details entry
- `BUY GOLD` and `SELL GOLD` actions
- dashboard error text appears inside the card when cached data exists but refresh fails

Important visual traits:
- purple full card
- large white typography
- gold coin decoration
- white button + bordered transparent button pair

Migration rule:
- this card should be split into smaller KMP components internally, but the result must still look like the Android card

### Balance-card visual ledger
- outer card:
  - shape `RoundedCornerShape(18dp)`
  - purple background
  - default elevation `8dp`
  - outer horizontal padding `16dp`
- content padding: `20dp`
- top row:
  - left icon `AccountBalance` at `16dp`
  - visibility toggle `20dp`
  - expand toggle `24dp`
- balance row:
  - gold amount text `32sp`
  - unit label `18sp`
  - gold coin cluster beside amount
- collapsed profit chip:
  - `Surface`
  - shape `RoundedCornerShape(12dp)`
  - semi-transparent white background
- expanded area:
  - `AnimatedVisibility`
  - divider line
  - invested/current value columns
  - profit pill uses `RoundedCornerShape(24dp)`
- actions:
  - buy button is custom white box
    - height `46dp`
    - shape `RoundedCornerShape(12dp)`
    - border `1dp` with `HabitGoldPurple200`
    - shadow `4dp`
  - sell button:
    - height `46dp`
    - transparent background
    - white border `1dp`
    - shape `RoundedCornerShape(12dp)`

## Gold Savings Audit
Home contains two separate savings areas:

### Gold Savings entry cards
`GoldSavingPlansCard`

Purpose:
- show Daily / Weekly / Monthly savings entry points
- adapt labels based on active or paused mandate
- allow resume for paused plans

Important behavior:
- one card per frequency
- Daily card has animated border
- title changes like `Upgrade Daily Savings` or `Resume Weekly Savings`
- subtitle changes to current amount when mandate exists

### Active savings overview
`HomeSipMandatesSection`

Purpose:
- show active mandates in a horizontal pager
- optional `View All` -> `UPI Autopay Manage`

Card details:
- status pill
- amount
- cadence label
- next debit / started date
- optional promo code
- optional bank attention row

Migration rule:
- savings entry cards and active savings overview are separate Home sections and must stay separate in KMP

### Savings visual ledger
- section heading:
  - `Gold Savings Habit`
  - `20sp`
  - underline bar width `120dp`, height `3dp`
- plan card row items:
  - outer surface shape `RoundedCornerShape(16dp)`
  - border `1dp`
  - inner padding `12dp`
  - left icon tile `42dp`
  - icon tile shape `RoundedCornerShape(12dp)`
  - resume button height `36dp`, shape `RoundedCornerShape(10dp)`
  - default trailing arrow chip is `32dp` circle
- daily plan special case:
  - animated border via `homeAnimatedSavingsBorder()`
  - base stroke `1dp`
  - animated stroke `2dp`
  - loop duration `5000ms`
- active savings pager card:
  - card shape `RoundedCornerShape(22dp)`
  - white / gold / red-tinted background based on status
  - border `1dp`
  - pill tags use `RoundedCornerShape(999dp)`

## Recent Activity Audit
Android `RecentActivitySection`:
- title: recent activity
- horizontal cards
- uses latest 3 items from recent transactions
- each card opens transaction details

Important mapping behavior in `toHomeRecentActivityUi()`:
- BUY -> bought Xg gold
- SELL -> sold Xg gold
- DELIVERY -> delivery for Xg gold
- SIP transactions show SIP name when available
- status color changes by state
- date formatting has multiple backend parser fallbacks

Migration rule:
- do not reduce recent activity to a simple text list
- keep the mapped transaction presentation and drilldown behavior

### Recent-activity visual ledger
- title `Recent Activity` at `18sp`
- `LazyRow` with `16dp` horizontal content padding
- card width `220dp`
- card shape `RoundedCornerShape(16dp)`
- top icon tile:
  - `40dp`
  - shape `RoundedCornerShape(12dp)`
- trailing chevron `16dp`

## Trust / Why Habit Gold / Footer Audit
Home includes additional non-financial presentation sections:

### Trust highlights carousel
- four cards
- auto-advancing pager
- each card opens HabitGold intro bottom sheet at a target page

### Why Habit Gold section
- separate editorial trust section lower on the page

### Footer
`HomeScreenFooter`

Contains:
- support contact card
- large trust statement
- three trust badges:
  - `100% Physical`
  - `100% Insured`
  - `Brink's Vault`
- `PoweredBySafeGoldRow()`

Migration rule:
- footer is part of Home parity
- it should not be cut just because it looks “extra”

### Lower-section visual ledger
- trust highlights carousel cards:
  - shape `RoundedCornerShape(8dp)`
  - border `1dp`
  - gradient backgrounds vary per slide
  - page spacing `12dp`
- why Habit Gold section:
  - title `18sp`
  - horizontal cards from `HabitGoldCard`
- footer support card:
  - shape `RoundedCornerShape(20dp)`
  - border `1dp`
  - left icon tile `40dp`, shape `RoundedCornerShape(12dp)`
- trust footer badges:
  - three equal columns
  - labels:
    - `100% Physical`
    - `100% Insured`
    - `Brink's Vault`

## Bottom Sheets
Android Home owns these bottom sheets:
- `GOLD_PRICE`
- `HABITGOLD_GOLD_INTRO`

Behavior:
- `GoldPriceChart` can route into buy
- `HabitGoldSheetContent` can route into buy

Migration rule:
- these bottom sheets are part of Phase 6 parity, even if implemented after the main scroll content

## Animation And Motion Ledger

### Shell / bar animation
- bottom bar is animated from `MainActivity.kt`
- enter:
  - `fadeIn`
  - `slideInVertically(initialOffsetY = it / 2)`
  - duration `220ms`
- exit:
  - `fadeOut`
  - `slideOutVertically(targetOffsetY = it / 2)`
  - duration `120ms`

### Home-specific animation
- pull-to-refresh uses `rememberPullToRefreshState()`
- trust highlights pager:
  - auto-advances every `4500ms`
  - page animation duration `650ms`
  - easing `FastOutSlowInEasing`
- balance expanded details:
  - `AnimatedVisibility`
  - `expandVertically + fadeIn`
  - `shrinkVertically + fadeOut`
- trust highlights pager container:
  - `animateContentSize()`
- savings daily border:
  - infinite transition
  - duration `5000ms`
- splash overlay animation belongs to shell, not Home, but Home lives under that shell

## API And Repository Ledger

### Home screen API dependencies
- `GET portfolio`
  - via `authRepository.getPortfolioDashboard()`
  - endpoint in `ApiService`: `portfolio`
- `GET trade/transactions?page=1&limit=5`
  - via `tradeRepository.getTransactions(page = 1, limit = 5)`
- `GET sip/mandates`
  - via `sipRepository.getSipMandates()`
- `GET user/features`
  - via `GetUserFeaturesUseCase`
- `GET promo/coupons/available`
  - via `GetAvailableCouponsUseCase`
  - Home uses it for SIP coupon preview only
- `POST trade/buy`
  - via `BuyGoldUseCase`
- `POST sip/mandate/session`
  - via `sipRepository.createSipMandateSession(...)`
- `GET trade/orders/{orderId}`
  - via `GetTransactionStatusUseCase`
- `GET sip/mandates/{id}`
  - for SIP verification polling
- `POST sip/mandate/{id}/resume`
  - for paused savings resume

### Home-specific API usage notes
- Home does not directly own the live gold price API in this file; it consumes `LivePriceManager.priceFlow`
- force update is derived from `user/features`, then mapped through `toForceUpdateStateOrNull()` into `ForceUpdateManager`
- Home recent activity only fetches the first page, limited to `5`, then shows top `3` mapped items
- buy and SIP actions started from Home are not leaf flows; they hand off into payment / setup journeys

## String And Copy Ledger

### Resource-backed Android strings clearly used by Home
- `home_screen_recent_activity`
- `home_screen_start_your_gold_journey`
- `home_screen_total_gold_balance`
- `home_screen_trust_slide_real_gold_title`
- `home_screen_trust_slide_real_gold_description`
- `home_screen_trust_slide_stored_safely_title`
- `home_screen_trust_slide_stored_safely_description`
- `home_screen_trust_slide_free_delivery_title`
- `home_screen_trust_slide_free_delivery_description`
- `home_screen_trust_slide_earn_every_time_title`
- `home_screen_trust_slide_earn_every_time_description`
- `common_need_help_with_something`
- `common_support_is_available_for_payments_shipping_and_orders`
- `common_your_gold_is_100percent_secured`
- `common_why_habitgold`
- `common_invested_value`
- `common_current_value`
- `common_view_details`
- `buy_gold_screen_buy_gold`
- `sell_gold_screen_sell_gold`
- `buy_gold_screen_save_weekly_on`
- `buy_gold_screen_confirm`
- `buy_gold_screen_first_debit_happens_today_auto_debit_begins_next`

### Hardcoded copy still present in Android Home
These should be explicitly mapped during migration and not silently rewritten:
- `Hello,`
- `Live Gold Price:`
- `Your Savings`
- `Track all your savings at a glance`
- `View All`
- `AUTO INVEST`
- `Next debit`
- `Started`
- `Bank confirmation pending`
- `Gold Savings Habit`
- `Start Daily Savings`
- `Start Weekly Savings`
- `Start Monthly Savings`
- `Upgrade <frequency> Savings`
- `Resume <frequency> Savings`
- `Starts from just ₹10/day`
- `Starts from just ₹50/week`
- `Starts from just ₹50/month`
- `Current Savings ...`
- `Earn rewards on every payment`
- `₹10 bonus × next 10 payments`
- `0.5% reward on all purchases`
- `Instantly convert rewards to gold`
- `Hide selector`
- `Show selector`
- `Resume`
- `Active`
- `Paused`

## Shape / Border Parity Rule
During implementation, every Home subsection must preserve:
- exact corner-radius family
- whether the Android version uses border or elevation or both
- whether icon tiles are circle vs rounded rectangle
- whether backgrounds are flat, white, tinted, or gradient

If a section is intentionally changed, that must be documented explicitly before coding the deviation.

## Non-UI Behavior That Still Belongs To Home
- notification permission request on entry for Android 13+
- pull-to-refresh
- dashboard refresh with cached fallback
- silent SIP mandate failure handling
- silent feature-call failure handling for force-update
- force-update trigger from `user/features`
- balance visibility persistence through datastore

Migration rule:
- these behaviors should be captured in the Home contract and audit checklist, not rediscovered mid-implementation

## Home Navigation Matrix
Direct destinations triggered from Home:
- `Route.BuyGold`
- `Route.WithdrawalMode`
- `Route.Profile`
- `Route.Alerts`
- `Route.GoldValueDetails`
- `Route.UpiAutopayManage`
- `Route.EditorialInsights`
- `Route.HelpCenter`
- `Route.History`
- `Route.GoldSavingDaily`
- `Route.GoldSavingWeekly`
- `Route.GoldSavingMonthly`
- `Route.TransactionDetails`

Indirect linked domains exposed by Home:
- buy
- sell
- SIP
- alerts
- profile
- history
- support
- editorial

Migration rule:
- Home must not be built in isolation from these route contracts

## What Phase 6 Should Not Do
- invent new tab structure
- replace the Android toolbar with a generic app bar
- collapse Home into only a balance card + list
- merge savings cards and active savings pager into one generic widget
- skip the trust/footer sections for speed
- build a `clean first draft` that is visually unlike Android

## Recommended Implementation Order
1. Restore Android shell parity
   - bottom nav items
   - bottom bar visibility behavior
   - route ownership
2. Build Home contract and state model
   - dashboard
   - recent activity
   - mandates
   - bottom sheet state
   - refresh / force update
3. Implement `HomeTopBar`
4. Implement zero-balance and balance-card split
5. Implement trust highlights and gold savings cards
6. Implement active savings pager
7. Implement recent activity cards
8. Implement lower trust/footer sections
9. Add bottom sheets
10. Run strict Android parity review before moving deeper into Buy/Sell/SIP refinements

## Definition Of Done For Phase 6
- bottom nav matches Android product flow
- top bar matches Android structure and density
- zero-balance and balance states match Android behavior
- Home sections appear in Android order
- Home routes open the same downstream flows
- recent activity and savings behavior match Android intent
- feature doc is updated from audit to implementation overview once coding starts
