import UIKit
import EventKit

class Storage: DatabaseManagerProtocol {
	
	/**
	DatabaseManager to access the database
	*/
	private static var databaseManager: DatabaseManager!
	
	/**
	user which is accessing the databse
	*/
	internal var user: User! {
		get{
			if userSave == nil {
				let userData = Storage.myDefaults.object(forKey: "user")
				
				if userData != nil {
					userSave = NSKeyedUnarchiver.unarchiveObject(with: userData as! Data) as! User

				}
			}
			return userSave
		}
		
		set(user) {
			userSave = user
			
			Storage.myDefaults.set(NSKeyedArchiver.archivedData(withRootObject: userSave), forKey: "user")
		}
	}
	private var userSave: User!
	
	/**
	the user under which the data is saved
	*/
	private static let myDefaults = UserDefaults(suiteName: "group.com.iOSApp")!
		
	init() {
		let databaseManager = DatabaseManager()
		databaseManager.delegate = self
		databaseManager.downloadItems()
		
		Storage.databaseManager = databaseManager
	}
	
	/**
	loads the events from the storage which can be rated
	*/
	static func getEventsRatable() -> [Event] {
		var events = Storage.getEvents()
		let today = Date()
		events = events.filter(){event in
			return (event).dateStart.compare(today) == ComparisonResult.orderedAscending && Storage.isParticipating(event: event)
		}
		return events
	}

	
	/**
	loads the events from the storage
	*/
	static func getEvents() -> [Event] {
		let eventData = myDefaults.object(forKey: "events")
		
		
		var events = [Event]()
		if eventData != nil {
			events = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
		}
		
		return events
	}
	
	/**
	loads the sponsors from the storage
	*/
	static func getSponsors() -> [Int: Sponsor] {
		let sponsorsData = myDefaults.object(forKey: "sponsors")
		
		
		var sponsors = [Int: Sponsor]()
		if sponsorsData != nil {
			sponsors = NSKeyedUnarchiver.unarchiveObject(with: sponsorsData as! Data) as! [Int: Sponsor]
		}
		
		return sponsors

	}
	
	/**
	Forwarding the participation to the DatabaseManager
	*/
	public static func participate(event: Event){
		databaseManager.participate(event: event)
	}
	
	/**
	Forwarding the refresh to the DatabaseManager
	*/
	public static func refresh(){
		databaseManager.downloadItems()
	}

	/**
	Creating a new calendar to show the Events if added
	*/
	private static func createCalendar(eventStore: EKEventStore){
		var created = false
		
		// Check if calendar already created
		let calendarData = myDefaults.object(forKey: "calendar")
		
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
			eventCalendar.title = "DoJuSport Events"
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
				#if app
					MainViewController.presentAlert(message: "Fehler im Kalendar")
				#endif
			}
			
			
			// Save calendar identifier
			myDefaults.set(NSKeyedArchiver.archivedData(withRootObject: eventCalendar.calendarIdentifier), forKey: "calendar")
		}
		
	}

	/**
	checks if event can be localy saved
	*/
	public static func saveEventInCalendar(event: Event){
		let eventStore = EKEventStore()
		
		switch EKEventStore.authorizationStatus(for: .event) {
		case .authorized:
			Storage.saveEventInCalendar(eventStore: eventStore, event: event)
			break
		case .denied:
			print("Calendar Access denied")
			break
		case .notDetermined:
			eventStore.requestAccess(to: .event, completion: { (granted, error) in
				if granted {
					Storage.saveEventInCalendar(eventStore: eventStore, event: event)
					
				}else{
					print("Calendar Access denied")
				}
			})
			break
		default:
			print("Calendar Access default")
		}
	}
	
	/**
	saves the event to the local calendar
	*/
	private static func saveEventInCalendar(eventStore: EKEventStore, event: Event){
		createCalendar(eventStore: eventStore)
		
		let calendarData = myDefaults.object(forKey: "calendar")
		let eventCalendarIdentifier = NSKeyedUnarchiver.unarchiveObject(with: calendarData as! Data) as! String
		let eventCalendar = eventStore.calendar(withIdentifier: eventCalendarIdentifier)!
		
		var alreadySaved = false
		
		let predicate = eventStore.predicateForEvents(withStart: event.dateStart, end: event.dateEnd, calendars: [eventCalendar])
		let events = eventStore.events(matching: predicate)
		
		for value in events{
			if value.title == event.name && value.location == event.location.title && value.notes == event.descriptionEvent {
				alreadySaved = true
				
				#if app
					MainViewController.presentAlert(message: "Event im Kalendar schon vorhanden")
				#endif
				break
			}
		}
		
		if !alreadySaved {
			let newEvent = EKEvent(eventStore: eventStore)
			newEvent.calendar = eventCalendar
			newEvent.title = event.name
			newEvent.startDate = event.dateStart
			newEvent.endDate = event.dateEnd
			newEvent.location = event.location.title
			newEvent.notes = event.descriptionEvent
			
			do {
				try eventStore.save(newEvent, span: .thisEvent, commit: true)
				
				#if app
					MainViewController.presentAlert(message: "Event gespeichert")
				#endif
			} catch {
				#if app
					MainViewController.presentAlert(message: "Event konnte nicht gespeichert werden")
				#endif
			}
		}
	}

	/**
	downloads the image
	*/
	public static func getImage(path: String) -> UIImage?{
		return databaseManager.getImage(path: path)
	}
	
	/**
	check if user is already participating
	*/
	public static func isParticipating(event: Event) -> Bool{
		let participatingData = myDefaults.object(forKey: "participating")
		
		
		var participating = [Int]()
		if participatingData != nil {
			participating = NSKeyedUnarchiver.unarchiveObject(with: participatingData as! Data) as! [Int]
		}
		
		for value in participating {
			if value == event.id {
				return true
			}
		}
		
		return false
	}
	
	/**
	set user to participating
	*/
	public static func participating(event: Event){
		if !isParticipating(event: event) {
			let participatingData = myDefaults.object(forKey: "participating")
			
			
			var participating = [Int]()
			if participatingData != nil {
				participating = NSKeyedUnarchiver.unarchiveObject(with: participatingData as! Data) as! [Int]
			}

			participating.append(event.id)
			
			myDefaults.set(NSKeyedArchiver.archivedData(withRootObject: participating), forKey: "participating")

		}
	}
	
	
	/**
	Saving the events and sponsors
	*/
	internal func itemsDownloaded(events: [Event], sponsors: [Int: Sponsor]) {
		
		let myDefaults = UserDefaults(suiteName: "group.com.iOSApp")!
		myDefaults.set(NSKeyedArchiver.archivedData(withRootObject: events), forKey: "events")
		myDefaults.set(NSKeyedArchiver.archivedData(withRootObject: sponsors), forKey: "sponsors")
		
		myDefaults.synchronize()
		
		// Sending a message to all ViewControllers to update their data
		NotificationCenter.default.post(name: Notification.Name(Bundle.main.bundleIdentifier! + "downloaded"), object: self)
	}
	
	/**
	Displaying an error message if there is an error with the database
	*/
	internal func error(){
		#if app
			MainViewController.presentAlert(message: "Keine Verbindung zum Server")
		#elseif widget
			NotificationCenter.default.post(name: Notification.Name(Bundle.main.bundleIdentifier! + "downloadError"), object: self)
		#endif
	}

	/**
	Displaying a message if participation to an event was successful
	*/
	internal func participation(status: ParticipationStatus){
		NotificationCenter.default.post(name: Notification.Name(Bundle.main.bundleIdentifier! + "participation"), object: status)
	}
}
