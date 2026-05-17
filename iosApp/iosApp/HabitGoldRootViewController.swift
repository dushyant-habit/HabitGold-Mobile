import UIKit
import ComposeApp

final class HabitGoldRootViewController: UIViewController {
    private let composeViewController = MainViewControllerKt.MainViewController()
    private lazy var juspayCoordinator = JuspayPaymentCoordinator(
        hostViewControllerProvider: { [weak self] in self },
        baseViewProvider: { [weak self] in self?.composeViewController.view }
    )

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        embedComposeViewController()
        juspayCoordinator.startObserving()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        juspayCoordinator.prepareIfNeeded()
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
}
