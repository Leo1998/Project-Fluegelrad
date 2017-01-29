import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

	func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any] = [:]) -> Bool {
		let urlString = url.absoluteString
		let urlArray = urlString.components(separatedBy: "/")
		
		if urlArray.count == 3 {
			let sponsors = Storage.getSponsors()
			let events = Storage.getEvents()

			for value in events {
				if value.id == Int(urlArray[2]) {
					
					let mainStoryboard = UIStoryboard(name: "Main", bundle: nil)
					let dayVC = mainStoryboard.instantiateViewController(withIdentifier: "CalendarDayViewController") as! CalendarDayViewController
					dayVC.event = value
					dayVC.sponsors = sponsors
					
					let root = window?.rootViewController as! UITabBarController
					
					(root.selectedViewController as! UINavigationController).pushViewController(dayVC, animated: true)
					
				}
			}
			
		}
		
		window?.makeKeyAndVisible()
		return true
	}

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
		
		UIApplication.shared.statusBarStyle = .lightContent
		
		let statusBar: UIView = UIApplication.shared.value(forKey: "statusBar") as! UIView
		if statusBar.responds(to: #selector(setter: UIView.backgroundColor)) {
			statusBar.backgroundColor = UIColor.secondary()
		}
		
        return true
    }
	
	func applicationDidBecomeActive(_ application: UIApplication) {
		application.registerUserNotificationSettings(UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil))

	}

}

