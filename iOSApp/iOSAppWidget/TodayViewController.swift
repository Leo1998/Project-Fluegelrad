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

	/**
	label which is shown if error or no events
	*/
	private var noEventsLabel: UILabel!
	
    override func viewDidLoad() {
        super.viewDidLoad()

		_ = Storage()
		
		noEventsLabel = UILabel()
		noEventsLabel.adjustsFontSizeToFitWidth = true
		view.addSubview(noEventsLabel)
		noEventsLabel.translatesAutoresizingMaskIntoConstraints = false
		noEventsLabel.addConstraintsXY(xView: view, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
		noEventsLabel.isHidden = true
		
		eventTable = UITableView()
		eventTable.register(TodayViewCell.self, forCellReuseIdentifier: "cell")
		eventTable.estimatedRowHeight = 50
		eventTable.separatorInset = UIEdgeInsets.zero
		view.addSubview(eventTable)
		eventTable.translatesAutoresizingMaskIntoConstraints = false
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

		reset()
		
		eventTable.delegate = self
		eventTable.dataSource = self

		
		let title = UILabel(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 25))
		title.text = "Events in den nächsten 24 Stunden"
		title.textAlignment = .center
		title.adjustsFontSizeToFitWidth = true
		
		if #available(iOS 9, *){
			if #available(iOS 10, *) {} else {
				title.textColor = UIColor.lightGray

			}
		}
		
		eventTable.tableHeaderView = title
		
		NotificationCenter.default.addObserver(self, selector: #selector(TodayViewController.showError), name: Notification.Name(Bundle.main.bundleIdentifier! + "downloadError"), object: nil)

		NotificationCenter.default.addObserver(self, selector: #selector(TodayViewController.reset), name: Notification.Name(Bundle.main.bundleIdentifier! + "downloaded"), object: nil)
		
		if #available(iOSApplicationExtension 10.0, *) {
			extensionContext?.widgetLargestAvailableDisplayMode = .expanded
		} else {
			if eventTable.visibleCells.count > 0 {
				preferredContentSize.height = CGFloat(shownEvents.count) * eventTable.visibleCells[0].frame.height + (eventTable.tableHeaderView?.frame.height)!
			}else{
				preferredContentSize.height = CGFloat(shownEvents.count) * 60 + (eventTable.tableHeaderView?.frame.height)!
			}

		}
	}
	
	@available(iOSApplicationExtension 10.0, *)
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
		
		if event.maxParticipants - event.participants == 0 {
			cell.participantsLabel.text = "Keine Plätze mehr frei"
		}else if event.maxParticipants >= 0 {
			cell.participantsLabel.text = "Es sind noch \(event.maxParticipants - event.participants) von \(event.maxParticipants!) Plätzen frei."
		}else if event.participants >= 0{
			cell.participantsLabel.text = "Es sind noch \(event.participants!) Personen angemeldet"
		}else {
			cell.participantsLabel.text = "Es ist keine Anmeldung verfügbar"
		}

		
		return cell
	}
	
	func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
		let url = URL(string: "doaktiv://\(shownEvents[indexPath.item].id!)")!
		
		extensionContext?.open(url, completionHandler: nil)
	}
	
	func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
		return UITableViewAutomaticDimension
	}
	
	/**
	Shows an error message
	*/
	func showError(){
		if allEvents.count == 0 {
			showMessage(message: "Keine Verbindung zum Server")
		}
	}
	
	/**
	Shows a message
	*/
	public func showMessage(message: String){
		eventTable.isHidden = true
		noEventsLabel.isHidden = false
		
		noEventsLabel.text = message
		
		if #available(iOSApplicationExtension 10.0, *) {
			extensionContext?.widgetLargestAvailableDisplayMode = .compact
		} else {
			if eventTable.visibleCells.count > 0 {
				preferredContentSize.height = CGFloat(shownEvents.count) * eventTable.visibleCells[0].frame.height + (eventTable.tableHeaderView?.frame.height)!
			}
		}
	}
	
	/**
	(Re)loads all the events
	*/
	func reset(){
		let events = Storage.getEvents()
		
		if events.count > 0 {
			allEvents = events.sorted(by: {
				(event1, event2) -> Bool in
				
				let date1 = (event1).dateStart
				let date2 = (event2).dateStart
				
				return (date1!.compare(date2!)) == ComparisonResult.orderedAscending
				
			})
		}

		
		let today = Date()
		let tomorrow = Calendar.autoupdatingCurrent.date(byAdding: .day, value: -1, to: today, wrappingComponents: false)
		shownEvents = allEvents.filter(){event in
			return (event).dateStart.compare(today) == ComparisonResult.orderedDescending && (event).dateStart.compare(tomorrow!) == ComparisonResult.orderedAscending
		}
		
		
		eventTable.reloadData()
		
		if shownEvents.count == 0 {
			
			showMessage(message: "Es gibt keine Events in den nächsten 24 Stunden")
		}
	}
}
