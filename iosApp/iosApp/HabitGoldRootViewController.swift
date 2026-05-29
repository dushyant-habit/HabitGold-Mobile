import UIKit
import ComposeApp

final class HabitGoldRootViewController: UIViewController, UIGestureRecognizerDelegate {
    private let composeViewController = MainViewControllerKt.MainViewController()
    private let backGestureView = UIView()
    private lazy var backEdgeGestureRecognizer: UIScreenEdgePanGestureRecognizer = {
        let recognizer = UIScreenEdgePanGestureRecognizer(
            target: self,
            action: #selector(handleBackEdgeGesture(_:))
        )
        recognizer.edges = .left
        recognizer.delegate = self
        recognizer.cancelsTouchesInView = false
        recognizer.delaysTouchesBegan = false
        return recognizer
    }()
    private lazy var juspayCoordinator = JuspayPaymentCoordinator(
        hostViewControllerProvider: { [weak self] in self },
        baseViewProvider: { [weak self] in self?.composeViewController.view }
    )

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        embedComposeViewController()
        installBackGestureView()
        juspayCoordinator.startObserving()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        juspayCoordinator.prepareIfNeeded()
    }

    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        .portrait
    }

    override var preferredInterfaceOrientationForPresentation: UIInterfaceOrientation {
        .portrait
    }

    override var shouldAutorotate: Bool {
        false
    }

    deinit {
        juspayCoordinator.invalidate()
    }

    private func embedComposeViewController() {
        addChild(composeViewController)
        composeViewController.view.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(composeViewController.view)
        NSLayoutConstraint.activate([
            composeViewController.view.topAnchor.constraint(equalTo: view.topAnchor),
            composeViewController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            composeViewController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            composeViewController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        composeViewController.didMove(toParent: self)
    }

    private func installBackGestureView() {
        backGestureView.translatesAutoresizingMaskIntoConstraints = false
        backGestureView.backgroundColor = .clear
        view.addSubview(backGestureView)
        NSLayoutConstraint.activate([
            backGestureView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            backGestureView.topAnchor.constraint(equalTo: view.topAnchor),
            backGestureView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            backGestureView.widthAnchor.constraint(equalToConstant: 24),
        ])
        backGestureView.addGestureRecognizer(backEdgeGestureRecognizer)
    }

    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        guard gestureRecognizer === backEdgeGestureRecognizer else { return true }
        return IosBackGestureBridgeApi.shared.canHandleBackGesture()
    }

    @objc
    private func handleBackEdgeGesture(_ gestureRecognizer: UIScreenEdgePanGestureRecognizer) {
        guard gestureRecognizer.state == .recognized else { return }
        _ = IosBackGestureBridgeApi.shared.handleBackGesture()
    }
}
