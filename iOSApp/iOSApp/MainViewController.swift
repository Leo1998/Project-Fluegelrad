import UIKit

class MainViewController: UITabBarController {
	
	/**
	A refference to itself to use in static methods
	*/
	private static var selfish: MainViewController!
	
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }

    override func viewDidLoad() {
        super.viewDidLoad()
		
		_ = Storage()
		
		MainViewController.selfish = self
		
		// Setting the color of the TabBar titles to match the app style
		UITabBarItem.appearance().setTitleTextAttributes([NSForegroundColorAttributeName: UIColor.black], for: .normal)
		UITabBarItem.appearance().setTitleTextAttributes([NSForegroundColorAttributeName: UIColor.primary()], for: .selected)
		UITabBar.appearance().tintColor = UIColor.primary()

		// Setting the default color of the TabBar icons to the original color of the image
		for item in tabBar.items!{
			if let image = item.image {
				item.image = image.withRenderingMode(.alwaysOriginal)
			}
		}
    }
	
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	/**
	Showing an alert
	*/
	public static func presentAlert(message: String){
		let alert = UIAlertController(title: message, message: nil, preferredStyle: .alert)
		let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
		alert.addAction(okAction)
		
		selfish.present(alert, animated: true, completion: nil)

	}
	
	override func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
		NotificationCenter.default.post(name: Notification.Name(Bundle.main.bundleIdentifier! + "segueBack"), object: self)
	}
}
