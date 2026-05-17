import SwiftUI
import UIKit
import HyperSDK

final class JuspayAppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        let sourceApplication = options[.sourceApplication] as? String ?? ""
        return HyperServices.handleRedirectURL(url, sourceApplication: sourceApplication)
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
