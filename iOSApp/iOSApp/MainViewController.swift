import UIKit

class MainViewController: UITabBarController, DatabaseManagerProtocol {
    
    private var databaseManager:DatabaseManager!
    
    private var events = [Event]()
    
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        databaseManager = DatabaseManager()
        databaseManager.delegate = self
        databaseManager.downloadItems()
    }
    
    internal func itemsDownloaded(items: [Event]) {
        
        events = items
        
        UserDefaults.standard.set(NSKeyedArchiver.archivedData(withRootObject: items), forKey: "events")
        UserDefaults.standard.synchronize()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

}
