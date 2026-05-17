# KMP Pre-Migration Audit

This document captures the Android app behavior that is easy to miss when looking only at top-level routes.

Source app audited:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android`

Use this with:

- [KMP_MIGRATION_ROADMAP.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_MIGRATION_ROADMAP.md:1)
- [KMP_PROGRESS_TRACKER.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROGRESS_TRACKER.md:1)

## Home

Android references:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/HomeScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/home/HomeViewModel.kt`

What is really in this flow:

- portfolio dashboard with cached refresh behavior
- recent transactions preview with transaction-details drilldown
- Home-triggered buy and sell entry
- Home-triggered SIP setup, upgrade, and resume
- force-update check
- balance visibility toggle
- gold price/info bottom sheet and intro bottom sheet
- gold value details entry
- support/help and editorial entry points

Migration notes:

- `HomeViewModel` mixes dashboard, recent transactions, force update, buy checkout, SIP checkout, SIP polling, and coupon fetch.
- `HomeScreen` is a hub and should be split into small sections in KMP.
- Home owns product handoffs that must be preserved even if the UI is redesigned.

## Buy

Android references:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/BuyGoldScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/buygold/BuyGoldViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/repository/TradeRepositoryImpl.kt`

What is really in this flow:

- one-time buy in rupees or grams
- SIP setup from the second tab
- coupon auto-fetch and manual promo validation
- embedded or external Juspay payment launch
- order-status polling and processing fallback
- invoice access after success

Migration notes:

- Buy logic is duplicated between `BuyGoldViewModel` and `HomeViewModel`; KMP should consolidate this into shared trade and SIP state machines.
- Payment launch is Android-specific, but checkout preparation and status polling should be shared.
- Rewards redeem reuses the buy flow and should not create a second purchase pipeline.

## Sell

Android references:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/SellGoldScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/sellgold/SellGoldViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/sellgold/SellGoldUpiSelectionScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/sellgold/SellGoldOutcomeScreens.kt`

What is really in this flow:

- `WithdrawalMode` gateway before entering cash sell vs physical coin flow
- sell in rupees or grams
- sell-availability check with locked-gold messaging
- create sell order
- select verified payout UPI
- execute sell order
- short status-polling window
- success, failure, and pending outcomes
- invoice fetch on success

Migration notes:

- Sell is a two-step flow: create order first, execute after payout UPI selection.
- Pending sell is not terminal; it depends on History for later verification.
- VPA management is adjacent and should live under trade/payments, not profile.

## Delivery / Get Coin

Android references:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/GetCoinCatalogScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/GetCoinCartScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/GetCoinAddressScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/GetCoinOrderSummaryScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/AddNewAddressScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/GetCoinViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/delivery/DeliveryTrackingScreen.kt`

What is really in this flow:

- delivery catalog filtered by product-value ceiling
- shortfall dialog that deep-links to Buy Gold with required grams
- shared address book and saved-address selection
- add, edit, delete address
- delivery pincode validation
- address OTP verification and serviceability refresh
- quote creation and expiry refresh
- payment launch and backend verification polling
- pending checkout restore after app/process interruption
- order placed and delivery tracking

Migration notes:

- Delivery has the most stateful recovery logic in the app.
- `PendingDeliveryCheckoutStore` persistence must be planned early in KMP.
- Address editing becomes locked while a quote is active.
- `DeliveryTrackingViewModel` directly decodes `listDeliveryOrders()` into a raw list, while the repository returns raw `JsonElement`; this should be normalized before porting.

## SIP / Savings

Android references:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/GoldSavingSetupScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/UpiAutopayManageScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/UpiAutopayViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/data/repository/SipRepositoryImpl.kt`

What is really in this flow:

- daily, weekly, and monthly savings setup
- existing mandate upgrade flow
- weekly-day and monthly-date selectors
- coupon support
- payment/mandate registration launch
- mandate-status polling
- success and pending states
- separate autopay-management list with filter, pause, resume, and cancel

Migration notes:

- SIP appears in Home, Buy, and dedicated savings screens, so ownership must be clear in KMP.
- Autopay management is a SIP concern, even if the Android app launches it from Home and Profile.

## Refer & Earn / Rewards

Android references:

- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnDetailScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferEarnDetailViewModel.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/RewardsHistoryScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/RewardsRedeemScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferralHistoryScreen.kt`
- `/Users/dushyantmainwal/AndroidStudioProjects/HabitGold-Android/app/src/main/java/com/habit/gold/screens/ReferralStatusScreen.kt`

What is really in this flow:

- rewards home with pre-journey vs post-journey presentation
- milestone rewards timeline
- refer detail and booster extension prompts
- rewards history
- rewards redeem into gold
- referral share and QR
- referral status/history screens

Migration notes:

- Rewards and referrals are connected but not identical; keep `feature/rewards` and `feature/referral` separate.
- `RewardsRedeemScreen` reuses the buy checkout pipeline with `useRewardsInr`.
- `ReferralHistory` currently has a route but no obvious live entry point.
- `ReferralStatusScreen` appears to be powered by hardcoded dummy data and should be treated as a redesign candidate unless product confirms otherwise.

## Cross-Flow Dependencies That Must Not Be Missed

- `TransactionDetails` is shared by trade and delivery, but invoice rules differ by transaction type.
- `WithdrawalMode` is a separate decision screen and should stay explicit in migration planning.
- `History` is not optional because sell pending states rely on it.
- `AddressBook` is shared by delivery and profile entry points.
- `RewardsRedeem` depends on both rewards state and buy/trade checkout state.

## Recommended KMP Ownership

- `feature/home`: dashboard, summaries, Home-only entry states
- `feature/trade`: buy, sell, withdrawal mode, VPA management, trade transaction details
- `feature/sip`: savings setup, upgrade, mandate polling, autopay management
- `feature/delivery`: catalog, cart, address, quote, payment verification, tracking
- `feature/history`: transaction list and shared transaction details shell
- `feature/rewards`: milestones, rewards history, rewards redeem
- `feature/referral`: refer detail, referral share, referral status/history

## Known Redesign Candidates

- `HomeScreen.kt` because it is too large and owns too many concerns
- `BuyGoldScreen.kt` because one-time buy and SIP are fused into one huge screen
- `GetCoinViewModel.kt` because it mixes catalog, address book, checkout, tracking, parsing, preview fakes, and persistence recovery
- `ReferralStatusScreen.kt` because it appears dummy-data driven
- `ReferralHistoryScreen.kt` because it appears disconnected from active app navigation
