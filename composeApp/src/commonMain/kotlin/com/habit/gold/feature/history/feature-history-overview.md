# Phase 10 History, Rewards, Referral, Alerts Overview

## Status

Phase 10 is `checkpointed through shared History, Alerts, and Rewards`.

Current shared app state:

- `History` tab is now a real shared route with list, filters, pagination, and transaction drilldown
- `Rewards` tab is now a real shared route with Rewards Home, Rewards History, Refer & Earn Detail, and Rewards Redeem
- `Alerts` is now a real shared Home-owned route backed by local persistence

The Android audit shows that Phase 10 is not one feature. It is four feature areas with two cross-feature seams:

- `feature/history`
- `feature/rewards`
- `feature/referral`
- `feature/alerts`
- cross-feature seam: `RewardsRedeem` reuses Trade buy/payment foundations
- cross-feature seam: referral attribution has shared state but platform capture

## Locked Decisions

- transactions list and drilldown belong under shared `feature/history`
- alerts belongs under shared `feature/alerts` with local persistence and platform notification hooks
- rewards home, rewards history, and refer detail belong under shared `feature/rewards`
- rewards redeem is rewards-routed but should reuse shared Trade payment/buy foundations
- referral attribution should become a shared contract, but Install Referrer / deep-link capture stay platform-owned
- `ReferralStatusScreen` and `ReferralHistoryScreen` are redesign candidates, not automatic migration targets

## Completed In Current Branch Slice

- replaced `MainTab.History` placeholder with a real shared `HistoryRoute`
- restored Android-style history list structure:
  - custom toolbar
  - trailing status filter
  - type chips
  - shimmer loading
  - pull-to-refresh
  - pagination
  - empty and error states
- reused shared transaction details and invoice viewer from the hardened Trade foundation
- replaced the Home alerts deferred placeholder with a real shared `AlertsRoute`
- added feature-owned local alerts persistence, read-on-open behavior, and Home bell unread-state refresh
- replaced `MainTab.Rewards` placeholder with a real shared `RewardsRoute`
- restored shared Rewards Home, Rewards History, Refer & Earn Detail, and Rewards Redeem
- added platform QR/share support plus Buy/SIP redirects from Rewards
- added targeted tests for:
  - history filtering and pagination behavior
  - alerts viewmodel mapping
  - alerts repository read-state persistence
  - Home unread-alert preference restoration
  - rewards home/history/detail behavior

## Pending Decisions

1. referral status/history product decision
2. final micro-parity pass if device QA finds remaining rewards mismatches
3. optional maintainability split for oversized Rewards UI files

## Quality Rule

Phase 10 must keep code quality in focus while implementation is happening.

Do not mark Phase 10 complete until the mandatory quality pass covers:

- file-size / responsibility review
- duplication / helper extraction review
- unused-code / stale-route sweep
- keyboard/focus behavior review
- localization review
- docs alignment with verified implementation
