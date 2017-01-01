import UIKit

class HomeViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchResultsUpdating {
	
	private var searchController: UISearchController!
	
	private var eventTable: UITableView!
	private var refreshControl: UIRefreshControl!

	private var filteredEvents = [Event]()
	private var allEvents = [Event]()
	
	private var dayEvent: Event?
	
	private var frame: CGRect!
	
	override func viewDidLoad() {
		super.viewDidLoad()
		
		frame = view.frame
		
		reset()
		
	}
	
	override func didReceiveMemoryWarning() {
		super.didReceiveMemoryWarning()
	}
	
	private func setupEvents(){
		let eventData = UserDefaults.standard.object(forKey: "events")
		let events = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
		
		allEvents = events.sorted(by: {
			(event1, event2) -> Bool in
			
			let date1 = (event1 ).dateStart
			let date2 = (event2 ).dateStart
			
			return (date1!.compare(date2!)) == ComparisonResult.orderedAscending
			
		})
		
		let today = Date()
		for (index, value) in events.enumerated(){
			if (value ).dateStart.compare(today) == ComparisonResult.orderedAscending{
				allEvents.remove(at: index)
			}
		}
	}
	
	
	func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
		if searchController.isActive && searchController.searchBar.text != "" {
			dayEvent = filteredEvents[indexPath.item]
		}else{
			dayEvent = allEvents[indexPath.item]
		}
		performSegue(withIdentifier: "CalendarDayViewController", sender: self)
	}
	
	override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
		if segue.identifier == "CalendarDayViewController" {
			let vc = segue.destination as! CalendarDayViewController
			vc.event = dayEvent
			dayEvent = nil
		}
	}
	
	func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		if searchController.isActive && searchController.searchBar.text != "" {
			return filteredEvents.count
		}
		return allEvents.count
	}
	
	func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		let cell:HomeViewCell = eventTable.dequeueReusableCell(withIdentifier: "cell")! as! HomeViewCell
		
		let event:Event!
		if searchController.isActive && searchController.searchBar.text != "" {
			event = (filteredEvents[indexPath.row] )
		}else{
			event = (allEvents[indexPath.row] )
		}
		
		let dateFormatter = DateFormatter()
		dateFormatter.dateFormat = "EEE dd.MM.YYYY HH:mm"
		
		cell.locationLabel.text = event.location.title
		cell.dateLabel.text = dateFormatter.string(from: event.dateStart)
		cell.hostLabel.text = event.host.name
		
		return cell
	}
	
	internal func refresh(){
		print("Refresh")
		
		MainViewController.refresh()
		
		setupEvents()
		eventTable.reloadData()
		
		refreshControl.endRefreshing()
	}
	
	public func reset(){
		if eventTable != nil {
			eventTable.removeFromSuperview()
		}
		
		
		
		setupEvents()
		
		eventTable = UITableView()
		eventTable.register(HomeViewCell.self, forCellReuseIdentifier: "cell")
		view.addSubview(eventTable)
		eventTable.translatesAutoresizingMaskIntoConstraints = false
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: topLayoutGuide, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		eventTable.delegate = self
		eventTable.dataSource = self

		
		refreshControl = UIRefreshControl()
		eventTable.addSubview(refreshControl)
		refreshControl.addTarget(self, action: #selector(HomeViewController.refresh), for: .valueChanged)
		
		searchController = UISearchController(searchResultsController: nil)
		searchController.searchResultsUpdater = self
		searchController.dimsBackgroundDuringPresentation = false
		definesPresentationContext = true
		eventTable.tableHeaderView = searchController.searchBar
		
		eventTable.reloadData()
	}

	private func filterEvents(searchText: String, scope: String = "All"){
		filteredEvents = allEvents.filter({ event in
			return event.name.lowercased().contains(searchText.lowercased())
		})
		
		eventTable.reloadData()
	}
	
	func updateSearchResults(for searchController: UISearchController) {
		filterEvents(searchText: searchController.searchBar.text!)
	}
}
