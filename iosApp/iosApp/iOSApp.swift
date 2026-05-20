import SwiftUI
import UIKit
import Clarity
import FirebaseCore
import FirebaseCrashlytics
import FirebaseMessaging
import FirebasePerformance
import HyperSDK
import UserNotifications
import Foundation

private let platformAppSuite = "com.habit.gold.app"
private let pendingReferralKey = "platform.pending_referral_code"
private let currentDeviceTokenKey = "platform.current_device_token"
private let alertsStorageKey = "alerts.items"
private let appPreferencesKey = "app.preferences"

private struct StoredAlertPayload: Codable {
    let id: String
    let title: String
    let description: String
    let createdAt: String
    let isRead: Bool
}

private struct AppPreferencesPayload: Codable {
    let hasUnreadAlerts: Bool
    let isBalanceVisible: Bool
}

final class JuspayAppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        configureFirebaseIfAvailable()
        Messaging.messaging().delegate = self
        configureClarityIfAvailable()
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, _ in
            guard granted else { return }
            DispatchQueue.main.async {
                application.registerForRemoteNotifications()
            }
        }
        return true
    }

    private func configureFirebaseIfAvailable() {
        guard FirebaseApp.app() == nil else { return }
        let appEnv = (Bundle.main.object(forInfoDictionaryKey: "APP_ENV") as? String ?? "prod").lowercased()
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            FirebaseApp.configure()
            Performance.sharedInstance().isDataCollectionEnabled = true
            Performance.sharedInstance().isInstrumentationEnabled = true
            Crashlytics.crashlytics().setCustomValue(appEnv, forKey: "app_env")
            Crashlytics.crashlytics().setCustomValue("GoogleService-Info", forKey: "firebase_plist")
            Crashlytics.crashlytics().log("Firebase configured using bundled GoogleService-Info.plist for APP_ENV=\(appEnv)")
            NSLog("Firebase configured successfully using bundled GoogleService-Info.plist for APP_ENV=\(appEnv).")
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
        NSLog("Firebase configured successfully for APP_ENV=\(appEnv).")
    }

    private func configureClarityIfAvailable() {
        let clarityEnabled = (Bundle.main.object(forInfoDictionaryKey: "ENABLE_CLARITY") as? String ?? "NO")
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .lowercased() == "yes"
        guard clarityEnabled else {
            NSLog("Clarity skipped: ENABLE_CLARITY is disabled.")
            return
        }

        let projectId = (Bundle.main.object(forInfoDictionaryKey: "CLARITY_PROJECT_ID") as? String ?? "")
            .trimmingCharacters(in: .whitespacesAndNewlines)
        guard !projectId.isEmpty else {
            NSLog("Clarity skipped: CLARITY_PROJECT_ID is missing.")
            return
        }

        let appEnv = (Bundle.main.object(forInfoDictionaryKey: "APP_ENV") as? String ?? "prod").lowercased()
        let logLevel: ClarityLogLevel = appEnv == "prod" ? .none : .verbose
        let clarityConfig = ClarityConfig(
            projectId: projectId,
            logLevel: logLevel,
            applicationFramework: .native
        )

        if ClaritySDK.initialize(config: clarityConfig) {
            Crashlytics.crashlytics().setCustomValue(projectId, forKey: "clarity_project_id")
            Crashlytics.crashlytics().log("Clarity initialized for APP_ENV=\(appEnv)")
            NSLog("Clarity initialized for APP_ENV=\(appEnv).")
        } else {
            Crashlytics.crashlytics().log("Clarity initialization returned false for APP_ENV=\(appEnv)")
            NSLog("Clarity initialization returned false for APP_ENV=\(appEnv).")
        }
    }

    private func firebasePlistResourceName(for appEnv: String) -> String {
        switch appEnv {
        case "prod", "production", "release":
            return "GoogleService-Info-Prod"
        case "preprod", "stage", "staging", "debug":
            return "GoogleService-Info-Staging"
        default:
            return "GoogleService-Info-Staging"
        }
    }

    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        let sourceApplication = options[.sourceApplication] as? String ?? ""
        if HyperServices.handleRedirectURL(url, sourceApplication: sourceApplication) {
            return true
        }
        captureReferral(from: url)
        return true
    }

    func application(
        _ application: UIApplication,
        continue userActivity: NSUserActivity,
        restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void
    ) -> Bool {
        if let url = userActivity.webpageURL {
            captureReferral(from: url)
            return true
        }
        return false
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        let apnsToken = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
        Messaging.messaging().apnsToken = deviceToken
        UserDefaults(suiteName: platformAppSuite)?.set(apnsToken, forKey: currentDeviceTokenKey)
        Crashlytics.crashlytics().setCustomValue(apnsToken, forKey: "apns_device_token")
        Messaging.messaging().token { token, error in
            if let error {
                Crashlytics.crashlytics().record(error: error)
                NSLog("Failed to fetch FCM token after APNs registration: \(error.localizedDescription)")
                return
            }
            guard let token, !token.isEmpty else {
                NSLog("FCM token fetch returned empty after APNs registration.")
                return
            }
            self.persistCurrentDeviceToken(token)
            Crashlytics.crashlytics().setCustomValue(token, forKey: "fcm_device_token")
            NSLog("FCM token fetched successfully after APNs registration.")
        }
        NSLog("APNs token registered successfully.")
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let fcmToken, !fcmToken.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            NSLog("Firebase Messaging registration token callback returned empty token.")
            return
        }
        persistCurrentDeviceToken(fcmToken)
        Crashlytics.crashlytics().setCustomValue(fcmToken, forKey: "fcm_device_token")
        NSLog("Firebase Messaging registration token received successfully.")
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        Crashlytics.crashlytics().record(error: error)
        NSLog("APNs registration failed: \(error.localizedDescription)")
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        persistAlert(from: notification.request.content)
        completionHandler([.banner, .sound, .badge])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        persistAlert(from: response.notification.request.content)
        completionHandler()
    }

    private func captureReferral(from url: URL) {
        let components = URLComponents(url: url, resolvingAgainstBaseURL: false)
        let queryCode = components?.queryItems?.first(where: { $0.name == "code" })?.value
        let pathCode = url.lastPathComponent.isEmpty ? nil : url.lastPathComponent
        let referralCode = queryCode ?? pathCode
        guard let referralCode, !referralCode.isEmpty else { return }
        UserDefaults(suiteName: platformAppSuite)?.set(referralCode, forKey: pendingReferralKey)
    }

    private func persistCurrentDeviceToken(_ token: String) {
        let normalized = token.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !normalized.isEmpty else { return }
        UserDefaults(suiteName: platformAppSuite)?.set(normalized, forKey: currentDeviceTokenKey)
    }

    private func persistAlert(from content: UNNotificationContent) {
        let title = content.title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? "HabitGold" : content.title
        let description = content.body
        let defaults = UserDefaults(suiteName: platformAppSuite)
        let decoder = JSONDecoder()
        let encoder = JSONEncoder()

        let currentAlerts: [StoredAlertPayload]
        if let raw = defaults?.string(forKey: alertsStorageKey),
           let data = raw.data(using: .utf8),
           let decoded = try? decoder.decode([StoredAlertPayload].self, from: data) {
            currentAlerts = decoded
        } else {
            currentAlerts = []
        }

        let nextAlert = StoredAlertPayload(
            id: "push-\(Int(Date().timeIntervalSince1970 * 1000))",
            title: title,
            description: description,
            createdAt: ISO8601DateFormatter().string(from: Date()),
            isRead: false
        )

        if let data = try? encoder.encode(Array(([nextAlert] + currentAlerts).prefix(200))),
           let raw = String(data: data, encoding: .utf8) {
            defaults?.set(raw, forKey: alertsStorageKey)
        }

        let currentPreferences: AppPreferencesPayload
        if let raw = defaults?.string(forKey: appPreferencesKey),
           let data = raw.data(using: .utf8),
           let decoded = try? decoder.decode(AppPreferencesPayload.self, from: data) {
            currentPreferences = decoded
        } else {
            currentPreferences = AppPreferencesPayload(hasUnreadAlerts: false, isBalanceVisible: true)
        }

        if let data = try? encoder.encode(
            AppPreferencesPayload(
                hasUnreadAlerts: true,
                isBalanceVisible: currentPreferences.isBalanceVisible
            )
        ), let raw = String(data: data, encoding: .utf8) {
            defaults?.set(raw, forKey: appPreferencesKey)
        }
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(JuspayAppDelegate.self) private var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
