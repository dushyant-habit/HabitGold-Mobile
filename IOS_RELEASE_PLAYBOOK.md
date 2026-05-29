# HabitGold iOS Release Playbook

This document stores the current App Store Connect setup decisions and the step-by-step iOS release flow for HabitGold.

Last updated: 2026-05-27

## Current App Store Connect Values

- App Name: `HabitGold`
- Bundle ID: `com.habit.gold`
- Company Name / Developer Name chosen in App Store Connect: `Auricol Technologies Private Limited`
- Recommended SKU: `habitgold-com.habit.gold`
- Recommended User Access: `Full Access`
- Xcode workspace: `iosApp/iosApp.xcworkspace`
- Xcode scheme for production upload: `iOSAppProd`

## Important Rules

- Open `iosApp/iosApp.xcworkspace`, not `iosApp/iosApp.xcodeproj`.
- Use the paid Apple Developer team, not `Personal Team`.
- Keep `Automatically manage signing` turned on.
- Do not manually force `Apple Distribution` in Build Settings while automatic signing is on.
- For TestFlight/App Store upload, use `Product > Archive`, not `Run`.
- Every new upload needs a new build number.

## What Company Name Means

- The App Store Connect `Company Name` becomes the developer name shown on the App Store.
- This was chosen as `Auricol Technologies Private Limited`.
- Apple treats this as important metadata, so it should be considered fixed for this app record.

## Round 1: Upload To TestFlight

### One-time checks

1. Open `iosApp/iosApp.xcworkspace` in Xcode.
2. Select the `iosApp` target.
3. Open `Signing & Capabilities`.
4. Confirm `Automatically manage signing` is ON.
5. Confirm the selected team is the paid Apple Developer team.
6. Confirm the production bundle ID is `com.habit.gold`.
7. Select the `iOSAppProd` scheme.

### Archive and upload

1. In Xcode, choose destination `Any iOS Device` or `Generic iOS Device`.
2. Click `Product > Archive`.
3. Wait for Organizer to open.
4. Select the latest archive.
5. Click `Distribute App`.
6. Choose `TestFlight & App Store` or `App Store Connect`.
7. Choose `Upload`.
8. If Xcode asks for `Distribution Preparation`, choose `App Store`.
9. Keep the default upload options unless there is a specific reason to change them.
10. Finish the upload.

### After upload

1. Open App Store Connect.
2. Go to `Apps > HabitGold > TestFlight`.
3. Wait for the build to finish processing.
4. Add the processed build to an internal testing group.
5. Install and test through the TestFlight app on real iPhones.

## Round 2: Submit To The App Store

If the TestFlight build is good and no code changes are needed, the same uploaded build can be used for App Store review. A second upload is not required unless the app changed.

### Submit the tested build

1. Open App Store Connect.
2. Go to `Apps > HabitGold`.
3. Create the iOS app version if needed.
4. Fill the required metadata:
   - description
   - screenshots
   - privacy policy URL
   - app privacy answers
   - age rating
   - export compliance
5. In the `Build` section, select the tested build.
6. Click `Add for Review`.
7. Click `Submit for Review`.
8. Prefer manual release for the first launch.

## If You Want To Run Prod On A Physical iPhone

Running from Xcode is different from uploading to TestFlight.

- `Run` uses development signing.
- Development signing requires at least one registered device on the Apple Developer team.

### If Xcode says:

- `Your team has no devices from which to generate a provisioning profile`
- `No profiles for 'com.habit.gold' were found`

That means Xcode is trying to create a development provisioning profile for local device testing.

### Fix

1. Connect a real iPhone to the Mac.
2. Unlock it and trust the Mac if prompted.
3. Keep automatic signing ON.
4. Let Xcode register that device on the paid team.
5. Run again.

If the goal is only TestFlight upload, skip `Run` completely and use `Archive`.

## Common Errors And Fixes

### Error: conflicting provisioning settings / Apple Distribution manually specified

Cause:

- Automatic signing is ON, but Build Settings also manually force `Apple Distribution`.

Fix:

- Remove the manual distribution override.
- Keep automatic signing ON.
- Let Xcode handle distribution signing during archive/upload.

### Error: no devices from which to generate a provisioning profile

Cause:

- Trying to run the app with development signing, but the team has no registered device.

Fix:

- Connect and register a physical device, or
- skip `Run` and use `Archive` for TestFlight.

### Error: build uploaded before but new upload is rejected

Cause:

- Build number was not incremented.

Fix:

- Increase the build number before uploading again.

## Recommended Values For This App Record

- Company Name: `Auricol Technologies Private Limited`
- App Name: `HabitGold`
- Bundle ID: `com.habit.gold`
- SKU: `habitgold-com.habit.gold`
- User Access: `Full Access`

## Official Apple References

- App record setup:
  - https://developer.apple.com/help/app-store-connect/create-an-app-record/add-a-new-app/
- Developer name:
  - https://developer.apple.com/help/app-store-connect/create-an-app-record/set-your-developer-name/
- Upload builds:
  - https://developer.apple.com/help/app-store-connect/manage-builds/upload-builds/
- Internal testers:
  - https://developer.apple.com/help/app-store-connect/test-a-beta-version/add-internal-testers/
- Submit for review:
  - https://developer.apple.com/help/app-store-connect/manage-submissions-to-app-review/submit-an-app/
- Development provisioning profiles:
  - https://developer.apple.com/help/account/provisioning-profiles/create-a-development-provisioning-profile/
- Register devices:
  - https://developer.apple.com/help/account/register-devices/register-a-single-device/
