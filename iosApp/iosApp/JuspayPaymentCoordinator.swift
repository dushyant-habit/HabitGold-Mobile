import Foundation
import UIKit
import HyperSDK
import ComposeApp

private enum JuspayBridgeKeys {
    static let tradeLaunchNotificationName = Notification.Name("HabitGoldTradePaymentLaunch")
    static let deliveryLaunchNotificationName = Notification.Name("HabitGoldDeliveryPaymentLaunch")
    static let requestId = "requestId"
    static let payloadJson = "payloadJson"
    static let preferredUpiPackage = "preferredUpiPackage"
}

private enum PaymentOrigin {
    case trade
    case delivery
}

private enum JuspayPaymentStatus: String {
    case charged
    case codInitiated = "cod_initiated"
    case autoRefunded = "auto_refunded"
    case backPressed = "backpressed"
    case userAborted = "user_aborted"
}

private struct JuspayConfig {
    let clientId: String
    let merchantId: String
    let routingId: String
    let environment: String
    let enabled: Bool

    static func current() -> JuspayConfig {
        let bundle = Bundle.main
        return JuspayConfig(
            clientId: bundle.infoString(forKey: "JUSPAY_CLIENT_ID"),
            merchantId: bundle.infoString(forKey: "JUSPAY_MERCHANT_ID"),
            routingId: bundle.infoString(forKey: "JUSPAY_ROUTING_ID"),
            environment: bundle.infoString(forKey: "JUSPAY_ENVIRONMENT"),
            enabled: bundle.infoBool(forKey: "JUSPAY_ENABLED")
        )
    }
}

final class JuspayPaymentCoordinator {
    private let hostViewControllerProvider: () -> UIViewController?
    private let baseViewProvider: () -> UIView?
    private let hyperServices = HyperServices()
    private let config = JuspayConfig.current()

    private var tradeNotificationObserver: NSObjectProtocol?
    private var deliveryNotificationObserver: NSObjectProtocol?
    private var currentRequestId: String?
    private var currentOrigin: PaymentOrigin?
    private var pendingProcessPayload: [String: Any]?
    private var isInitiated = false
    private var isInitiating = false

    init(
        hostViewControllerProvider: @escaping () -> UIViewController?,
        baseViewProvider: @escaping () -> UIView?
    ) {
        self.hostViewControllerProvider = hostViewControllerProvider
        self.baseViewProvider = baseViewProvider
        hyperServices.shouldUseViewController = false
        hyperServices.shouldPresentInFullScreen = false
        hyperServices.shouldUseAppNavigationController = false
    }

    func startObserving() {
        if tradeNotificationObserver == nil {
            tradeNotificationObserver = NotificationCenter.default.addObserver(
                forName: JuspayBridgeKeys.tradeLaunchNotificationName,
                object: nil,
                queue: .main
            ) { [weak self] notification in
                self?.handleLaunchNotification(notification, origin: .trade)
            }
        }
        if deliveryNotificationObserver == nil {
            deliveryNotificationObserver = NotificationCenter.default.addObserver(
                forName: JuspayBridgeKeys.deliveryLaunchNotificationName,
                object: nil,
                queue: .main
            ) { [weak self] notification in
                self?.handleLaunchNotification(notification, origin: .delivery)
            }
        }
    }

    func prepareIfNeeded() {
        guard config.enabled else { return }
        guard !isInitiated, !isInitiating else { return }
        guard let hostViewController = hostViewControllerProvider() else { return }
        initiateIfNeeded(on: hostViewController)
    }

    func invalidate() {
        if let tradeNotificationObserver {
            NotificationCenter.default.removeObserver(tradeNotificationObserver)
            self.tradeNotificationObserver = nil
        }
        if let deliveryNotificationObserver {
            NotificationCenter.default.removeObserver(deliveryNotificationObserver)
            self.deliveryNotificationObserver = nil
        }
        currentRequestId = nil
        currentOrigin = nil
        pendingProcessPayload = nil
        if isInitiated || isInitiating {
            hyperServices.terminate()
        }
        isInitiated = false
        isInitiating = false
    }

    private func handleLaunchNotification(_ notification: Notification, origin: PaymentOrigin) {
        guard config.enabled else {
            completeFailure(
                requestId: notification.requestId,
                status: "juspay_disabled",
                message: "Juspay is disabled in this build.",
                origin: origin
            )
            return
        }

        guard !config.clientId.isEmpty, !config.merchantId.isEmpty else {
            completeFailure(
                requestId: notification.requestId,
                status: "juspay_not_configured",
                message: "Juspay merchant configuration is missing in this build.",
                origin: origin
            )
            return
        }

        guard let requestId = notification.requestId else { return }
        guard currentRequestId == nil else {
            completeFailure(
                requestId: requestId,
                status: "payment_in_progress",
                message: "Another payment is already in progress.",
                origin: origin
            )
            return
        }

        guard let payloadJson = notification.payloadJson,
              let processPayload = payloadJson.jsonObjectDictionary else {
            completeFailure(
                requestId: requestId,
                status: "invalid_payload",
                message: "Invalid payment payload.",
                origin: origin
            )
            return
        }

        currentRequestId = requestId
        currentOrigin = origin
        startCheckout(with: processPayload)
    }

    private func startCheckout(with processPayload: [String: Any]) {
        guard let hostViewController = hostViewControllerProvider() else {
            completeFailure(
                requestId: currentRequestId,
                status: "host_unavailable",
                message: "Payment screen is not ready.",
                origin: currentOrigin ?? .trade
            )
            return
        }

        hyperServices.baseViewController = hostViewController
        hyperServices.baseView = baseViewProvider()

        if isInitiated {
            hyperServices.process(hostViewController, processPayload: processPayload)
            return
        }

        pendingProcessPayload = processPayload
        initiateIfNeeded(on: hostViewController)
    }

    private func initiateIfNeeded(on hostViewController: UIViewController) {
        guard !isInitiated, !isInitiating else { return }
        isInitiating = true
        hyperServices.initiate(
            hostViewController,
            payload: buildInitiatePayload(),
            callback: { [weak self] data in
                self?.handleSdkEvent(data)
            }
        )
    }

    private func handleSdkEvent(_ data: [String: Any]?) {
        guard let data, let event = data["event"] as? String else { return }

        switch event {
        case "initiate_result":
            handleInitiateResult(data)
        case "process_result":
            handleProcessResult(data)
        default:
            break
        }
    }

    private func handleInitiateResult(_ data: [String: Any]) {
        isInitiating = false
        let error = data["error"] as? Bool ?? false
        guard !error else {
            let message = (data["errorMessage"] as? String)?.nonEmpty
                ?? (((data["payload"] as? [String: Any])?["errorMessage"] as? String)?.nonEmpty)
                ?? "Unable to start payment."
            let requestId = currentRequestId
            pendingProcessPayload = nil
            completeFailure(
                requestId: requestId,
                status: "initiate_failed",
                message: message,
                origin: currentOrigin ?? .trade
            )
            return
        }

        isInitiated = true
        guard let pendingProcessPayload,
              let hostViewController = hostViewControllerProvider() else { return }
        self.pendingProcessPayload = nil
        hyperServices.process(hostViewController, processPayload: pendingProcessPayload)
    }

    private func handleProcessResult(_ data: [String: Any]) {
        guard let requestId = currentRequestId else { return }
        let origin = currentOrigin ?? .trade
        currentRequestId = nil
        currentOrigin = nil
        let result = parseProcessResult(data)

        switch origin {
        case .trade:
            switch result {
            case .success(let status):
                IosTradePaymentBridgeApi.shared.completeSuccess(
                    requestId: requestId,
                    status: status
                )
            case .failure(let status, let message, let shouldPoll):
                IosTradePaymentBridgeApi.shared.completeFailure(
                    requestId: requestId,
                    status: status,
                    message: message,
                    shouldPollOrderStatus: shouldPoll
                )
            case .backPressed:
                IosTradePaymentBridgeApi.shared.completeBackPressed(requestId: requestId)
            }
        case .delivery:
            switch result {
            case .success(let status):
                IosDeliveryPaymentBridgeApi.shared.completeSuccess(
                    requestId: requestId,
                    status: status
                )
            case .failure(let status, let message, _):
                IosDeliveryPaymentBridgeApi.shared.completeFailure(
                    requestId: requestId,
                    status: status,
                    message: message
                )
            case .backPressed:
                IosDeliveryPaymentBridgeApi.shared.completeBackPressed(requestId: requestId)
            }
        }
    }

    private func completeFailure(
        requestId: String?,
        status: String,
        message: String,
        origin: PaymentOrigin
    ) {
        guard let requestId else { return }
        currentRequestId = nil
        currentOrigin = nil
        pendingProcessPayload = nil
        switch origin {
        case .trade:
            IosTradePaymentBridgeApi.shared.completeFailure(
                requestId: requestId,
                status: status,
                message: message,
                shouldPollOrderStatus: false
            )
        case .delivery:
            IosDeliveryPaymentBridgeApi.shared.completeFailure(
                requestId: requestId,
                status: status,
                message: message
            )
        }
    }

    private func buildInitiatePayload() -> [String: Any] {
        var payload: [String: Any] = [
            "requestId": UUID().uuidString,
            "service": "in.juspay.hyperpay",
            "payload": [
                "action": "initiate",
                "merchantId": config.merchantId,
                "clientId": config.clientId,
                "environment": config.environment,
            ],
        ]

        if !config.routingId.isEmpty, var innerPayload = payload["payload"] as? [String: Any] {
            innerPayload["xRoutingId"] = config.routingId
            payload["payload"] = innerPayload
        }

        return payload
    }

    private func parseProcessResult(_ data: [String: Any]) -> ParsedProcessResult {
        let error = data["error"] as? Bool ?? true
        let payload = data["payload"] as? [String: Any] ?? data
        let status = ((payload["status"] as? String) ?? "").lowercased()
        let message = (data["errorMessage"] as? String)?.nonEmpty
            ?? ((payload["errorMessage"] as? String)?.nonEmpty)
            ?? "Payment \(status.isEmpty ? "failed" : status)"

        if !error {
            switch status {
            case JuspayPaymentStatus.charged.rawValue,
                 JuspayPaymentStatus.codInitiated.rawValue,
                 JuspayPaymentStatus.autoRefunded.rawValue:
                return .success(status: status)
            case JuspayPaymentStatus.backPressed.rawValue,
                 JuspayPaymentStatus.userAborted.rawValue:
                return .backPressed
            default:
                return .failure(status: status, message: message, shouldPollOrderStatus: true)
            }
        }

        switch status {
        case JuspayPaymentStatus.backPressed.rawValue,
             JuspayPaymentStatus.userAborted.rawValue:
            return .backPressed
        default:
            return .failure(status: status, message: message, shouldPollOrderStatus: true)
        }
    }
}

private enum ParsedProcessResult {
    case success(status: String)
    case failure(status: String, message: String, shouldPollOrderStatus: Bool)
    case backPressed
}

private extension Bundle {
    func infoString(forKey key: String) -> String {
        (object(forInfoDictionaryKey: key) as? String)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
    }

    func infoBool(forKey key: String) -> Bool {
        let normalized = infoString(forKey: key).lowercased()
        return normalized == "yes" || normalized == "true" || normalized == "1"
    }
}

private extension Notification {
    var requestId: String? {
        userInfo?[JuspayBridgeKeys.requestId] as? String
    }

    var payloadJson: String? {
        userInfo?[JuspayBridgeKeys.payloadJson] as? String
    }
}

private extension String {
    var jsonObjectDictionary: [String: Any]? {
        guard let data = data(using: .utf8),
              let object = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
            return nil
        }
        return object
    }

    var nonEmpty: String? {
        isEmpty ? nil : self
    }
}
