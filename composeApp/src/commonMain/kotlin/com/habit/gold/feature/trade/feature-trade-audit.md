# Feature Trade Audit

## Status
- Audit status: approved structure, behavior, API, platform-boundary, and design source audit for starting Phase 7
- Migration status: shared foundation and real end-to-end shared Buy and Sell flows are in place on this branch, while final transaction-details / invoice-viewer parity and VPA/help follow-up work are still pending
- Goal: rebuild Trade as a close Android replica before wiring later feature destinations

## Audit Process Rule
Trade must be audited in multiple passes before implementation starts.

Minimum audit passes:
1. structure pass
   - entry routes
   - subroutes
   - shared vs platform ownership
   - hidden sheets, dialogs, and outcome screens
2. visual pass
   - tabs
   - cards
   - pricing bars
   - payout blocks
   - coupons
   - outcomes
3. interaction pass
   - amount and grams switching
   - refresh windows
   - swipe-to-pay behavior
   - pending and retry behavior
   - back handling
   - invoice access
4. API pass
   - endpoints
   - idempotency
   - polling windows
   - fallback endpoints
   - coupon rules
   - VPA verification and selection
5. platform pass
   - Juspay boundaries
   - payment result mapping
   - system UI stabilization after overlays
   - activity bridge fallback when embedded checkout is unavailable

Do not treat Trade audit as complete after only reading `BuyGoldScreen` and `SellGoldScreen`.
Trade also includes Withdrawal Mode, VPA payout selection, transaction details, invoice viewing, payment result handling, coupon validation, and shared status polling.

## Android Sources Audited
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/BuyGoldScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/SellGoldScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/WithdrawalModeScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/TransactionDetailsScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/InvoiceViewerScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/sellgold/SellGoldOutcomeScreens.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/sellgold/SellGoldUpiSelectionScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/vpa/VpaListScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/vpa/VpaListViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/buygold/BuyGoldViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/sellgold/SellGoldViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/repository/TradeRepositoryImpl.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/model/TradeDtos.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/remote/ApiService.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/domain/usecase/BuyGoldUseCase.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/domain/usecase/SellGoldUseCase.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/domain/usecase/GetOrderInvoiceUseCase.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/domain/usecase/GetAvailableCouponsUseCase.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/domain/usecase/ValidateCouponUseCase.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/domain/usecase/VerifyVpaUseCase.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/navigation/Routes.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/navigation/NavGraph.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/PaymentActivity.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/payments/juspay/EmbeddedJuspayCheckoutHost.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/payments/juspay/EmbeddedJuspayCheckoutCoordinator.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/payments/juspay/JuspayCheckoutHelper.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/payments/juspay/JuspayActivityForwarder.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/payments/juspay/JuspayPaymentState.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/payments/juspay/JuspaySdkPayloadMerge.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/util/SystemUiStability.kt`

## Main Conclusion
Trade is not one screen and not one API call.

It is a combined feature area made of:
- Buy one-time flow
- Buy SIP setup and SIP upgrade entry
- coupon auto-fetch and manual coupon validation
- shared payment launch contract with a native Juspay boundary
- shared post-payment status polling
- Sell create and execute flow
- Withdrawal Mode gateway before Sell vs Get Coin
- verified VPA selection and VPA management
- trade transaction details
- invoice viewing and saving
- sell outcome states: success, failure, pending

Phase 7 must start by matching this Android structure closely.
It should not start as a simplified `buy screen + sell screen` rewrite.

## Current Shared Implementation Gap Ledger

This section exists to keep the implementation honest while Phase 7 is underway.
If a screen is working but still visually approximate, it must stay listed here until it is re-checked against Android and corrected.

### Withdrawal Mode
- current shared screen is structurally close, but it must still be rechecked for:
  - exact top-bar density
  - exact bottom CTA surface treatment
  - exact card border and selected-state behavior
  - exact icon block and radio styling

### Buy
- current shared `Buy` screen is still a first-pass functional slice, not a strict Android replica
- the biggest current risk areas are:
  - top tab/header treatment
  - amount-entry block density
  - quick amount chip treatment
  - coupon card styling
  - latest-order card styling
  - bottom payment surface behavior

#### Buy strict UI audit checklist
The Buy screen must not be marked visually complete until all of these are checked against Android side by side.

- top bar density:
  - `statusBarsPadding()`
  - compact vertical rhythm
  - centered title
  - real help action, not a dead icon
- rotating fact strip:
  - text rotates automatically
  - no fake badge chrome around the whole strip
  - correct gold tone from Android `Gold600` family
  - same `AutoAwesome` icon size and text weight treatment
- segmented mode switch:
  - exact Android pill density
  - selected/unselected text treatment
  - exact corner radius and border hierarchy
- rupees and grams entry blocks:
  - amount label casing
  - placeholder weight and scale
  - plus/minus control size
  - slider thickness and thumb treatment
  - quick chip spacing and height
- coupon row:
  - field / apply / offers row layout
  - not a summary pill card
  - apply must disable correctly after the same coupon is applied
  - applied state must show `Change` and `Remove`
  - validation must work from current estimate values, not only from an already-created order
- coupon sheet:
  - title and subtitle must match Android structure
  - selected coupon card treatment must match Android
  - empty state and progress treatment must match Android
  - list-row spacing must be verified visually, not approximated
- SafeGold footer:
  - must use the real `safegold_image` asset
  - `POWERED BY` letter spacing and icon height must be checked
- bottom action surface:
  - live price strip must include animated progress fill
  - live wave indicator must be present
  - `Updates in:` timer must be split and styled like Android
  - one-time CTA copy must be Android-authentic: `Save Now`
  - bottom bar and navigation-inset spacing must be checked on device

#### Buy parity lessons learned
These are concrete misses from the first shared Buy passes and must be treated as explicit review gates next time.

- do not treat the fact strip as a decorative chip; in Android it behaves like a centered rotating message row
- do not invent coupon summary surfaces; Buy coupon UI is a real inline input + apply + offers pattern with a separate sheet
- do not assume the CTA label from the business meaning; Android one-time Buy uses `Save Now`
- do not reduce the live price bar to static text; it has animated progress and a wave indicator
- do not replace SafeGold branding with text-only fallback when the Android screen uses a real asset
- do not mark Buy parity complete without checking top spacing and bottom spacing on device, because these were easy to miss from code inspection alone

#### Buy polling and payment-result audit checklist
The post-payment surfaces must be audited as separate Android screens, not as one reusable "status template".

- polling wait state:
  - no top app bar
  - no close or back affordance
  - full-screen white surface
  - dual progress ring with centered seconds counter
  - centered order-id card near the lower half
  - title/body copy must match Android purchase-processing wording
- payment verifying fallback:
  - centered card/dialog-style layout
  - large circular progress
  - secure footer row with lock/check indicators
  - not the same layout as the order-id polling screen
- success state:
  - nested green circles with inner check icon
  - transaction-details card with completed chip
  - separate rows for `Gold Credited` and `Amount Paid`
  - invoice row below the card
  - single primary CTA at the bottom
- failure state:
  - nested red circles with close icon
  - centered error-code card
  - filled retry button plus outlined dashboard button
- pending state:
  - must not reuse generic error/success chrome
  - order/status card should still follow the Android card hierarchy if this state is shown

#### Buy polling and payment-result lessons learned
- do not collapse processing, verifying, success, failure, and pending into one generic outcome composable
- do not leave the polling screen navigable with a back button when Android intentionally treats it as a locked processing state
- do not reuse the main Buy screen app bar on post-payment surfaces
- do not skip the transaction-details card structure on success just because the data already exists in state
- do not sign off polling UI from logic parity alone; the payment-return surfaces need their own visual audit pass

### Sell Entry
- current shared `Sell` entry is functionally usable, but still visually simplified versus Android
- the biggest current risk areas are:
  - top section density
  - toggle treatment
  - amount field visuals
  - sellable/locked balance messaging layout
  - bottom live-price surface

### Sell Payout
- current shared payout step must be checked again for:
  - UPI row styling
  - selected-state treatment
  - summary block hierarchy
  - bottom CTA spacing and density

### Sell Outcomes
- success, failure, and pending are functional, but they still need exact Android parity review for:
  - icon scale
  - spacing rhythm
  - title/body weights
  - footer CTA layout

### Transaction Details
- shared route ownership is in place, but the details screen is still a first-pass shared implementation
- it must still be matched against Android for:
  - section order
  - row hierarchy
  - invoice CTA treatment
  - status styling

### Invoice Viewer
- current shared route is functional, but it is not yet Android-parity UI
- it still needs:
  - true viewer-state parity decision
  - loading/error/open affordance review
  - Android comparison for action placement

## Trade Ownership Boundary

### Shared KMP ownership
- Buy and Sell route state
- amount and grams conversion rules
- coupon fetch and validate rules
- payout amount and sell availability mapping
- order creation and execute requests
- shared polling engine or shared polling component
- transaction details and invoice route ownership
- post-payment verification logic
- error mapping

### Platform ownership
- Juspay SDK initialization and launch
- embedded checkout host vs standalone activity fallback
- payment result callback bridge
- system UI stabilization after payment overlay
- preferred UPI app package selection if preserved
- iOS HyperSDK host ownership, redirect forwarding, and CocoaPods workspace integration
- iOS URL scheme and `LSApplicationQueriesSchemes` ownership when payment SDK post-install automation is incomplete or unstable

Migration rule:
- `commonMain` should own the trade state machine and backend contract
- `androidMain` and `iosMain` should own SDK launching and result callback glue
- CocoaPods-backed iOS SDK integrations must be verified from the generated `.xcworkspace`, not only the raw `.xcodeproj`
- if a payment SDK mutates plist or URL-scheme state through post-install scripts, keep critical callback schemes and query schemes explicit in source control so the app still builds and redirects correctly when that automation drifts

## Route And Flow Inventory

### Trade entry routes
- `Route.BuyGold`
- `Route.SellGold`
- `Route.WithdrawalMode`
- `Route.TransactionDetails`
- `Route.InvoiceViewer`
- `Route.VpaList`

### Trade-adjacent routes entered from Trade
- `Route.GoldSavingDaily`
- `Route.GoldSavingWeekly`
- `Route.GoldSavingMonthly`
- `Route.GetCoinCatalog`

### Primary entry points
- Home buy CTA -> `Route.BuyGold`
- Home sell CTA -> `Route.WithdrawalMode`
- Gold value details -> buy or withdrawal mode
- Home recent activity -> `Route.TransactionDetails`
- Sell payout flow -> `Route.VpaList`

### Structural rule
- Sell is not entered directly from Home in Android parity
- Home enters `WithdrawalMode`, then user chooses `Get Coin` or `Cash`
- `Cash` leads into `SellGold`

## Buy Flow Audit

### Main responsibilities
- one-time purchase in rupees mode
- one-time purchase in grams mode
- live buy price and refresh windows
- coupon preview, coupon selection, coupon validation
- rewards usage in rupees
- invoice access after completion
- SIP setup and SIP upgrade entry from the same screen

### Buy UI state ledger
- `Idle`
- `Loading`
- `CheckoutReady`
- `SipCheckoutReady`
- `Polling`
- `Success`
- `Processing`
- `Error`

### Buy interaction rules
- screen supports switching between rupees and grams
- request body may send either amount or grams
- live rate expiry can invalidate a request and force price refresh
- a successful order can still require payment launch plus later polling
- post-payment non-terminal states are not all shown as explicit `Polling` UI
- coupon validation in pre-payment Buy must use the current amount/grams estimate, not depend on a previously-created order object

### Buy polling rules
- polling use case: `GetTransactionStatusUseCase`
- interval: `5000ms`
- max attempts: `6`
- success statuses:
  - `COMPLETED`
  - `SUCCESS`
- failure statuses:
  - `FAILED`
- non-terminal statuses:
  - `PENDING`
  - `PAYMENT_RECEIVED`
  - `GOLD_BUY_FAILED`
  - `PAYOUT_PROCESSING`
- after polling timeout:
  - move to `Processing`

### Buy payment return rules learned from Android
- after Juspay returns to Buy, Android does not trust the SDK callback alone to decide final success or failure
- if the SDK callback is `success`, Android immediately enters post-payment processing and polls backend order status
- if the SDK callback is `failure`, Android still immediately enters post-payment processing and polls backend order status
- only `backpressed` returns the user directly to the Buy entry state without polling
- if the buy-order response has no SDK payload, Android still enters post-payment processing and polls order status directly
- the shared KMP launcher must therefore distinguish:
  - SDK-return failures that still require backend polling
  - pre-launch/configuration failures that should stay on the Buy form with an error
- Buy must not remain on the same entry surface after a real SDK return unless the user explicitly cancelled

### SIP setup and upgrade from Buy
- Buy screen also creates SIP mandate sessions
- setup uses `sip/mandate/session`
- upgrade uses `sip/mandates/{id}/update-session`
- mandate polling:
  - interval `5000ms`
  - max attempts `6`
  - success `ACTIVE`
  - failure `FAILED_REGISTRATION`
  - timeout fallback `Processing`

Migration rule:
- Buy cannot be audited or implemented as one-time purchase only
- SIP tab and coupon logic are part of the same Android feature surface

## Sell Flow Audit

### Main responsibilities
- sell in rupees mode
- sell in grams mode
- live sell price and refresh window
- sell availability and locked-balance messaging
- create sell order
- execute sell order against a verified VPA
- short poll for completion
- pending outcome fallback
- invoice access on success

### Sell UI state ledger
- `Idle`
- `InitiatingSell`
- `ExecutingSell`
- `WaitingForTransaction`
- `Success`
- `Failed`
- `PendingOrder`

### Sell availability rules
- primary source: `portfolio/sell-availability`
- fallback: `portfolio`
- last fallback: derive from history
- UI must distinguish:
  - total balance
  - sellable balance
  - locked balance
- sell can be blocked even when the user owns gold

### Sell polling rules
- polling use case: `GetTransactionStatusUseCase`
- interval: `5000ms`
- max attempts: `2`
- success statuses:
  - `COMPLETED`
  - `SUCCESS`
- failure statuses:
  - `FAILED`
- timeout fallback:
  - move to `PendingOrder`
  - user later checks History

### Sell outcome surfaces
- `SellGoldSuccessScreen`
- `SellGoldFailureScreen`
- `SellGoldPendingScreen`

Migration rule:
- pending outcome is not an error state
- it is an intentional product state after a short polling window

## Withdrawal Mode Audit
- route: `Route.WithdrawalMode`
- default selected mode in Android: `CASH`
- options:
  - `Get Coin`
  - `Cash`
- proceed CTA routes:
  - `COIN` -> `GetCoinCatalog`
  - `CASH` -> `SellGold`

Migration rule:
- treat Withdrawal Mode as part of the trade entry structure
- do not bypass it when matching Android product flow

## VPA And Payout Audit

### Core sell payout screen
- `SellGoldUpiSelectionScreen`
- owns:
  - payout summary
  - selected VPA state
  - swipe-to-sell CTA
  - live sell price bar
  - confirmation dialog

### VPA data rules
- fetch from `GET user/vpa`
- choose default or first verified VPA as fallback
- `trade/sell/execute` requires `vpaId`
- VPA verify and add logic exists in `VpaListViewModel`
- new UPI IDs are explained as being added automatically after a UPI payment

### VPA management routes
- `Route.VpaList`
- supports:
  - list current UPI IDs
  - set default
  - verify and add VPA

Migration rule:
- VPA handling is a real part of Sell parity
- Sell is incomplete if we only port the create-order screen

## Transaction Details And Invoice Audit

### Transaction details ownership
- route: `Route.TransactionDetails`
- used by trade and later by history and delivery
- displays:
  - transaction ID
  - type
  - status
  - amount
  - gold quantity
  - SIP metadata when applicable

### Invoice rules
- trade invoices use `GET trade/orders/{orderId}/invoice`
- delivery orders have a separate invoice path and fallback logic
- `InvoiceViewerScreen`:
  - downloads the PDF
  - renders it page by page
  - supports save/export

Migration rule:
- `TransactionDetails` should be planned as shared trade-history infrastructure
- invoice access is part of Trade parity, not optional polish

## Coupon Audit

### Coupon ownership
- fetch available coupons through `GetAvailableCouponsUseCase`
- validate through `ValidateCouponUseCase`
- order type matters
- coupon behavior appears in:
  - Buy one-time flow
  - SIP setup and upgrade paths
  - Home coupon handoff references

### Coupon parity rules
- auto-fetch available coupons for the active order mode
- manual code entry also exists
- validation must feed visible pricing and benefits
- do not implement coupons as a single text field without the availability sheet behavior

## API Ledger

### Buy
- `POST trade/buy`
  - requires `Idempotency-Key`
  - body:
    - `amount`
    - or `grams`
    - `buyRateId`
    - `couponCode`
    - `useRewardsInr`

### Sell
- `POST trade/sell`
  - requires `Idempotency-Key`
  - body:
    - `grams`
    - `sellRateId`
- `POST trade/sell/execute`
  - body:
    - `orderId`
    - `vpaId`

### Status and invoice
- `GET trade/orders/{orderId}`
- legacy fallback: `GET trade/status/{orderId}`
- `GET trade/orders/{orderId}/invoice`
- `GET trade/transactions?page={page}&limit={limit}`

### Sell availability and VPA
- `GET portfolio/sell-availability`
- `GET user/vpa`
- `POST user/vpa/verify`
- `PATCH user/vpa/{id}/set-default`

### Coupon
- `GET promo/coupons/available`
- `POST promo/coupons/validate`

### Shared API rules
- buy and sell create calls use generated idempotency keys
- amount normalization:
  - money -> `2` decimals
  - grams -> `4` decimals
- if API error code is `GOLD_RATE_EXPIRED`:
  - refresh live prices
- error parsing must extract:
  - message array first entry
  - nested message object code
  - fallback `error` string

## DTO Ledger
- `BuyGoldRequestDto`
- `BuyGoldResponseDto`
- `SellGoldRequestDto`
- `SellGoldResponseDto`
- `TransactionStatusResponseDto`
- `OrderInvoiceResponseDto`
- `TransactionDto`
- `TransactionsResponseDto`
- `ExecuteSellRequestDto`
- `ExecuteSellResponseDto`

Important response details:
- buy response can include:
  - `paymentProvider`
  - `paymentProviderOrderId`
  - `priceLockId`
  - `priceLockExpiresAt`
  - `sdkPayload`
- sell response can include:
  - `transactionId`
  - derived payout amount if `amount` is blank

## Design System Ledger For Trade

These are the shared Android trade UI primitives that must be matched before rebuilding Buy and Sell screens.

### TradeInfoPillCard
- not a pill-shaped chip despite the name
- full-width centered row
- no enclosing card chrome by default
- used for animated fact and trust statements
- content usually combines:
  - `AutoAwesome` icon at `16dp`
  - `8dp` gap
  - `11sp` to `12sp` semi-bold text
  - gold-tinted color treatment

### TradeSegmentedControlPager
- outer background: `Slate50`
- outer border: `1dp Slate100`
- outer corner radius: configurable, often `40dp`
- outer inner padding: `3dp`
- selected segment:
  - white background
  - `1dp Slate200` border
  - bold text
- unselected segment:
  - transparent background
  - semi-bold text
- default font sizes:
  - `11sp` to `12sp`
- used for:
  - buy one-time grams/rupees switch
  - buy SIP frequency switch
  - sell grams/rupees switch

### LivePreviewPriceBar
- full-width bottom bar
- background: `Purple30`
- animated horizontal progress fill while price is valid
- horizontal padding: `16dp`
- vertical padding: `6dp`
- left cluster:
  - `LiveWaveIndicator`
  - label text `11sp`
  - value text `11sp`, extra bold
  - GST hint text `9sp`
- right cluster:
  - `Updates in:`
  - countdown timer
- refreshing state replaces normal content with a centered “updating price” label

### PoweredBySafeGoldRow
- centered row
- left text:
  - `POWERED BY`
  - `9sp`
  - bold
  - letter spacing `1.2sp`
- `8dp` gap
- SafeGold image at `10dp` height

### SwipeToPayButton
- used for sell payout confirmation
- premium mode height: `64dp`
- outer shape: `RoundedCornerShape(16dp)`
- premium background: `HabitGoldPrimary`
- handle:
  - width about `56dp`
  - rounded rectangle `12dp`
  - translucent white background
- text:
  - `14sp`
  - bold
  - white
  - slight letter spacing
- premium sell text:
  - `SWIPE TO SELL GOLD`

## Buy Design Audit

### Buy root structure
- top app row, not a stock Material `TopAppBar`
- white background
- `statusBarsPadding()`
- `16dp` horizontal padding
- `12dp` vertical padding
- center title
- left back button
- right help button tinted with theme purple

### Buy bottom action area
- white surface
- includes `imePadding()`
- horizontal divider
- `LivePreviewPriceBar`
- optional inline red error card
- then CTA row

#### One-time buy CTA row
- horizontal padding: `20dp`
- vertical padding: `12dp`
- split row:
  - amount breakdown button
  - primary pay CTA
- amount breakdown button:
  - height `56dp`
  - white background
  - `1dp Slate200` border
  - `12dp` corners
  - title amount `14sp` extra bold
  - sublabel `9sp` violet with up-arrow icon
- pay CTA:
  - height `56dp`
  - weight larger than breakdown button
  - `12dp` corners
  - text `17sp` black weight
  - loading state uses `BouncingLoadingDots`

#### SIP CTA row
- single full-width primary button
- same `56dp` height
- same `12dp` radius
- label varies by frequency:
  - daily
  - weekly
  - monthly

### Buy content order
1. `TradeInfoPillCard`
2. active mode controls
3. main amount input
4. conversion helper or slider
5. quick amount chips
6. coupon section
7. SafeGold row
8. for SIP:
   - frequency selector
   - amount control
   - coupon section
   - growth projection card
   - SafeGold row

### Buy one-time amount mode visual ledger
- segmented control with `40dp` rounding
- uppercase helper label:
  - `11sp`
  - bold
  - letter spacing `2sp`
- rupees entry:
  - full-width box
  - background `Slate50`
  - corners `12dp`
  - padding `12dp` vertical and `16dp` horizontal
  - rupee symbol `42sp`
  - amount text `42sp` black weight
- grams entry:
  - plus/minus circular controls
  - center box `Slate50`, `12dp` corners
  - amount text `42sp`
  - trailing `gm` label `20sp`

### Buy one-time affordances
- rupees mode shows conversion sentence in gold color
- grams mode shows:
  - slider
  - purple thumb
  - `0.1 gm` to max labels at `10sp`
- quick chips:
  - centered row
  - width about `74dp`
  - height `34dp`
  - white background
  - `1dp Slate200` border
  - rounded `12dp`

### Buy SIP visual ledger
- segmented control options:
  - `Daily`
  - `Weekly`
  - `Monthly`
- frequency header:
  - `11sp`
  - bold
  - letter spacing `2sp`
  - purple selected value text
- amount selector:
  - circular plus/minus buttons at `48dp`
  - center amount container in `Slate50`
  - rupee symbol and amount at `48sp`
- slider:
  - track `8dp`
  - thumb `28dp`
- min/max labels:
  - `10sp`
  - bold
- projection card:
  - bordered, rounded rectangle
  - icon circle with trend glyph
  - next due text
  - purple-highlighted estimate line
  - info icon opens compounding bottom sheet

### Buy sheets and dialogs

#### Compounding bottom sheet
- white sheet
- drag handle `Slate200`
- max height about `82%` of screen
- summary card:
  - `Slate50`
  - `1dp Slate100`
  - `12dp` corners
- yearly timeline list with numbered circles
- explanatory note in italic small text

#### Breakdown bottom sheet
- title `Breakdown`
- body card:
  - `Slate50`
  - `1dp Slate100`
  - `16dp` corners
- rows for:
  - gold value
  - GST
  - quantity
  - coupon if applied
  - amount to be paid
- bottom pay button:
  - `54dp` height
  - `12dp` corners

#### Weekly and monthly selector sheets
- weekly:
  - list rows
  - row padding about `12dp` horizontal, `10dp` vertical
  - radio button on trailing side
- monthly:
  - headline with purple selected day
  - date circles in 7-column grid
  - explanatory note
  - full-width confirm button `52dp` high

#### Coupon sheet
- height about `52%` of screen
- title `Available Coupons`
- subtitle `Select one to validate and apply.`
- loading uses centered circular progress
- empty state text centered
- coupon rows:
  - rounded `12dp`
  - white background
  - selected row gets light purple tint
  - border `1dp Slate100` or purple-tinted when selected
  - trailing state:
    - spinner
    - check icon
    - `Locked`
    - or purple pill `Apply`

### Buy coupon inline area
- white container
- `12dp` corners
- `1dp Slate100` border
- internal padding `10dp` horizontal, `8dp` vertical
- draft state:
  - outlined text field look using `BasicTextField`
  - apply button as purple pill
  - offers text button with down arrow
- applied state:
  - green check
  - code and benefit text
  - change and remove text buttons

## Sell Design Audit

### Sell root structure
- same custom top row pattern as Buy
- same white background
- same `16dp` horizontal, `12dp` vertical, `statusBarsPadding()`

### Sell bottom action area
- white surface
- divider
- `LivePreviewPriceBar`
- divider
- full-width bottom CTA area with:
  - `24dp` horizontal padding
  - `16dp` top
  - `24dp` bottom
- CTA button:
  - height `56dp`
  - `16dp` corners
  - text `16sp` bold
  - disabled state changes text to `Gold Under Holding Period`

### Sell content order
1. `TradeInfoPillCard`
2. balance summary row
3. grams/rupees segmented control
4. amount entry
5. optional slider
6. quick chips
7. optional net-credit banner for grams mode
8. safety layer
9. error text if present

### Sell balance summary ledger
- redeemable and total values shown in one bordered row
- container:
  - `Slate50`
  - `1dp Slate200`
  - `10dp` corners
- trailing info button opens balance sheet
- text sizes:
  - labels `10sp`
  - values `12sp` bold

### Sell amount entry ledger
- rupees mode:
  - same large container style as Buy
  - rupee symbol `48sp`
  - amount `48sp`
- grams mode:
  - smaller circular plus/minus buttons than Buy
  - `24dp` control size
  - amount text `42sp`
  - trailing `gm` label `20sp`
- grams slider:
  - track `6dp`
  - thumb `28dp`
  - labels `0 gm` and full balance in `10sp`
- quick chips:
  - `10%`, `25%`, `50%`, `Full`
  - width about `78dp`
  - height `34dp`

### Sell special visual states
- grams mode can show net credit banner:
  - `6dp` corners
  - `1dp HabitGoldPrimary` border
  - `Slate50` background
- safety layer:
  - centered partner row
  - SafeGold image
  - trust badge row
- inline error text:
  - centered
  - red
  - `13sp`

### Sell balance information bottom sheet
- custom drag handle bar
- title `Balance Information`
- three detail items:
  - total balance
  - redeemable gold
  - locked gold
- each item:
  - icon circle
  - grams
  - value
  - explanatory description
- if locked gold exists:
  - amber notice card with schedule icon and next release date
- bottom CTA:
  - `Got it`
  - full-width
  - `50dp` height
  - `12dp` corners

## Sell Payout VPA Screen Design Audit

### Top section
- white header column
- `statusBarsPadding()`
- compact back row with centered title
- below that, payout summary card:
  - full width
  - purple-tinted background
  - `18dp` corners
  - `16dp` horizontal, `14dp` vertical padding
- headline:
  - `You receive`
  - large amount `28sp`

### Bottom section
- divider
- `LivePreviewPriceBar`
- divider
- `SwipeToPayButton` premium style inside white padded container

### VPA list area
- page content scrolls
- `Select payout UPI` heading at `14sp` semi-bold
- each VPA tile:
  - `18dp` corners
  - `Purple50` when selected or `Slate50` otherwise
  - border `1dp Purple200` or `Slate200`
  - horizontal padding `16dp`
  - vertical padding `14dp`
- selected radio uses `Check` icon
- footnote about payout destination shown below list

### Confirmation dialog
- custom `Dialog`, not stock alert only
- confirms amount and selected UPI before final sell

## Withdrawal Mode Design Audit
- simple white page scaffold
- stock `TopAppBar` title
- bottom purple-toned area behind CTA
- footer does not add extra navigation-bar spacer beyond the Android screen rhythm
- CTA uses:
  - `24dp` horizontal padding
  - `24dp` vertical padding
  - `56dp` height
- two selection cards:
  - white background
  - `12dp` corners
  - selected border `2dp HabitGoldPrimary`
  - unselected border `1dp Neutral200`
  - selected elevation `4dp`
- icon lane:
  - `56dp` rounded square background
  - coin option uses gold/orange tint
  - cash option uses green tint
- trailing custom selected circle with check mark

## Transaction Details Design Audit
- stock `TopAppBar`
- main content card:
  - `Neutral25` background
  - internal padding `16dp`
- heading row can include SIP badge
- date under title
- horizontal divider with large spacing before details
- detail rows:
  - fixed label width `120dp`
  - `16dp` gap
  - value right-aligned
- invoice CTA:
  - full-width purple button
  - `52dp` height
  - icon + text row when not loading

### Status coloring
- green for success or credit
- orange for pending or processing
- red for fail or cancel

## Invoice Viewer Design Audit
- stock `TopAppBar`
- white screen
- floating download action button
- states:
  - full-screen centered loading spinner
  - centered error text with retry button
  - PDF viewer
- PDF viewer:
  - white background
  - vertical lazy list of rendered pages
  - pinch to zoom up to `4x`
  - panning constrained to viewport

## Sell Outcome Screens Design Audit

### Shared pattern
- full-screen white column
- centered content
- large icon circle at top
- summary card in middle
- no extra app-bar shell above the result surface
- bottom full-width `Go to dashboard` button
- back button intercepted to return to dashboard

### Success screen
- outer success circle `100dp`
- inner green circle `60dp`
- title `24sp`
- purple summary card `16dp` corners
- invoice link row with purple description icon
- invoice row sits below the summary card, not inside the dashboard button area
- dashboard button uses the flatter Android Trade button treatment instead of a shared elevated CTA

### Failure screen
- same layout
- red icon treatment
- helper card describing next action
- dashboard button stays `50dp` high like Android

### Pending screen
- purple schedule icon treatment
- message explains status not confirmed within polling window
- order ID card shown in the middle
- dashboard button stays `50dp` high like Android

## VPA Management Screen Design Audit

### Scaffold
- white page
- centered top app bar
- title in purple
- optional FAB exists in code path but disabled by flags

### Loading state
- full-page shimmer list
- `3` placeholder cards
- each shimmer card mirrors the real VPA card layout

### Success state
- list padding:
  - horizontal `20dp`
  - top `20dp`
  - bottom `124dp`
- VPA item card:
  - `16dp` corners
  - `Slate50Alt` background
  - `1dp Slate200Alt` border
  - `16dp` internal padding
- verified state shows green check icon
- default state shows green pill
- bottom informational note card anchored near footer

### Add VPA dialog
- stock `AlertDialog`
- white container
- `24dp` corners
- outlined text field with purple focus
- inline loading / success / error status row under input

## Asset And Icon Ledger
- SafeGold brand image:
  - `R.drawable.safegold_image`
- repeated shared icons:
  - back
  - help
  - auto awesome
  - info
  - schedule
  - check / check circle
  - warning
  - description
  - lock
  - workspace premium
- Android Trade currently relies mostly on Material icons plus the SafeGold raster asset rather than a large trade-specific drawable set

## Design Parity Non-Negotiables Before Coding
- use the custom trade top rows, not generic large app bars
- preserve the shared trade component family:
  - info pill row
  - segmented control
  - live preview price bar
  - SafeGold footer row
- preserve separate CTA patterns:
  - Buy uses standard buttons
  - Sell payout uses premium swipe CTA
- preserve sell pending outcome as a distinct full-screen design
- preserve the balance-info sheet and coupon sheet as real surfaces, not inline substitutes

## Juspay And Payment Boundary Audit

### Android payment model
- Android can launch payment through:
  - embedded host: `EmbeddedJuspayCheckoutHost`
  - fallback activity: `PaymentActivity`
- payment result types:
  - `success`
  - `failure`
  - `backpressed`
- Android may merge preferred UPI package into `sdkPayload`
- Android stabilizes system bars after Juspay overlay returns

### Shared migration rule
- shared Trade code should emit a typed `PaymentLaunchRequest`
- platform layer should convert that to Juspay SDK invocation
- shared Trade code should consume a typed `PaymentLaunchResult`
- shared Trade code should then decide:
  - poll order
  - show failure
  - show processing
  - resume idle

### Non-negotiable audit checklist for Juspay
- verify where `sdkPayload` comes from
- verify when embedded host is used vs activity fallback
- verify how back-pressed is mapped
- verify how failure message is surfaced
- verify system bars are restored after overlay dismissal
- verify payment launcher is reusable by Trade, SIP, Rewards, and Delivery later

## Visual And Interaction Hotspots To Recheck Before Coding
- Buy top area density and live-price treatment
- rupees vs grams mode switching
- bottom price bar layout
- swipe CTA behavior and loading lockouts
- coupon sheet layout and benefit summary
- sell locked-gold messaging
- sell payout summary card
- VPA selection row visuals
- sell success / failure / pending visual hierarchy
- transaction-details status coloring
- invoice loading / retry / save behavior

## Cleanup Risks To Avoid
- building Buy without SIP tab awareness
- building Sell without Withdrawal Mode
- treating pending sell as a failure
- pushing Juspay directly into shared code
- forgetting trade invoice rules
- forgetting legacy `trade/status/{orderId}` fallback
- rebuilding transaction details twice later for History

## Phase 7 Implementation Order
1. create `feature/trade` audit-backed package structure
2. port trade DTOs and repository contract
3. port trade remote data source and repository implementation
4. create shared polling engine for buy and sell
5. define shared payment launcher contract
6. build Buy contract, state, and reducer
7. build Sell contract, state, and reducer
8. rebuild Withdrawal Mode gateway
9. rebuild Buy UI with one-time and SIP-aware structure
10. rebuild Sell UI, payout VPA selection, and sell outcomes
11. rebuild trade transaction details and invoice viewer pieces
12. add tests for:
  - status transitions
  - polling timeout paths
  - sell pending fallback
  - coupon validation mapping
  - payment result mapping

## Definition Of Audit Completion
Trade audit is complete only when:
- route inventory is explicit
- Buy and Sell state machines are explicit
- polling windows and terminal states are explicit
- coupon and SIP coupling is documented
- Juspay shared vs platform ownership is explicit
- invoice, transaction details, and VPA ownership are explicit
- implementation order is documented
