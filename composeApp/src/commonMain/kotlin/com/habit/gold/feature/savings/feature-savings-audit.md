# Phase 8 Savings Audit

## Scope

Phase 8 covers HabitGold's recurring gold savings / SIP flows. In Android, this is not a single screen. It is a connected product area spanning:

- Home savings cards and mandate previews
- Gold saving setup flows for `Daily`, `Weekly`, and `Monthly`
- Upgrade flows for existing mandates
- UPI autopay mandate management
- SIP-related polling, success, pending, and failure states
- SIP-linked transaction metadata that later appears in History / Trade transaction details

This audit is intentionally strict and implementation-oriented. It exists to prevent the same drift we had earlier where placeholder screens or generic layouts were treated as parity work.

## Android Source Of Truth

Primary Android references audited for this phase:

- `ApiService.kt`
- `SipDtos.kt`
- `SipRepository.kt`
- `SipRepositoryImpl.kt`
- `GoldSavingSetupScreen.kt`
- `UpiAutopayManageScreen.kt`
- `UpiAutopayViewModel.kt`
- `HomeScreen.kt`
- `NavGraph.kt`
- `Routes.kt`

## Real Product Surface

Savings is made of two major flows:

1. `Setup / upgrade flow`
   - entered from Home savings cards
   - frequency-specific entry (`Daily`, `Weekly`, `Monthly`)
   - supports creating a new savings mandate
   - supports upgrading an existing mandate
   - owns amount entry, frequency/day selection, coupon handling, Juspay handoff, and post-payment polling

2. `Manage autopay flow`
   - entered from Home mandate previews and profile/home shortcuts
   - lists existing mandates
   - filters by status
   - supports pause / resume / cancel actions
   - shows mandate metadata and helper notes for paused, cancelled, and failed states

## API Inventory

Android already uses these endpoints:

- `POST sip/mandate/session`
- `POST sip/mandates/{id}/update-session`
- `GET sip/mandates`
- `GET sip/mandates/{id}`
- `GET sip/mandates/{id}/executions`
- `POST sip/mandate/{id}/pause`
- `POST sip/mandate/{id}/resume`
- `POST sip/mandate/{id}/cancel`

### Request / response shapes

`CreateSipMandateRequestDto`
- `amount: Int`
- `frequency: String`
- `name: String`
- `goalType: String?`
- `executionDay: Int?`
- `promoCode: String?`

`CreateSipMandateResponseDto`
- `mandateId: String`
- `sdk_payload: JsonObject?`

`SipMandateDto`
- full mandate state
- billing fields appear both flattened and nested under `billing`
- includes promo metadata and sync fields

`SipExecutionDto`
- `id`
- `executionDate`
- `amount`
- `status`

## Navigation Inventory

Android Home does not open a generic placeholder for savings. It routes into real flows:

- Home savings cards open setup / upgrade by frequency
- Home "view all" mandate area routes to `UpiAutopayManage`
- Transaction details later consume SIP metadata

Current KMP before this phase:

- Home opened `HomeDeferredTarget.Savings`
- that rendered a placeholder handoff card
- no shared `feature/savings` package existed

That is the primary Phase 8 gap.

## Strict Route-Entry Contract

This is the first place the earlier shared setup implementation drifted.

### What Home passes into setup

Android Home passes only:

- `frequency`
- `amount`
- `mandateId`
- `executionDay`

It does **not** block the first render on a mandate-detail fetch.

### What setup must do on first paint

- `new setup`
  - render immediately from frequency defaults and any optional amount passed in
- `upgrade from Home`
  - render immediately from Home-passed `amount`, `mandateId`, and `executionDay`
  - do not show a loading screen before the user sees the setup UI
- `paused / resume-style Home handoff`
  - Android still routes through the same `mandateId`-based setup contract
  - do not invent a separate loading-first resume screen unless product explicitly changes

### Non-negotiable rule

For Home-driven savings setup:

- do **not** show `Loading your savings plan...` as the first screen just because `mandateId` exists
- only fetch additional mandate detail if something critical is missing and cannot be derived from the route payload

## Exact Route Param Logic

Android computes route values from Home as follows:

- `amount`
  - prefer current billing amount
  - then next execution amount
  - then mandate amount
- `executionDay`
  - `WEEKLY`: derive Monday-first weekday number from `nextExecutionDate`
  - `MONTHLY`: derive day-of-month from `nextExecutionDate`
  - `DAILY`: no execution day

This route-param behavior is part of logic parity, not just navigation parity.

## State Inventory

### Setup / upgrade flow state

Android setup owns:

- frequency config
- amount text
- selected execution day
- coupon draft / applied coupon
- setup vs upgrade mode
- payment readiness
- polling
- processing
- success
- pending
- failure

### Setup flow logic parity rules

Android setup logic is more specific than a generic “enter amount and pay” screen.

#### Frequency configuration

`Daily`
- default amount: `50`
- min: `10`
- max: `5000`
- chips: `50`, `100`, `150`, `200`, `500`

`Weekly`
- default amount: `500`
- min: `50`
- max: `15000`
- chips: `100`, `300`, `500`, `1000`, `1500`

`Monthly`
- default amount: `2500`
- min: `50`
- max: `15000`
- chips: `500`, `1000`, `2500`, `5000`, `10000`

#### Upgrade defaults

Android seeds upgrade amount using a recommendation strategy:

- choose the first chip greater than the current amount
- otherwise bump above current amount while staying within max

#### Validation

- amount must be between frequency min/max
- upgrade amount must be greater than current amount
- weekly requires a selected weekday
- monthly requires a selected monthly date

#### Mandate session creation

- `new setup` -> `createSipMandateSession`
- `existing mandate` -> `updateSipMandateSession`

Both requests use:

- `name = "<Frequency> Gold Savings"`
- `goalType = "SAVINGS"`
- `promoCode` only if non-empty

#### Payment return behavior

- `backpressed` -> reset flow back to setup form
- `success` and `failure` callback types both can lead into mandate polling if `pendingMandateId` exists
- missing SDK payload is a hard failure path

#### Polling behavior

- poll `GET sip/mandates/{id}` up to `6` times every `5s`
- `ACTIVE` -> success
- `FAILED_REGISTRATION` -> failure
- `PENDING_REGISTRATION` or no terminal answer after max polls -> processing / pending state

This polling logic is mandatory parity behavior. It should not be simplified into a one-shot success assumption.

This is a large migration slice and should not be mixed casually with mandate management.

### Manage autopay flow state

Android manage flow owns:

- loading
- loaded mandates
- error
- status filtering
- local expanded/collapsed cards
- pause / resume / cancel actions
- helper notes per status bucket

This is a clean first shared slice because it has real product value and much lower coupling to Juspay and setup selectors.

## UI Audit

### Home savings entry

Current shared Home already has:

- savings plan cards
- active mandate previews

But it lacks:

- a real destination when the user taps into savings details

### UPI Autopay Manage screen

Important Android characteristics:

- white child screen with simple top app bar
- status filter action in top bar
- loading shimmer list
- mandate list with expandable cards
- status chip and amount in header row
- detail rows when expanded
- helper notes for paused / cancelled / failed registration states
- primary CTA varies by status
- revoke action is secondary text action
- pause and revoke use confirmation dialogs

### Setup / upgrade screen

Important Android characteristics:

- very large screen with frequency-specific hero copy and dynamic config
- top hero image changes by frequency
- amount entry and selector sheets
- coupon handling
- compounding preview card
- next-payment summary card
- bottom live price bar
- embedded Juspay handoff
- polling and result states

This should be treated as a second slice, not forced into the first implementation pass.

### Savings hero asset ledger

Android uses real frequency-specific drawables:

- `start_daily_savings_icon.png`
- `start_weekly_savings_icon.png`
- `start_monthly_savings_icon.png`

Missing these is a parity failure, not a polish issue.

### Setup UI surface inventory

Android setup is made of these visible sections:

1. top app bar with back + help
2. frequency-specific hero image
3. amount hint label
4. amount card with:
   - rupee prefix
   - large amount text
   - suffix or schedule selector on the right
5. rotating projection text block
6. quick amount chips with optional tags
7. coupon section
8. compounding preview card
9. next payment card
10. bottom live buy price bar
11. bottom CTA surface
12. weekly / monthly selector bottom sheets
13. success / pending / failure full-screen states

Any shared rebuild should be audited section-by-section against this list before being called parity work.

## Current KMP Drift Found In Re-Audit

These are the concrete misses from the current shared setup implementation:

- it introduced a loading-first setup screen for mandate-backed flows, which Android does not do
- it did not include the frequency hero images
- it treated the setup UI as an acceptable first pass before a strict visual audit
- it did not lock the exact Android section inventory before implementation
- it narrowed the setup flow surface too early, which risks logic parity drift on coupon and compounding behavior

## Current Shared Status

The shared setup flow now covers:

- no-loading-first Home-driven setup entry
- daily / weekly / monthly setup screens
- frequency hero assets
- rotating projection text
- quick amount chips with chip-attached tags
- coupon entry and offers flow
- compounding preview card
- next payment card
- live price footer and CTA
- weekly selector sheet
- monthly calendar-style selector
- payment handoff and post-payment polling states

Still pending from strict parity review:

- exact compounding bottom-sheet parity
- final setup spacing / density micro-parity
- savings-specific promo UX refinement
- manage-autopay visual parity
- final end-to-end Savings QA

## Phase 8 Implementation Strategy

### First production slice

The first production slice should:

- create a real shared `feature/savings`
- port the full savings data/domain foundation
- replace the Home savings placeholder with a real shared manage-autopay route
- include:
  - mandate list
  - status filtering
  - pause / resume / cancel
  - loading / empty / error states
  - focused tests

### Deferred within Phase 8

The following remain Phase 8 work after the strict re-audit:

- exact compounding bottom-sheet parity
- final setup spacing / density micro-parity
- savings coupon validation and promo UX refinement
- Buy-tab SIP parity if product keeps a dedicated Buy SIP setup entry
- manage-autopay visual parity
- execution history screen if product needs a dedicated surface
- final end-to-end Savings QA

## Non-negotiable rules for further Savings work

- Do not use a placeholder handoff card once a real feature route exists.
- Do not treat setup and manage as one "generic savings screen".
- Do not implement setup UI before the data/domain and route boundary are stable.
- Do not bury savings logic back inside Home once `feature/savings` exists.
- Do not ship hardcoded strings for completed Savings surfaces.
- Do not mark Phase 8 complete until setup / upgrade, manage, and payment-result flows are all covered.

## Slice 2 Completion Notes

The current shared Phase 8 implementation now includes:

- Home savings cards opening real shared setup / upgrade routes
- frequency-aware setup for `Daily`, `Weekly`, and `Monthly`
- mandate-aware upgrade / resume behavior
- create / update mandate-session API wiring
- shared Juspay launch through the existing payment contract
- post-payment mandate polling with `polling`, `processing`, `success`, and `failure` states
- strict re-audit-driven setup rebuild with:
  - real daily / weekly / monthly hero assets
  - immediate first paint from Home-passed route data
  - setup top bar with help wiring
  - live buy price footer
  - coupon row and setup promo state
  - compounding preview card and next-payment card

This means the biggest remaining Phase 8 work is no longer route ownership or payment plumbing. It is the remaining product polish and any deferred SIP surfaces outside the current Home-driven setup flow.

## Current Phase 8 Rule

The current shared setup screen is no longer a placeholder rebuild candidate.

Further Savings work should now follow this order:

1. finish exact setup micro-parity
2. finish manage-autopay visual parity
3. close any deferred coupon / promo and detail-surface gaps
4. run final end-to-end Savings QA

Do not restart the setup flow from scratch again unless a new audit proves the route-entry or logic contract itself is wrong.
