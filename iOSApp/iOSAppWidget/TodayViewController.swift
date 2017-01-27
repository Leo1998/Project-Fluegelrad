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

	
    override func viewDidLoad() {
		
		
        super.viewDidLoad()
		
		let myDefaults = UserDefaults(suiteName: "group.com.iOSApp")!
		let eventData = myDefaults.object(forKey: "events")
		
		if eventData != nil {
			shownEvents = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
		}
		
		eventTable = UITableView()
		eventTable.register(TodayViewCell.self, forCellReuseIdentifier: "cell")
		eventTable.separatorColor = UIColor.primary()
		view.addSubview(eventTable)
		eventTable.translatesAutoresizingMaskIntoConstraints = false
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		eventTable.delegate = self
		eventTable.dataSource = self
		
		let title = UILabel(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 25))
		title.text = "Events in den nächsten 24 Stunden"
		title.textAlignment = .center
		title.font = UIFont.boldSystemFont(ofSize: 16)
		
		eventTable.tableHeaderView = title
		
		extensionContext?.widgetLargestAvailableDisplayMode = .expanded
	}
	
	func widgetActiveDisplayModeDidChange(_ activeDisplayMode: NCWidgetDisplayMode, withMaximumSize maxSize: CGSize) {
		if activeDisplayMode == .compact{
			preferredContentSize = maxSize
		}else{
			if eventTable.visibleCells.count > 0 {
				preferredContentSize.height = CGFloat(shownEvents.count) * eventTable.visibleCells[0].frame.height + (eventTable.tableHeaderView?.frame.height)!
			}else{
				extensionContext?.widgetLargestAvailableDisplayMode = .compact
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
		
		return cell
	}
	
	func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
		let url = URL(string: "doJuSport://\(shownEvents[indexPath.item].id!)")!
		
		extensionContext?.open(url, completionHandler: nil)
	}
}
