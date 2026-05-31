import Foundation
import FirebaseCore
import FirebaseCrashlytics
import FirebasePerformance

enum IosRuntimeBootstrap {
    static func shouldLogVerboseRuntimeMessages() -> Bool {
        currentAppEnv() != "prod"
    }

    static func configureFirebaseIfAvailable() {
        guard FirebaseApp.app() == nil else { return }
        let appEnv = currentAppEnv()
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            FirebaseApp.configure()
            Performance.sharedInstance().isDataCollectionEnabled = true
            Performance.sharedInstance().isInstrumentationEnabled = true
            Crashlytics.crashlytics().setCustomValue(appEnv, forKey: "app_env")
            Crashlytics.crashlytics().setCustomValue("GoogleService-Info", forKey: "firebase_plist")
            Crashlytics.crashlytics().log("Firebase configured using bundled GoogleService-Info.plist for APP_ENV=\(appEnv)")
            if shouldLogVerboseRuntimeMessages() {
                NSLog("Firebase configured successfully using bundled GoogleService-Info.plist for APP_ENV=\(appEnv).")
            }
            return
        }
        let resourceName = firebasePlistResourceName(for: appEnv)
        guard let plistPath = Bundle.main.path(forResource: resourceName, ofType: "plist") else {
            NSLog("Firebase skipped: \(resourceName).plist is missing for APP_ENV=\(appEnv).")
            return
        }
        guard let options = FirebaseOptions(contentsOfFile: plistPath) else {
            NSLog("Firebase skipped: could not load FirebaseOptions from \(resourceName).plist.")
            return
        }
        FirebaseApp.configure(options: options)
        Performance.sharedInstance().isDataCollectionEnabled = true
        Performance.sharedInstance().isInstrumentationEnabled = true
        Crashlytics.crashlytics().setCustomValue(appEnv, forKey: "app_env")
        Crashlytics.crashlytics().setCustomValue(resourceName, forKey: "firebase_plist")
        Crashlytics.crashlytics().log("Firebase configured for APP_ENV=\(appEnv)")
        if shouldLogVerboseRuntimeMessages() {
            NSLog("Firebase configured successfully for APP_ENV=\(appEnv).")
        }
    }

    static func currentAppEnv() -> String {
        (Bundle.main.object(forInfoDictionaryKey: "APP_ENV") as? String ?? "prod").lowercased()
    }

    static func firebasePlistResourceName(for appEnv: String) -> String {
        switch appEnv {
        case "prod", "production", "release":
            return "GoogleService-Info-Prod"
        case "preprod", "stage", "staging", "debug":
            return "GoogleService-Info-Staging"
        default:
            return "GoogleService-Info-Staging"
        }
    }
}
