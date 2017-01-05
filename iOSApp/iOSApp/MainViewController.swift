import UIKit
import EventKit

class MainViewController: UITabBarController, DatabaseManagerProtocol {
    
    private static var databaseManager:DatabaseManager!
	private static var selfish: MainViewController!
    
    private var events = [Event]()
    
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }

    override func viewDidLoad() {
        super.viewDidLoad()
		
		MainViewController.selfish = self
		
		let eventStore = EKEventStore()
		
		switch EKEventStore.authorizationStatus(for: .event) {
		case .authorized:
			MainViewController.createEvent(eventStore: eventStore)
			break
		case .denied:
			print("Calendar Access denied")
			break
		case .notDetermined:
			eventStore.requestAccess(to: .event, completion: { (granted, error) in
				if granted {
					MainViewController.createEvent(eventStore: eventStore)
				}else{
					print("Calendar Access denied")
				}
			})
			break
		default:
			print("Calendar Access default")
		}
		
		
        MainViewController.databaseManager = DatabaseManager()
        MainViewController.databaseManager.delegate = self
        MainViewController.databaseManager.downloadItems()
    }
	
	public static func createEvent(eventStore: EKEventStore){
		var created = false
		
		let calendarData = UserDefaults.standard.object(forKey: "calendar")
		
		if calendarData != nil {
			let eventCalendarIdentifier = NSKeyedUnarchiver.unarchiveObject(with: calendarData as! Data) as! String
			let eventCalendar = eventStore.calendar(withIdentifier: eventCalendarIdentifier)
			
			if eventCalendar != nil {
				created =  true
			}
		}
		
		if !created {
			let eventCalendar = EKCalendar(for: .event, eventStore: eventStore)
			eventCalendar.title = "Dortmunder Events"
			eventCalendar.cgColor = UIColor.green.cgColor
			
			for value in eventStore.sources {
				if value.sourceType == .local {
					eventCalendar.source = value
					break
				}
			}
			
			do {
				try eventStore.saveCalendar(eventCalendar, commit: true)
			} catch {
				let alert = UIAlertController(title: "Calendar couldn't be saved", message: error.localizedDescription, preferredStyle: .alert)
				let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
				alert.addAction(okAction)
				
				selfish.present(alert, animated: true, completion: nil)
			}
			
			UserDefaults.standard.set(NSKeyedArchiver.archivedData(withRootObject: eventCalendar.calendarIdentifier), forKey: "calendar")
			UserDefaults.standard.synchronize()
		}
	}
	
    internal func itemsDownloaded(items: [Event]) {
        
        events = items
        
        UserDefaults.standard.set(NSKeyedArchiver.archivedData(withRootObject: items), forKey: "events")
        UserDefaults.standard.synchronize()
		
		NotificationCenter.default.post(name: Notification.Name(Bundle.main.bundleIdentifier!), object: self)
    }
	
	internal func error(){
		let alert = UIAlertController(title: "Couldn't connect to the server", message: nil, preferredStyle: .alert)
		let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
		alert.addAction(okAction)
		
		present(alert, animated: true, completion: nil)
	}

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    public static func refresh(){
        databaseManager.downloadItems()
    }
}
