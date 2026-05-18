# Phase 10 History, Rewards, Referral, Alerts Audit

## Status

Audit status: `decision-complete for implementation start`

Implementation status: `not started`

This audit is intentionally stricter than earlier phase audits. It is meant to be detailed enough that Phase 10 implementation can follow it without making new product, route, or ownership decisions mid-stream.

It covers:

- transactions list
- transaction details and status drilldown
- rewards home
- rewards history
- rewards redeem
- refer and earn detail
- referral status / referral history decision
- alerts
- referral attribution ownership

## Audit Rules For Phase 10

This phase must not repeat earlier audit mistakes.

Before implementation:

1. inspect the Android screens line by line, not just by feature name
2. lock exact route ownership and back behavior
3. document exact UI section order for parity-critical screens
4. document first-paint rules so blocking loaders are only used where Android really uses them
5. keep code-quality review active during implementation, not only at the end

Mandatory interaction checks for every Phase 10 interactive screen:

- keyboard closes on outside tap
- shimmer vs blocking loader behavior is justified
- bottom-sheet open state and dismissal behavior match Android
- ripple vs no-ripple item behavior is captured
- sticky footer / CTA behavior is captured
- search and filter focus behavior is captured

Mandatory Phase 10 pre-commit gate:

- file-size / responsibility review
- duplication / shared-helper review
- unused-code / stale-route / stale-param sweep
- keyboard/focus behavior review
- localization review
- docs alignment with verified implementation

## Android Source Of Truth

Primary Android references audited:

- `app/src/main/java/com/habit/gold/navigation/NavGraph.kt`
- `app/src/main/java/com/habit/gold/navigation/Routes.kt`
- `app/src/main/java/com/habit/gold/di/KoinModules.kt`
- `app/src/main/java/com/habit/gold/screens/HistoryScreen.kt`
- `app/src/main/java/com/habit/gold/screens/HistoryViewModel.kt`
- `app/src/main/java/com/habit/gold/screens/TransactionDetailsScreen.kt`
- `app/src/main/java/com/habit/gold/screens/AlertsScreen.kt`
- `app/src/main/java/com/habit/gold/screens/ReferEarnScreen.kt`
- `app/src/main/java/com/habit/gold/screens/ReferEarnViewModel.kt`
- `app/src/main/java/com/habit/gold/screens/ReferEarnDetailScreen.kt`
- `app/src/main/java/com/habit/gold/screens/ReferEarnDetailViewModel.kt`
- `app/src/main/java/com/habit/gold/screens/RewardsHistoryScreen.kt`
- `app/src/main/java/com/habit/gold/screens/RewardsHistoryViewModel.kt`
- `app/src/main/java/com/habit/gold/screens/RewardsRedeemScreen.kt`
- `app/src/main/java/com/habit/gold/screens/ReferralStatusScreen.kt`
- `app/src/main/java/com/habit/gold/screens/ReferralHistoryScreen.kt`
- `app/src/main/java/com/habit/gold/data/remote/ApiService.kt`
- `app/src/main/java/com/habit/gold/data/repository/TradeRepositoryImpl.kt`
- `app/src/main/java/com/habit/gold/data/repository/RewardsRepositoryImpl.kt`
- `app/src/main/java/com/habit/gold/data/local/DataStoreManager.kt`
- `app/src/main/java/com/habit/gold/data/local/StoredAlert.kt`
- `app/src/main/java/com/habit/gold/notifications/HabitGoldMessagingService.kt`
- `app/src/main/java/com/habit/gold/referral/ReferralAttributionManager.kt`
- `app/src/main/java/com/habit/gold/data/model/TradeDtos.kt`
- `app/src/main/java/com/habit/gold/data/model/RewardsMilestonesDto.kt`
- `app/src/main/java/com/habit/gold/data/model/RewardsHistoryDto.kt`
- `app/src/main/java/com/habit/gold/data/model/ReferDetailsDto.kt`

## Current Shared KMP State

Current shared truth before Phase 10 work:

- bottom tabs exist as `Home`, `Rewards`, `History`
- `Rewards` tab still renders a placeholder in `AppShellScreen`
- `History` tab still renders a placeholder in `AppShellScreen`
- Home still routes alerts to a deferred placeholder target
- Trade already owns shared:
  - transactions API
  - transaction status polling
  - invoice logic
  - user VPA list / verify / set-default
- Savings already owns shared SIP mandate management
- Auth already owns referral-code submission at signup
- core storage already has `AppPreferencesStorage.hasUnreadAlerts`

This means Phase 10 should not duplicate existing Trade or Auth foundations. It should build feature-owned routes and presentation around the shared backend primitives that already exist.

## Main Conclusion

Phase 10 is not one feature. It is four connected feature boundaries:

- `feature/history`
- `feature/rewards`
- `feature/referral`
- `feature/alerts`

There are also two explicit cross-feature seams:

- `RewardsRedeem` is rewards-routed but reuses the Trade buy/payment foundation
- referral attribution is feature-owned in shared code, but Install Referrer and push/deep-link capture stay platform-owned

Two Android screens are not trustworthy as final product truth:

- `ReferralStatusScreen`
- `ReferralHistoryScreen`

Both are currently dummy/local-state driven and should be treated as redesign candidates until product confirms they still matter.

## Ownership Decisions

### `feature/history`

Owns:

- transactions list tab
- type and status filters
- pull-to-refresh / pagination
- route into transaction details

Uses shared foundations from Trade and Delivery for:

- transaction fetching
- transaction status mapping
- invoice drilldown rules

### `feature/rewards`

Owns:

- rewards home tab
- rewards milestone journey
- rewards history
- rewards redeem route

Uses shared foundations from:

- Trade buy/payment for `RewardsRedeem`
- referral sharing helper for invite actions
- Home / Savings / Trade deep links for CTA handoffs

### `feature/referral`

Owns:

- refer and earn detail screen
- referral attribution contract
- referral status/history decision

Platform-specific capture remains outside shared code:

- Install Referrer
- app-link / deep-link extraction

Shared code should own:

- pending referral attribution state and backend-facing submission rules

### `feature/alerts`

Owns:

- alerts route
- alerts list UI
- read/unread presentation

Storage can remain in a shared app-preferences/core storage layer.

Platform-specific push bindings stay platform-owned:

- FCM/APNs setup
- notification receive callbacks

## Route Inventory

Android route inventory relevant to Phase 10:

- `Route.History`
- `Route.TransactionDetails`
- `Route.ReferEarn`
- `Route.ReferEarnDetail`
- `Route.RewardsHistory`
- `Route.RewardsRedeem`
- `Route.ReferralStatus`
- `Route.ReferralHistory`
- `Route.Alerts`
- `Route.DownloadStatement`

Current ownership and entry points:

- bottom tab `History` opens `Route.History`
- bottom tab `Rewards` opens `Route.ReferEarn`
- Home top bar opens `Route.Alerts`
- Home recent activity can open:
  - `Route.History`
  - `Route.TransactionDetails`
- rewards home can open:
  - rewards detail
  - rewards history
  - rewards redeem
  - referral status/history
- refer detail can deep-link into:
  - buy gold
  - weekly SIP setup

Shared route targets that must land in Phase 10:

- `MainTab.History`
- `MainTab.Rewards`
- `HomeDeferredTarget.Alerts`

## Screen Inventory

### 1. Transactions list

Android route: `Route.History`

Screen type:

- full-screen tab-owned route

Top structure:

- custom toolbar
- trailing status filter action
- second row type chips:
  - All
  - Buy Gold
  - Sell Gold
  - Delivery

Body states:

- shimmer loading list
- error state with retry
- success list with pull-to-refresh
- pagination spinner at the bottom
- distinct empty copy for trade vs delivery filters

Important logic:

- default status view is not all statuses
- default includes success, refund, and certain in-progress payouts
- pagination uses page + limit with TTL cache
- refresh preserves previous list while refetching
- transaction rows are clickable and navigate to details with denormalized args

Interaction rules:

- filter chips are inline, not a sheet
- status filter opens a bottom sheet
- pull-to-refresh is active on the list itself

### 2. Transaction details

Android route: `Route.TransactionDetails`

Screen type:

- pushed full-screen drilldown from history and Home activity

Top structure:

- standard top app bar

Body structure:

- summary card
- optional SIP badge
- detail rows:
  - Transaction ID
  - Type
  - Status
  - SIP frequency if relevant
  - Amount
  - Gold quantity
- invoice button

Important logic:

- display is seeded from route args first
- invoice rules differ between trade and delivery
- delivery invoice fetch has fallback chain:
  - direct invoice endpoint
  - delivery order details
  - `sgInvoiceId` URL fallback
- trade invoice only available for successful / complete states

Ownership note:

- this should remain a shared transaction-details route in Phase 10, but it must reuse the stricter Trade hardening later instead of diverging.

### 3. Alerts

Android route: `Route.Alerts`

Screen type:

- pushed full-screen route from Home

Top structure:

- centered top app bar

Body states:

- centered empty state
- otherwise list of cards

Card structure:

- icon circle
- unread dot
- title
- description
- relative time

Important logic:

- no backend page fetch
- backed by locally persisted alerts list
- opening screen marks all alerts read
- notifications are recorded by FCM service into `DataStoreManager`

### 4. Rewards home

Android route: `Route.ReferEarn`

Screen type:

- bottom tab root for rewards

Top structure:

- custom rewards top bar

Header behavior:

- `PreJourney` header
- `PostJourney` header
- selected based on rewards milestone data

Body structure:

- pre/post journey hero
- extra rewards card
- refer & win detail card
- swipe-to-redeem track
- rewards milestone journey
- lifetime booster card

States:

- large shimmer screen on initial load
- pull-to-refresh
- refresh on nav resume

Important logic:

- milestone and feature-flag payloads both affect UI
- header switches when total earned > 0
- milestone rows use cumulative threshold math
- rewards home is not just a referral CTA page

### 5. Refer and Earn detail

Android route: `Route.ReferEarnDetail`

Screen type:

- pushed detail route from rewards home

Top structure:

- centered title top bar
- trailing history action

Body structure:

- win assured card
- booster active card
- earnings calculator
- referral code section with copy action
- bottom CTA row:
  - Share Invite
  - My QR

Important logic:

- deep links to:
  - buy gold
  - weekly SIP setup
- QR dialog generation
- referral sharing helper
- backend-backed detail payload with fallback defaults

### 6. Rewards history

Android route: `Route.RewardsHistory`

Screen type:

- pushed list route

Top structure:

- centered title top bar

Body states:

- loading spinner
- error state
- success state
- empty text

Success structure:

- helper intro text
- list rows with icon, amount, source chip, expiry label

Important logic:

- no pagination in current Android code
- refresh preserves previous list
- titles are derived from kind/source mapping
- expiry labels only apply to credit flows

### 7. Rewards redeem

Android route: `Route.RewardsRedeem`

Screen type:

- pushed payment flow route

Important ownership decision:

- do not build this as a separate payment system
- treat it as a rewards-routed buy-gold redemption variant
- reuse shared Trade buy foundations and payment boundary where possible

Important logic:

- live price
- amount input
- redeemable cap
- GST-inclusive math
- embedded/external Juspay launch
- order confirmation
- success state

Audit implication:

- Phase 10 should not try to reinvent payment plumbing already built in shared Trade

### 8. Referral status

Android route: `Route.ReferralStatus`

Current Android truth:

- custom top bar
- custom segmented tabs for active/completed
- dummy local data

Decision:

- do not treat as backend-backed source of truth
- keep it as a product decision item:
  - preserve
  - redesign
  - remove

### 9. Referral history

Android route: `Route.ReferralHistory`

Current Android truth:

- dummy local state
- simulated referral increments
- hardcoded referral code
- weak route entry relationship

Decision:

- treat as redesign candidate, not faithful migration target

## API Inventory

Backend-backed APIs already present in Android:

- `GET trade/transactions?page&limit`
- `GET trade/orders/{orderId}`
- `GET rewards/milestones`
- `GET rewards/history`
- `GET rewards/refer-details`
- `POST user/referral`

Existing shared feature reuse opportunities:

- `trade/transactions` should be reused from shared Trade data/domain work
- `trade/orders/{orderId}` should reuse shared Trade status logic
- `user/referral` already exists in shared auth/referral submission flow

Local persistence / platform-backed behavior:

- alerts list is persisted locally
- notifications are appended by platform push service
- install referrer and deep-link capture are platform-owned

## Validation And State Inventory

### History

- filter state:
  - type filter chips
  - status bottom-sheet filter
- paginated success state
- refresh-with-previous-data state
- error with retry

### Transaction details

- seeded route-display state
- invoice loading state
- invoice error state
- different invoice eligibility rules for trade vs delivery

### Rewards home

- initial shimmer loading
- pull-to-refresh
- resume refresh
- feature-flag dependent subcontent
- pre/post journey header state

### Rewards history

- loading
- success
- success while refreshing
- empty
- error

### Refer detail

- backend-backed detail state with fallback defaults
- QR dialog state
- share / copy side effects

### Alerts

- empty
- list
- read-on-open side effect

### Referral status / history

- current Android implementations are local/dummy states only
- product validation required before shared migration

## Asset And String Ledger

Parity-critical assets / presentation patterns:

- rewards top bar treatment
- rewards shimmer placeholders
- rewards pre/post journey header art and gradients
- swipe-to-redeem track
- milestone journey cards and icons
- alert card icon and unread dot styling
- history row status pills
- refer detail QR dialog visuals

Parity-critical strings / copy behavior:

- transaction filter labels
- transaction empty-state copy by filter type
- rewards helper copy and expiry labels
- refer detail booster extension wording
- alerts empty-state copy
- any referral status/history copy should be treated as unstable until product confirms those screens

## Must Match Android Exactly

These must match Android closely in the first implementation pass:

- History toolbar, chip row, shimmer, status filter sheet, and row structure
- Alerts card layout and read-on-open behavior
- Rewards home header switching, shimmer, milestone journey, and swipe-redeem entry
- Refer detail section order and CTA layout
- Rewards history row structure and mapping rules

These can safely reuse shared foundations under the hood:

- transaction fetching and status use cases from Trade
- rewards payment launch plumbing from Trade
- referral submission backend path from Auth
- alert persistence contract from core storage

These must not be blindly ported as-is:

- `ReferralStatusScreen`
- `ReferralHistoryScreen`

## Implementation Order

Recommended Phase 10 order:

1. `feature/history`
   - transactions list
   - transaction details handoff
   - shared route ownership from bottom tab and Home
2. `feature/alerts`
   - alerts route
   - local persistence contract
   - Home handoff replacement
3. `feature/rewards`
   - rewards home
   - rewards history
   - refer detail
4. `RewardsRedeem`
   - integrate as rewards-routed trade checkout variant
5. `feature/referral`
   - shared referral attribution contract
   - decide preserve/redesign/remove for status/history before implementation

## Blockers And Decisions Still Open

- whether `ReferralStatus` / `ReferralHistory` should be preserved, redesigned, or removed
- whether transaction details should stay as a Trade-owned shared route reused by History, or be moved into a dedicated shared transaction-details surface
- whether rewards redeem should ship in the first Phase 10 checkpoint or remain a deferred subflow after rewards home/history/detail are stable

## Current Audit Verdict

Phase 10 is ready to start only if we follow this order:

1. history
2. alerts
3. rewards home/history/detail
4. rewards redeem
5. referral decision work

Do not start by building a generic `Rewards` tab placeholder replacement without first resolving:

- history ownership
- alerts persistence
- rewards redeem reuse boundaries
- referral status/history product truth
