import UIKit

class MainViewController: UITabBarController, DatabaseManagerProtocol {
    
    var databaseManager:DatabaseManager!
    
    var  events: NSArray = NSArray()
    var selectedEvent: Event = Event()
    
    
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        databaseManager = DatabaseManager()
        databaseManager.delegate = self
        databaseManager.downloadItems()
    }
    
    func itemsDownloaded(items: NSArray) {
        events = items
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

}
