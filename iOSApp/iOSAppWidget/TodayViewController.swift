import UIKit
import NotificationCenter

class TodayViewController: UIViewController, NCWidgetProviding, UITableViewDelegate, UITableViewDataSource  {
	
	/**
	Table View for the events
	*/
	private var eventTable: UITableView!
	
	/**
	events which are shown to the user
	*/
	private var shownEvents = [Event]()
	
	/**
	all events
	*/
	private var allEvents = [Event]()


	
    override func viewDidLoad() {
        super.viewDidLoad()
		
		let myDefaults = UserDefaults(suiteName: "group.com.iOSApp")!
		let eventData = myDefaults.object(forKey: "events")
		
		var events = [Event]()
		if eventData != nil {
			events = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
		}
		
		if events.count > 0 {
			allEvents = events.sorted(by: {
				(event1, event2) -> Bool in
				
				let date1 = (event1).dateStart
				let date2 = (event2).dateStart
				
				return (date1!.compare(date2!)) == ComparisonResult.orderedAscending
				
			})
		}
		
		
		eventTable = UITableView()
		eventTable.register(TodayViewCell.self, forCellReuseIdentifier: "cell")
		eventTable.estimatedRowHeight = 50
		eventTable.separatorColor = UIColor.red
		view.addSubview(eventTable)
		eventTable.translatesAutoresizingMaskIntoConstraints = false
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		eventTable.delegate = self
		eventTable.dataSource = self
		
		let title = UILabel(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 25))
		title.text = "Events in den nächsten 24 Stunden"
		title.textAlignment = .center
		title.font = UIFont.boldSystemFont(ofSize: 20)
		
		eventTable.tableHeaderView = title
		
		extensionContext?.widgetLargestAvailableDisplayMode = .expanded
	}
	
	override func viewDidAppear(_ animated: Bool) {
		let today = Date()
		let tomorrow = Calendar.autoupdatingCurrent.date(byAdding: .day, value: -1, to: today, wrappingComponents: false)
		shownEvents = allEvents.filter(){event in
			return (event).dateStart.compare(today) == ComparisonResult.orderedDescending && (event).dateStart.compare(tomorrow!) == ComparisonResult.orderedAscending
		}
		
		eventTable.reloadData()

		if shownEvents.count == 0 {
			eventTable.removeFromSuperview()
			
			let noEventsLabel = UILabel()
			noEventsLabel.text = "Es gibt keine Events in den nächsten 24 Stunden"
			noEventsLabel.adjustsFontSizeToFitWidth = true
			view.addSubview(noEventsLabel)
			noEventsLabel.translatesAutoresizingMaskIntoConstraints = false
			noEventsLabel.addConstraintsXY(xView: view, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
			
			extensionContext?.widgetLargestAvailableDisplayMode = .compact

		}
	}
	
	func widgetActiveDisplayModeDidChange(_ activeDisplayMode: NCWidgetDisplayMode, withMaximumSize maxSize: CGSize) {
		if activeDisplayMode == .compact{
			preferredContentSize = maxSize
		}else{
			if eventTable.visibleCells.count > 0 {
				preferredContentSize.height = CGFloat(shownEvents.count) * eventTable.visibleCells[0].frame.height + (eventTable.tableHeaderView?.frame.height)!
			}else{
				preferredContentSize.height = CGFloat(shownEvents.count) * 60 + (eventTable.tableHeaderView?.frame.height)!
			}
		}
	}
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func widgetPerformUpdate(completionHandler: (@escaping (NCUpdateResult) -> Void)) {
		completionHandler(NCUpdateResult.newData)
    }
	
	func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		return shownEvents.count
	}
	
	func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		let cell:TodayViewCell = eventTable.dequeueReusableCell(withIdentifier: "cell")! as! TodayViewCell
		let event:Event = (shownEvents[indexPath.row])

		cell.nameLabel.text = event.name
		
		let today = Date()
		let calendar = Calendar.autoupdatingCurrent
		let todayComponents = calendar.dateComponents([.day], from: today)
		let dateStartComponents = calendar.dateComponents([.day], from: event.dateStart)

		
		var dateString = "Morgen "
		if todayComponents.day == dateStartComponents.day {
			dateString = "Heute "
		}
		
		let dateFormatter = DateFormatter()
		dateFormatter.dateFormat = "'um' HH:mm"
		
		cell.startTimeLabel.text = dateString + dateFormatter.string(from: event.dateStart)
		
		if event.maxParticipants - event.participants == 0{
			cell.participantsLabel.text = "Keine Plätze mehr frei"
		}else {
			cell.participantsLabel.text = "\(event.maxParticipants - event.participants) von \(event.maxParticipants!) Plätzen frei"
		}

		
		return cell
	}
	
	func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
		let url = URL(string: "doJuSport://\(shownEvents[indexPath.item].id!)")!
		
		extensionContext?.open(url, completionHandler: nil)
	}
	
	func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
		return UITableViewAutomaticDimension
	}
}
