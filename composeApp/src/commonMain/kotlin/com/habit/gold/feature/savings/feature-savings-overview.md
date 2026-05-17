# Savings Feature Overview

## Status

Phase 8 is in progress.

Current state:

- audit completed
- shared `feature/savings` foundation created
- second production slice live:
  - mandate management route
  - loading / error / empty states
  - status filtering
  - pause / resume / cancel actions
  - daily / weekly / monthly setup route
  - upgrade / resume mandate handoff from Home
  - mandate-session payment launch
  - post-payment mandate polling
  - pending / success / failure setup states
- strict re-audit completed and setup flow rebuilt from the stricter logic + UI contract

Still pending in Phase 8:

- exact compounding bottom-sheet parity
- final setup micro-parity pass for spacing, sheet density, and detail rhythm
- savings-specific coupon and promo UX refinement
- Buy-tab SIP parity if product keeps a dedicated Buy SIP entry
- manage-autopay strict visual parity
- execution history / additional detail parity if required
- final end-to-end Savings QA pass

## Slice 1 Ownership

The first slice intentionally owns only mandate-management behavior:

- `GET sip/mandates`
- `GET sip/mandates/{id}`
- `GET sip/mandates/{id}/executions`
- `POST sip/mandate/{id}/pause`
- `POST sip/mandate/{id}/resume`
- `POST sip/mandate/{id}/cancel`

It replaces the previous Home savings placeholder route with a real shared screen.

## Slice 2 Ownership

The second major Savings slice now owns the setup / upgrade flow:

- daily / weekly / monthly setup
- upgrade existing mandate amount
- execution-day selection
- resume handoff for paused mandates
- Juspay launch
- post-payment mandate verification and result states

## Next Slice

The next Savings slice should focus on polish and parity:

- compounding sheet exact parity
- savings coupon and promo UX refinement
- Buy-tab SIP entry parity if still required
- manage-autopay strict visual parity
- execution-history / detail surfaces if product needs them
- final end-to-end Savings QA
