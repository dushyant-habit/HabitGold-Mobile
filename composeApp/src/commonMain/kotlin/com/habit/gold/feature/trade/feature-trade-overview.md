# Feature Trade Overview

## Status
- Phase: `Phase 7`
- Phase status: `checkpointed, not fully closed`
- Current status: shared Trade foundation, live price state, route ownership, and real end-to-end shared `WithdrawalMode`, `Buy`, and `Sell` flows are in place; Android and iOS Buy now launch real Juspay through the shared payment contract, Android-style post-payment polling after return is in place, Buy transitions through processing / success / failure / pending states, Sell flows through entry -> payout -> execution -> outcomes with invoice access, and the remaining Trade work is intentionally deferred into a later review pass for transaction-details / invoice-viewer parity plus VPA/help follow-up routes
- Source audit: [feature-trade-audit.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/trade/feature-trade-audit.md:1)
- Verification command: `./gradlew :composeApp:allTests :composeApp:compileKotlinIosSimulatorArm64 :composeApp:compilePreprodDebugKotlinAndroid`
- Verification result: `BUILD SUCCESSFUL`
- iOS native SDK verification: `xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build`

## Scope Of Current Slice
- create the shared `feature/trade` package structure from the approved audit
- add shared repository contracts, DTOs, endpoint wrappers, polling rules, payment-launch models, and initial presentation contracts
- connect Home buy/sell entry points to real Trade-owned routes instead of placeholder deferred handoffs
- add the first real Trade-owned screens and route ownership on top of the verified shared contracts
- add shared live buy/sell price ownership so Buy and Sell use Android-style rate ids and countdown behavior
- build real shared Buy and Sell flows on top of the verified shared backend contracts and platform payment bridges

## Included In This Slice
- audited package structure for:
  - buy
  - sell
  - withdrawal gateway
  - VPA payout handling
  - transaction details
  - invoice viewing
  - payment launch bridge
- shared Trade repository contract
- trade DTO set mirroring Android backend usage
- remote data source for:
  - `POST trade/buy`
  - `POST trade/sell`
  - `POST trade/sell/execute`
  - `GET trade/orders/{orderId}`
  - `GET trade/status/{orderId}`
  - `GET trade/orders/{orderId}/invoice`
  - `GET trade/transactions`
  - `GET portfolio/sell-availability`
  - `GET user/vpa`
  - `PATCH user/vpa/{id}/set-default`
  - `POST user/vpa/verify`
  - `GET promo/coupons/available`
  - `POST promo/coupons/validate`
- repository mapping and normalization rules
- KMP-safe idempotency key generation for order-creating calls
- shared polling policy and polling use case
- shared payment-launch request/result contract for native Juspay bridging
- shared live price store for:
  - `GET gold/price/buy`
  - `GET gold/price/sell`
  - buy/sell rate ids
  - countdown / refresh windows
- initial Buy and Sell MVI contracts
- real shared Trade route ownership from Home into:
  - `WithdrawalMode`
  - `Buy`
  - `Sell`
  - trade-owned transaction details
  - trade-owned invoice viewer
  - deferred route stubs for `Get Coin`, `VPA list`, and `Help Center`
- rebuilt shared `WithdrawalMode` screen
- corrected `WithdrawalMode` footer spacing and inset handling against the Android screen
- rebuilt shared `Buy` flow with:
  - rupees / grams mode switching
  - live price summary
  - quick amount shortcuts
  - totals summary
  - coupon apply / change / remove flow
  - real Juspay launch and post-payment polling
  - success / failure / pending / verifying states
  - invoice access
- rebuilt shared `Sell` flow slices for:
  - amount entry
  - balance information
  - payout UPI selection
  - success / failure / pending outcomes
  - invoice access
- corrected Sell footer surfaces back to the Android white bottom-bar treatment
- restored Sell success invoice affordance and dashboard reset behavior
- rebuilt shared trade-owned detail routes for:
  - transaction details
  - invoice viewer
- added a shared payment-launch dependency boundary
- added Android `PaymentActivity` + embedded Juspay bridge behind the shared launcher contract for Buy
- aligned Buy payment return handling with Android:
  - poll after Juspay success callbacks
  - poll after Juspay failure callbacks that still came back from the SDK
  - poll directly when the buy-order response has no SDK payload
  - move Buy through processing / success / failure / pending states instead of dropping back onto the form
- added the iOS Juspay payment bridge through Swift + HyperSDK while keeping shared payment state and callback mapping in Kotlin
- added iOS app-level Juspay configuration through xcconfig, `Info.plist`, CocoaPods, URL schemes, query schemes, and redirect forwarding
- added official Juspay HyperSDK Android Maven repository wiring plus plugin-managed Android SDK asset packaging
- Koin module wiring through shared app startup
- foundation tests:
  - `TradeRepositoryImplTest`
  - `PollTradeStatusUseCaseTest`
  - `BuyTradeMathTest`
  - `SellTradeMathTest`

## Folder Structure
```text
feature/trade/
  data/
    model/
    remote/
    repository/
  di/
  domain/
    model/
    usecase/
  presentation/
    buy/
    sell/
  feature-trade-audit.md
  feature-trade-overview.md
```

## Rules For This Feature
- do not start Trade UI from memory; keep using the audit as the source of truth
- do not describe a Trade screen as parity-complete unless it has been compared again against Android after implementation
- when working on `Buy`, follow the explicit Buy strict UI audit checklist and parity lessons in `feature-trade-audit.md` before closing the surface
- keep payment SDK launching outside shared code
- keep Android payment SDK launch in the platform layer even when shared Buy owns the order and polling state
- keep polling policies centralized instead of duplicating them in Buy and Sell flows
- for Buy payment return handling, follow Android exactly:
  - SDK success does not mean order success until backend status polling confirms it
  - SDK failure does not automatically mean order failure; if the SDK returned control, the backend order must still be polled
  - only pre-launch/configuration failures should remain on the form with an inline/shared error
- do not bypass `WithdrawalMode` when rebuilding Android trade parity
- keep transaction details and invoice ownership inside Trade until History lands and shares them

## Current Parity Boundary
- Trade foundation, route ownership, polling, and payment-launch boundaries are in good shape
- Android Buy payment launch and return handling are real platform-bound Juspay flows
- iOS Buy payment launch, redirect handling, and callback mapping are real platform-bound HyperSDK flows behind the same shared Trade payment contract
- `WithdrawalMode`, `Buy`, and `Sell` are now end-to-end working flows and have gone through repeated parity correction passes
- this feature should currently be treated as a verified checkpoint, not a fully closed Phase 7 sign-off
- the remaining Trade gaps are:
  - final transaction-details parity
  - final invoice-viewer parity
  - VPA-management parity
  - final shared help experience

## Deferred Review Backlog
1. finish strict Android visual parity for trade-owned transaction details
2. finish strict Android visual parity for the invoice viewer experience
3. rebuild VPA management parity
4. replace the temporary Trade help route with the final shared help experience
5. do one final Trade side-by-side QA sweep before calling Phase 7 complete
