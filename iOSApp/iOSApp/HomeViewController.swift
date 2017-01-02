import UIKit

class HomeViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchResultsUpdating, UISearchBarDelegate, UIPickerViewDataSource, UIPickerViewDelegate {
	
	private var searchController: UISearchController!
	
	private var eventTable: UITableView!
	private var refreshControl: UIRefreshControl!

	private var filteredEvents = [Event]()
	private var allEvents = [Event]()
	
	private var dayEvent: Event?
	
	private var frame: CGRect!
	
	private var picker: SortPicker!
	private var sortCategory: SortingCategory!
	
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

		let allTempEnum = Filter.all
		var allTempString = [String]()
		
		for value in allTempEnum{
			allTempString.append(value.rawValue)
		}
		
		searchController.searchBar.scopeButtonTitles = allTempString
		searchController.searchBar.delegate = self
		searchController.searchBar.setImage(#imageLiteral(resourceName: "ic_sort"), for: .bookmark, state: .normal)
		searchController.searchBar.showsBookmarkButton = true
		
		eventTable.tableHeaderView = searchController.searchBar

		eventTable.reloadData()
		
		picker = SortPicker(frame: view.frame)
		picker.picker.dataSource = self
		picker.picker.delegate = self
		picker.isHidden = true
		view.addSubview(picker)
		
		sortCategory = .rating
	}

	private func filterEvents(searchText: String, scope: String){
		filteredEvents = allEvents.filter({ event in
			switch scope{
			case Filter.name.rawValue:
				return event.name.lowercased().contains(searchText.lowercased())
			case Filter.host.rawValue:
				return event.host.name.lowercased().contains(searchText.lowercased())
			case Filter.age.rawValue:
				return event.ageMin <= (Int(searchText) == nil ? Int.max : Int(searchText)!)
			case Filter.free.rawValue:
				return event.price == 0 && event.name.lowercased().contains(searchText.lowercased())
			default:
				return false
			}
			
		})
		
		eventTable.reloadData()
	}
	
	func updateSearchResults(for searchController: UISearchController) {
		let scope = searchController.searchBar.scopeButtonTitles![searchController.searchBar.selectedScopeButtonIndex]
		filterEvents(searchText: searchController.searchBar.text!, scope: scope)
	}
	
	func searchBar(_ searchBar: UISearchBar, selectedScopeButtonIndexDidChange selectedScope: Int) {
		
		let scope = searchBar.scopeButtonTitles![selectedScope]
		if scope == Filter.age.rawValue {
			searchController.searchBar.keyboardType = .numberPad
		}else{
			searchController.searchBar.keyboardType = .default

		}

		searchController.searchBar.reloadInputViews()

		
		filterEvents(searchText: searchBar.text!, scope: searchBar.scopeButtonTitles![selectedScope])
	}

	func searchBarBookmarkButtonClicked(_ searchBar: UISearchBar) {
		picker.isHidden = false
	}
	
	func numberOfComponents(in pickerView: UIPickerView) -> Int {
		return 1
	}
	
	func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
		return SortingCategory.all.count
	}
	
	func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
		return SortingCategory.all[row].rawValue
	}
	
	func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
		picker.isHidden = true
		sortCategory = SortingCategory.all[row]
		
		sort()
	}
	
	private func sort(){
		allEvents.sort(by: {
			(event1, event2) -> Bool in
			
			let sort1: Any
			let sort2: Any
			
			switch sortCategory!{
			case .rating:
				//TODO
				sort1 = (event1).name
				sort2 = (event2).name

				break
			case .alphabetically:
				sort1 = (event1).name
				sort2 = (event2).name
				
				break
			case .chronologically:
				sort1 = (event1).dateStart
				sort2 = (event2).dateStart
				
				break
			case .host:
				sort1 = (event1).host.name
				sort2 = (event2).host.name
				
				break
			}
			
			switch sortCategory!{
			case .rating, .alphabetically, .host:
				return ((sort1 as! String).compare(sort2 as! String)) == ComparisonResult.orderedAscending
			case .chronologically:
				return ((sort1 as! Date).compare(sort2 as! Date)) == ComparisonResult.orderedAscending
			}
			
			
		})
		eventTable.reloadData()

	}

}
