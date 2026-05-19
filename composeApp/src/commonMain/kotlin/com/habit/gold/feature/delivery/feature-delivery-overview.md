# Feature Delivery Overview

## Status

- Phase: `Phase 11`
- Current status: `shared delivery / get coin checkpoint is live; final device QA and any remaining invoice/detail parity follow-up are still pending`
- Source audit: [feature-delivery-audit.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/composeApp/src/commonMain/kotlin/com/habit/gold/feature/delivery/feature-delivery-audit.md:1)

## Scope

This feature boundary covers:

- Delivery catalog / Get Coin root
- Delivery cart
- saved address list
- add / edit / delete address
- address OTP verification
- pincode serviceability verification
- delivery quote creation
- payment launch and payment-result handling
- pending-checkout restore
- order summary
- delivery tracking

This feature also owns the shortfall recovery handoff into Buy Gold.

## Locked Decisions

- `feature/delivery` owns the delivery catalog through order-tracking flow
- `HomeDestination.Delivery` is the route owner for all shared delivery screens
- delivery shortfall should hand the user into shared Trade Buy with:
  - grams mode enabled
  - the required shortfall rounded up to the next `0.5g` step
- backing out of that Buy flow should return the user to Delivery / Get Coin, not Home
- delivery eligibility must use `redeemableGoldGrams`, not total portfolio balance
- address serviceability is valid only when verification status is `PINCODE_SERVICEABLE`
- Help entry should reuse the shared Profile Help surface rather than introducing delivery-specific help screens
- delivery routing should preserve the caller context when launched from `WithdrawalMode`

## Current Shared Slice

- `feature/delivery` data/domain/use-case foundation
- shared delivery payment-launch contract
- delivery catalog with product selection and shortfall handling
- cart review and quote-driven checkout
- address book, address CRUD, OTP verification, and serviceability refresh
- payment verification and order polling recovery
- pending checkout restore and catalog persistence
- order summary
- delivery tracking
- targeted delivery viewmodel / DTO tests

## Entry Points

- Home / withdrawal handoff into `Get Coin`
- direct delivery route ownership under `HomeDestination.Delivery`
- buy-back handoff from delivery shortfall dialog
- order summary to tracking handoff

## Cross-Feature Boundaries

- Delivery reuses shared Trade Buy for shortfall recovery only
- Delivery does not own generic transaction details; History / Trade continue to own those broader invoice/detail surfaces
- Delivery address and pincode logic are feature-owned, not shared with Profile
- Delivery payment launch is platform-backed through the shared delivery payment launcher contract

## Important Product Rules

- product affordability is checked against `redeemableGoldGrams`
- shortfall grams are rounded up to the next `0.5g` before navigating to Buy
- address edits must preserve `type` and `landmark`
- payment cancel / failure should not silently fall into success-style polling
- outside-tap keyboard dismissal should work on address entry surfaces
- checkout/footer spacing should follow the same dense-but-breathable rhythm as the Android withdrawal and checkout screens

## Pending Slice

1. final device QA on catalog, address, checkout, and tracking surfaces
2. confirm whether any delivery-specific invoice viewer parity still needs a dedicated shared screen beyond current order summary / tracking coverage
3. continue maintainability cleanup if the remaining delivery files begin to drift upward again

## Testing Priorities

- payment-result handling after success, failure, cancel, and launcher error
- shortfall-to-buy-gold rounding and return navigation
- address serviceability and OTP verification transitions
- pending checkout restore and quote refresh behavior
- order summary / tracking state transitions
