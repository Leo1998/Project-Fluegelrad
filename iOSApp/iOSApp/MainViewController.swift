import UIKit

class MainViewController: UITabBarController, DatabaseManagerProtocol {
    
    private static var databaseManager:DatabaseManager!
    
    private var events = [Event]()
    
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        MainViewController.databaseManager = DatabaseManager()
        MainViewController.databaseManager.delegate = self
        MainViewController.databaseManager.downloadItems()
    }
    
    internal func itemsDownloaded(items: [Event]) {
        
        events = items
        
        UserDefaults.standard.set(NSKeyedArchiver.archivedData(withRootObject: items), forKey: "events")
        UserDefaults.standard.synchronize()
		
		let alert = UIAlertController(title: "Couldn't connect to ther server", message: nil, preferredStyle: .alert)
		let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
		alert.addAction(okAction)
		
		present(alert, animated: true, completion: nil)
		
		NotificationCenter.default.post(name: Notification.Name(Bundle.main.bundleIdentifier!), object: self)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    public static func refresh(){
        databaseManager.downloadItems()
    }
}
