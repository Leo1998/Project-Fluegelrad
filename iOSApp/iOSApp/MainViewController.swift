import UIKit

class MainViewController: UITabBarController, DatabaseManagerProtocol {
    
    private var databaseManager:DatabaseManager!
    
    private var events: NSArray = NSArray()
    
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
        
        let eventsDict = NSMutableArray()
        for event in events {
            eventsDict.add((event as! Event).getDictonary())
        }
        
        
        UserDefaults.standard.set(eventsDict, forKey: "events")
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

}
