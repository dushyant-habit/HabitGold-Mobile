import SwiftUI
import UIKit
import ComposeApp
import Clarity
import FirebaseCrashlytics
import FirebaseMessaging
import HyperSDK
import UserNotifications

final class JuspayAppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        IosRuntimeBootstrap.configureFirebaseIfAvailable()
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

        let appEnv = IosRuntimeBootstrap.currentAppEnv()
        let clarityConfig = ClarityConfig(
            projectId: projectId,
            logLevel: .none,
            applicationFramework: .native
        )

        if ClaritySDK.initialize(config: clarityConfig) {
            Crashlytics.crashlytics().setCustomValue(projectId, forKey: "clarity_project_id")
            Crashlytics.crashlytics().log("Clarity initialized for APP_ENV=\(appEnv)")
        } else {
            Crashlytics.crashlytics().log("Clarity initialization returned false for APP_ENV=\(appEnv)")
            NSLog("Clarity initialization returned false for APP_ENV=\(appEnv).")
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
            IosPlatformRuntimeBridge.shared.registerCurrentFcmToken(token: token)
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
        IosPlatformRuntimeBridge.shared.registerCurrentFcmToken(token: fcmToken)
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
        IosPlatformRuntimeBridge.shared.persistReferralUrl(rawUrl: url.absoluteString)
    }

    private func persistAlert(from content: UNNotificationContent) {
        let title = content.title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? "HabitGold" : content.title
        let description = content.body
        IosPlatformRuntimeBridge.shared.recordAlert(title: title, description: description)
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(JuspayAppDelegate.self) private var appDelegate

    init() {
        IosRuntimeBootstrap.configureFirebaseIfAvailable()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
