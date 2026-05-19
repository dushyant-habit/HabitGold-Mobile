import SwiftUI
import UIKit
import FirebaseCore
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

final class JuspayAppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        configureFirebaseIfAvailable()
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
        let token = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
        UserDefaults(suiteName: platformAppSuite)?.set(token, forKey: currentDeviceTokenKey)
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
