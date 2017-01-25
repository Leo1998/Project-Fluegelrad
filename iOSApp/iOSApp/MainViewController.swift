import UIKit
import EventKit

class MainViewController: UITabBarController, DatabaseManagerProtocol {
	
	/**
	DatabaseManager to download and create all Events
	*/
    private static var databaseManager:DatabaseManager!
	
	/**
	A refference to itself to use in static methods
	*/
	private static var selfish: MainViewController!
	
	/**
	A refference to an event where the user wants to participate
	*/
	private static var eventPart: Event?
	
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }

    override func viewDidLoad() {
        super.viewDidLoad()
		
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
		
		// Initalizing the DatabaseManager
        MainViewController.databaseManager = DatabaseManager()
        MainViewController.databaseManager.delegate = self
        MainViewController.databaseManager.downloadItems()
    }
	
	/**
	Creating a new calendar to show the Events if added
	*/
	public static func createEvent(eventStore: EKEventStore){
		var created = false
		
		// Check if calendar already created
		let calendarData = UserDefaults.standard.object(forKey: "calendar")
		
		if calendarData != nil {
			let eventCalendarIdentifier = NSKeyedUnarchiver.unarchiveObject(with: calendarData as! Data) as! String
			let eventCalendar = eventStore.calendar(withIdentifier: eventCalendarIdentifier)
			
			if eventCalendar != nil {
				created =  true
			}
		}
		
		// If not created create new
		if !created {
			let eventCalendar = EKCalendar(for: .event, eventStore: eventStore)
			eventCalendar.title = "Dortmunder Events"
			eventCalendar.cgColor = UIColor.primary().cgColor
			
			for value in eventStore.sources {
				if value.sourceType == .local {
					eventCalendar.source = value
					break
				}
			}
			
			do {
				try eventStore.saveCalendar(eventCalendar, commit: true)
			} catch {
				let alert = UIAlertController(title: "Fehler im Kalendar", message: error.localizedDescription, preferredStyle: .alert)
				let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
				alert.addAction(okAction)
				
				selfish.present(alert, animated: true, completion: nil)
			}
			
			
			// Save calendar identifier
			UserDefaults.standard.set(NSKeyedArchiver.archivedData(withRootObject: eventCalendar.calendarIdentifier), forKey: "calendar")
			UserDefaults.standard.synchronize()
		}

	}
	
	/**
	Saving the events and sponsors
	*/
	internal func itemsDownloaded(events: [Event], sponsors: [Int: Sponsor]) {
		
		UserDefaults.standard.set(NSKeyedArchiver.archivedData(withRootObject: sponsors), forKey: "sponsors")
        UserDefaults.standard.synchronize()
		
		let myDefaults = UserDefaults(suiteName: "group.com.iOSApp")!
		myDefaults.set(NSKeyedArchiver.archivedData(withRootObject: events), forKey: "events")
		
		// Sending a message to all ViewControllers to update their data
		NotificationCenter.default.post(name: Notification.Name(Bundle.main.bundleIdentifier! + "downloaded"), object: self)
    }
	
	/**
	Displaying an error message if there is an error with the database
	*/
	internal func error(){
		let alert = UIAlertController(title: "Keine Verbindung zum Server", message: nil, preferredStyle: .alert)
		let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
		alert.addAction(okAction)
		
		present(alert, animated: true, completion: nil)
	}
	
	/**
	Displaying a message if participation to an event was successful
	*/
	internal func participation(status: ParticipationStatus){
		CalendarDayViewController.participation(status: status, event: MainViewController.eventPart!)
	}

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	/**
	Forwarding the participation to the DatabaseManager
	*/
	public static func participate(event: Event){
		eventPart = event
		databaseManager.participate(event: event)
	}

	/**
	Forwarding the refresh to the DatabaseManager
	*/
    public static func refresh(){
        databaseManager.downloadItems()
    }
	
	override func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
		NotificationCenter.default.post(name: Notification.Name(Bundle.main.bundleIdentifier! + "segueBack"), object: self)
	}
}
