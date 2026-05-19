# Phase 11 Delivery / Get Coin Audit

## Status

Audit status: `strict Android-source audit complete`

Implementation status: `shared delivery / get coin checkpoint is implemented`

## Main Conclusion

Delivery is not a thin card handoff from Trade or Home. It is a feature-owned product area with its own route graph, address state, payment/result handling, and order-tracking behavior.

Shared Phase 11 currently ports:

1. Delivery catalog / Get Coin root
2. Cart and quote review
3. Address list and add/edit/delete address
4. Address OTP verification and pincode serviceability refresh
5. Payment launch and payment-result handling
6. Pending checkout restore
7. Order summary
8. Delivery tracking

The remaining follow-up is not about “whether delivery exists.” It is about final device QA and whether product still needs any additional dedicated invoice/detail parity beyond the current summary / tracking slice.

## Route Ownership

Locked ownership decisions:

- `HomeDestination.Delivery` owns the shared delivery route graph
- `feature/delivery` owns:
  - catalog
  - cart
  - address list
  - add/edit address
  - order summary
  - tracking
- Delivery shortfall should navigate into shared Trade Buy
- Buy must preserve the delivery caller as its return destination
- Delivery launched from `WithdrawalMode` must preserve `WithdrawalMode` as the return target, not fall back to Home

## Current Shared Implementation

Implemented from this audit:

- delivery catalog
- shortfall dialog and buy-gold handoff
- cart
- address CRUD
- address OTP verification
- serviceability validation
- quote/order placement
- payment verification handling
- pending checkout restore
- order summary
- delivery tracking
- targeted tests for DTO mapping and catalog state transitions

Still pending from this audit:

- final device QA across the delivery surfaces
- explicit decision on whether delivery still needs a dedicated shared invoice/detail surface beyond the current route set

## API And Logic Inventory

Shared delivery layer covers:

- product listing
- quote creation
- address CRUD
- send address OTP
- verify address OTP
- serviceability validation
- confirm order
- fetch order details
- list delivery orders
- fetch delivery invoice URL

## Product Rules That Must Not Drift

- affordability checks must use `redeemableGoldGrams`
- serviceability is valid only when verification status is `PINCODE_SERVICEABLE`
- payment cancel / failure must not silently fall into success-style polling
- address edits must preserve existing `type` and `landmark`
- shortfall-to-buy must round up to the next `0.5g`
- returning from that Buy flow must return to Delivery / Get Coin
- Home / Trade / Delivery return destinations must stay explicit and deterministic

## UI Surface Map

### 1. Delivery Catalog / Get Coin

Owns:

- product list
- balance card
- free-delivery badge
- insufficient-balance dialog
- shortfall-to-buy CTA

### 2. Delivery Cart

Owns:

- selected address preview
- coupon entry
- payment details
- proceed-to-checkout CTA

### 3. Delivery Address

Owns:

- address list
- add/edit/delete address entry
- delete confirmation dialog
- fixed bottom CTA when checkout continuation is needed

### 4. Add / Edit Address

Owns:

- form entry
- keyboard dismissal on outside tap
- OTP send / verify path
- address-type preservation

### 5. Order Summary

Owns:

- order placed summary
- visible order ID
- track-delivery CTA

### 6. Tracking

Owns:

- delivery-order list / detail expansion
- courier and tracking metadata
- tracking-link launch

## Testing Priorities

- payment success, failure, cancel, and launcher error
- pending-checkout restore and resume
- address serviceability / OTP transitions
- shortfall buy handoff rounding and return navigation
- order summary / tracking state transitions

## Maintainability Notes

- large delivery files were intentionally split during this checkpoint
- keep new delivery strings resource-backed
- prefer semantic aliases over introducing a third independent color system
- continue adding targeted tests as delivery state logic grows
