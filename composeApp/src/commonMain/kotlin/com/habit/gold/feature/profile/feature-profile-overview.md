# Profile Feature Overview

## Status

Phase 9 is checkpointed, not fully closed.

Current shared state:

- Home now hands `Profile` off to a real shared `feature/profile` hub route
- shared `feature/profile` foundation exists for:
  - profile fetch
  - profile update
  - KYC verify
  - nominee save
  - portfolio-balance fetch used by delete-account gating
  - logout
  - delete-account request
- the shared Profile hub is live with:
  - account, delivery, and support sections
  - linked handoffs into Savings-owned Autopay and trade-backed VPA list
  - shared logout and delete-account dialogs
- real shared child flows are live for:
  - personal info
  - KYC
  - nominee
  - help center
  - contact us
- Android-style date picker and Profile save path are working again
- Contact Us uses the Android webhook flow again
- Security / biometric is intentionally deferred for a later review pass

The Android source of truth is a real Profile hub with child routes for:

- account details / personal info
- nominee details
- KYC
- security verification
- UPI Autopay management
- Manage UPI IDs
- support and contact surfaces
- logout and delete-account dialogs

## Ownership Decision

The strict audit locks these boundaries:

- `UPI Autopay Management` remains **Savings-owned**
  - Android launches it from Profile and Home
  - but its data model, repository, and state machine are SIP-owned
  - Phase 9 should link into the shared Savings route instead of re-owning it
- `UPI ID Management` remains **payments / trade-backed**
  - Android uses `TradeRepository` for `getUserVpas`, `verifyVpa`, and `setDefaultVpa`
  - Phase 9 should own the Profile entry and screen parity, but not move the repository boundary into Profile

## Required Phase 9 Flows

Phase 9 must cover:

- Profile landing screen
- Personal info / account details
- KYC summary and verification
- Nominee details
- Security / app lock
- Logout
- Delete account flow
- Savings-owned UPI Autopay entry from Profile
- UPI ID Management

## Structure Decision

Default shared structure for implementation:

- `feature/profile`
  - profile hub
  - personal info
  - `kyc`
  - `nominee`
  - `security`
  - `upi`

Do not split a separate top-level `feature/security` unless implementation proves the shared route ownership becomes cleaner that way.

## Current Gap Summary

The biggest remaining Phase 9 gaps are:

- biometric / security parity is still deferred
- Profile child screens still need final micro-parity against Android
- Manage Autopay remains Savings-owned and still needs final shared visual hardening
- Manage UPI IDs still needs final strict parity review under the Profile route
- final end-to-end Phase 9 device QA is still pending

## Implementation Priorities

The implementation order locked by the audit is:

1. finish biometric / security handling from the deferred review pass
2. finish linked UPI surfaces parity
   - Savings-owned Manage Autopay
   - trade-backed UPI ID Management screen
3. final logout/delete-account/Profile hub micro-parity pass
4. final Phase 9 parity and QA pass
