# Phase 9 Profile And Security Audit

## Status

This audit is decision-complete for Phase 9, and the current implementation is a checkpoint rather than final sign-off.

It covers:

- Profile landing
- Personal info
- KYC
- Nominee
- Security / App Lock
- Logout and delete-account
- UPI Autopay Management
- UPI ID Management

It also resolves the current cross-phase ownership conflict between Phase 8 Savings and Phase 9 Profile.

## Android Source Of Truth

Primary Android references discovered during the audit:

- `ProfileScreen.kt`
- `PersonalInfoScreen.kt`
- `KycScreen.kt`
- `NomineeDetailsScreen.kt`
- `SetupSecurityScreen.kt`
- `SecurityVerificationScreen.kt`
- `UpiAutopayManageScreen.kt`
- `vpa/VpaListScreen.kt`
- `profile/ProfileViewModel.kt`
- `UpiAutopayViewModel.kt`
- `vpa/VpaListViewModel.kt`
- `navigation/NavGraph.kt`
- `navigation/Routes.kt`
- `data/remote/ApiService.kt`
- `data/repository/AuthRepositoryImpl.kt`
- `data/model/UserDtos.kt`

## Current KMP State

Current shared repo truth:

- Home now routes `Profile` to a real shared `ProfileDestination.Hub`
- shared `feature/profile` exists with data/domain/presentation foundation
- first-slice shared Profile hub is implemented
- Savings already owns the shared `Manage Autopay` implementation
- Trade already owns shared VPA / payout primitives and invoice/payment-linked UPI behavior

This means Phase 9 is not starting from a blank slate. It must integrate cleanly with:

- existing shared auth/session/profile-complete state
- existing shared Savings-owned Autopay route
- existing trade/payments-backed VPA ownership

## Current Shared Checkpoint

The following work is now live in shared code:

- shared `feature/profile` module and DI
- shared profile remote/repository/use-case foundation
- shared profile update/KYC verify/nominee save request paths
- session-aware shared logout and delete-account request handling
- real shared Profile hub route from Home
- real shared Personal Info route with Android-style DOB picker
- real shared KYC route
- real shared Nominee route
- real shared Help Center and Contact Us routes
- Savings-owned Autopay entry from Profile
- trade-backed VPA list entry from Profile
- shared logout and delete-account dialogs

Still pending from this audit:

- biometric / security parity and crash hardening
- final micro-parity for Profile hub, child routes, and linked UPI surfaces
- final end-to-end Phase 9 device QA

## Ownership Decision

### Locked decision: `UPI Autopay Management`

`UPI Autopay Management` stays **Savings-owned**.

Reason:

- Android launches it from Home and Profile
- Android viewmodel is SIP-backed
- pre-migration audit already states Autopay is a SIP concern
- KMP already has a shared Savings-owned mandate-management route

Phase 9 responsibility:

- provide the Profile entry point and correct navigation
- do not re-own the mandate repository or create a duplicate `Manage Autopay` feature under Profile

### Locked decision: `UPI ID Management`

`UPI ID Management` stays **trade/payments-backed but Profile-routed**.

Reason:

- Android `VpaListViewModel` uses `TradeRepository`
- endpoints are under `user/vpa`
- the same VPA set is already used by Sell payout flows

Phase 9 responsibility:

- include the screen in the audit and parity plan
- own the Profile route entry and UI parity work
- do not move VPA backend ownership into Savings

## Route Inventory

Android route inventory relevant to Phase 9:

- `Route.Profile`
- `Route.PersonalInfo`
- `Route.NomineeDetails`
- `Route.Kyc`
- `Route.SecurityVerify`
- `Route.UpiAutopayManage`
- `Route.VpaList`
- linked support routes from Profile:
  - `Route.HelpCenter`
  - `Route.ContactUs`
  - `Route.AddressBook`
  - `Route.DeliveryTracking`

Current route behavior:

- Home top bar profile avatar opens `Route.Profile`
- Profile is a hub screen, not a modal
- child routes are pushed from Profile and return with normal back behavior
- logout exits the authenticated graph and navigates to login
- security verification is also used as a top-level gate in Android, not only from Profile

## Screen Inventory

### 1. Profile landing

Sections on Android:

- centered top app bar with back
- large centered user name + phone header
- `ACCOUNT` group card
  - account details
  - UPI Autopay
  - Manage UPI IDs
  - nominee details
  - enable biometric
  - KYC status
- `DELIVERY` group card
  - track order
  - saved addresses
- `SUPPORT` group card
  - help center
  - contact us
- logout card
- delete-account card
- app version footer
- logout dialog
- delete-account dialog
- delete-blocked dialog when gold balance is non-zero

### 2. Personal info

Sections:

- centered top app bar
- editable form
  - legal name
  - email
  - DOB
  - gender
- sticky save button footer

Special behavior:

- legal name becomes locked after signup if already set
- DOB uses a picker dialog
- save is disabled until valid changes exist

### 3. KYC

Sections:

- centered top app bar
- PAN field
- full-name field
- loading skeleton state
- blocking error dialog
- sticky submit button footer

Special behavior:

- verified PAN details become read-only
- successful verification pops back
- error path uses a dedicated modal dialog, not just a toast

### 4. Nominee details

Sections:

- centered top app bar
- nominee name
- relationship dropdown
- mobile number with `+91` prefix
- sticky save button footer

Special behavior:

- relationship uses dropdown, not free text
- mobile allows only 10 digits
- save requires valid changes

### 5. Security / App Lock

Android splits security into two surfaces:

- `SetupSecurityScreen`
  - post-auth / setup-oriented screen
  - skip action in top bar
  - four-digit MPIN entry
  - biometric toggle
  - custom keypad
- `SecurityVerificationScreen`
  - verification / unlock screen
  - optional biometric auto-auth
  - four-digit MPIN verification
  - error state on wrong PIN
  - custom keypad

This split is non-negotiable for parity.

### 6. UPI Autopay Management

Android surface already audited in Phase 8:

- separate Manage Autopay list
- filter sheet
- pause / resume / revoke dialogs
- status-specific cards

Phase 9 only needs:

- correct Profile entry point
- ownership note
- parity consistency with Profile launch expectations

### 7. UPI ID Management

Sections:

- centered top app bar
- loading skeleton
- error text state
- empty state
- list of UPI cards
- bottom info note
- optional add dialog in code, but floating action is disabled in Android by constant

Special behavior:

- verified badge
- default badge
- `set as default` action exists in code but currently hidden behind constant
- add-VPA flow exists but is also gated by a constant
- info note says new UPI IDs are usually added automatically after a UPI payment

## Logic And State Audit

### Profile hub

Android `ProfileViewModel` owns:

- `getUserProfile`
- `getPortfolioDashboard`
- `updateUserProfile`
- `logout`
- `requestDeleteAccount`
- `verifyKyc`

State model:

- `Idle`
- `Loading`
- `Success(profile, vpas, totalGoldBalanceGrams, message?)`
- `LogoutSuccess`
- `Error`

Important behavior:

- screen seeds from local DataStore and syncs with remote profile
- logout success triggers graph exit
- delete-account request first checks gold balance in UI before allowing destructive flow
- delete-account success logs the user out

### Personal info validation

Observed Android rules:

- legal name required
- legal name filtered with `filterLegalNameInput()`
- legal name may become read-only after signup
- save only enabled when valid changes exist
- DOB normalized before API call
- gender normalized before API call

### KYC validation

Observed rules:

- PAN max length 10
- PAN uppercased
- both PAN and name required before submit
- verified KYC fields become read-only
- error dialog blocks back-dismiss until acknowledged

### Nominee validation

Observed rules:

- nominee name cannot contain digits
- relation required from fixed list:
  - Father
  - Mother
  - Husband
  - Wife
  - Son
  - Daughter
  - Brother
  - Sister
  - Other
- mobile required and exactly 10 digits
- save only enabled when valid changes exist

### Security / App Lock logic

Observed rules:

- setup is distinct from verification
- biometric enablement checks device capability
- enabling biometric requires successful biometric prompt
- verification can auto-launch biometrics when enabled
- wrong PIN shows inline error and clears after delay

### Logout and delete-account

Observed rules:

- logout uses a confirmation dialog
- delete-account is blocked if `totalGoldBalanceGrams > 0`
- allowed delete flow requires typing `HABITGOLD`
- delete-account request ultimately logs the user out on success

### UPI surfaces

Autopay:

- SIP-backed state machine
- fetch mandates
- pause/resume/cancel
- status filtering

UPI IDs:

- `getUserVpas`
- `verifyVpa`
- `setDefaultVpa`
- add/default actions exist in code but are partially product-disabled by constants

## API Inventory

### Auth / Profile-owned APIs

From Android `ApiService` and `AuthRepositoryImpl`:

- `GET user/profile`
- `PUT user/profile`
- `GET portfolio`
- `POST auth/logout`
- `POST kyc/verify`
- `DELETE/POST account-delete request` via `requestDeleteAccount()` path in repo layer

Shared DTOs needed:

- `UserProfileDto`
- `NomineeDto`
- `UserKycDto`
- `UpdateProfileRequestDto`
- `KycVerifyRequestDto`

### Savings-owned UPI Autopay APIs

Already migrated in Phase 8:

- `GET sip/mandates`
- `GET sip/mandates/{id}`
- `GET sip/mandates/{id}/executions`
- `POST sip/mandate/{id}/pause`
- `POST sip/mandate/{id}/resume`
- `POST sip/mandate/{id}/cancel`

### Trade/payments-backed UPI ID APIs

- `GET user/vpa`
- `POST user/vpa/verify`
- `PATCH user/vpa/{id}/set-default`

## Asset And String Ledger

### Asset findings

Phase 9 does **not** appear to depend on dedicated raster hero images like Savings.

UI is primarily built from:

- Material icons
- shaped cards / pills / badges
- biometric / lock vector iconography
- custom keypad layouts for security screens

Important parity assets/components to preserve:

- profile section headers and grouped cards
- red destructive cards/dialog icon circles
- KYC badges and pills
- custom PIN dot boxes and keypad
- biometric toggle row styling

### String findings

Important user-facing strings include:

- Profile section titles
- KYC status labels:
  - Verified
  - Pending
  - Retry
  - Start Now
- legal-name immutability helper text
- delete-account warnings
- `type HABITGOLD` confirmation
- UPI info note
- biometric success/failure copy

Do not hardcode these in shared code.

## Must Match Android Exactly

The following are parity-critical and should not be “simplified” during migration:

- Profile must remain a hub screen with grouped sections, not a flat settings list
- Autopay must stay Savings-owned even if launched from Profile
- UPI ID Management must remain a distinct screen from Autopay
- security must remain split into `setup` and `verify`
- delete-account must keep:
  - gold-balance block
  - `HABITGOLD` confirmation
  - logout-on-success behavior
- KYC must keep read-only behavior once verified
- nominee relation must stay dropdown-backed, not free text
- Personal Info legal-name lock must be preserved

## Implementation Order

Phase 9 should be implemented in this order:

1. create shared `feature/profile` umbrella with subpackages:
   - hub
   - personal info
   - `kyc`
   - `nominee`
   - `security`
   - `upi`
2. port profile DTO/domain/repository/use cases
3. replace Home Profile deferred placeholder with real Profile route
4. build Profile landing hub
5. build Personal Info
6. build KYC
7. build Nominee
8. build Security setup and verify flows
9. wire Profile entry to:
   - Savings-owned Manage Autopay
   - trade-backed UPI ID Management
10. add logout and delete-account flow
11. add tests for:
   - profile state transitions
   - update-profile validation gating
   - KYC success/error handling
   - nominee validation
   - security verification state
   - logout/delete-account state effects

## Roadmap Corrections Locked By This Audit

Phase 9 should not describe `UPI Autopay Management` as a separately re-owned feature.

Correct interpretation:

- Phase 9 owns the Profile launch point and parity review for it
- Phase 8 Savings still owns the actual shared Manage Autopay feature

Phase 9 does still fully own the audit and parity plan for `UPI ID Management`.
