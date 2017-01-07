import UIKit

class CalendarListViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchResultsUpdating, UISearchBarDelegate, UIPickerViewDataSource, UIPickerViewDelegate {
	
	private var searchController: UISearchController!
	
	private var eventTable: UITableView!
	private var refreshControl: UIRefreshControl!

	private var filteredEvents = [Event]()
	private var allEvents = [Event]()
	
	private var sponsors = [Int: Sponsor]()
	
	private var dayEvent: Event?
	
	private var picker: SortPicker!
	private var sortCategory: SortingCategory!
	
	override func viewDidLoad() {
		super.viewDidLoad()
		
		setupEvents()
		
		eventTable = UITableView()
		eventTable.register(CalendarListViewCell.self, forCellReuseIdentifier: "cell")
		eventTable.rowHeight = UIScreen.main.bounds.height / 10
		view.addSubview(eventTable)
		eventTable.separatorColor = UIColor.primary()
		eventTable.translatesAutoresizingMaskIntoConstraints = false
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: topLayoutGuide, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		eventTable.delegate = self
		eventTable.dataSource = self
		eventTable.backgroundColor = UIColor.clear
		
		
		refreshControl = UIRefreshControl()
		eventTable.addSubview(refreshControl)
		refreshControl.addTarget(self, action: #selector(CalendarListViewController.refresh), for: .valueChanged)
		
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
		searchController.searchBar.barTintColor = UIColor.primary()
		searchController.searchBar.tintColor = UIColor.accent()
		
		eventTable.tableHeaderView = searchController.searchBar
		
		eventTable.reloadData()
		
		picker = SortPicker(frame: UIScreen.main.bounds)
		picker.picker.dataSource = self
		picker.picker.delegate = self
		picker.isHidden = true
		view.addSubview(picker)
		
		let gesture = UITapGestureRecognizer(target: self, action: #selector(CalendarListViewController.exitSort))
		picker.dimView.addGestureRecognizer(gesture)
		
		sortCategory = .rating

		
		NotificationCenter.default.addObserver(self, selector: #selector(CalendarListViewController.reset), name: Notification.Name(Bundle.main.bundleIdentifier!), object: nil)
	}
	
	override func didReceiveMemoryWarning() {
		super.didReceiveMemoryWarning()
	}
	
	private func setupEvents(){
		let sponsorData = UserDefaults.standard.object(forKey: "sponsors")
		
		if sponsorData != nil {
			sponsors = NSKeyedUnarchiver.unarchiveObject(with: sponsorData as! Data) as! [Int: Sponsor]
		}
		
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
			vc.sponsors = sponsors
		}
	}
	
	func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		if searchController.isActive && searchController.searchBar.text != "" {
			return filteredEvents.count
		}
		return allEvents.count
	}
	
	func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		let cell:CalendarListViewCell = eventTable.dequeueReusableCell(withIdentifier: "cell")! as! CalendarListViewCell
		
		let event:Event!
		if searchController.isActive && searchController.searchBar.text != "" {
			event = (filteredEvents[indexPath.row] )
		}else{
			event = (allEvents[indexPath.row] )
		}
		
		var imageTemp = sponsors[event.hostId]?.image
			
		let size = CGSize(width: (imageTemp?.size.width)! * ((UIScreen.main.bounds.height/10) / (imageTemp?.size.height)!), height: UIScreen.main.bounds.height/10)
		
		UIGraphicsBeginImageContext(size)
		imageTemp?.draw(in: CGRect(origin: .zero, size: size))
			
		imageTemp = UIGraphicsGetImageFromCurrentImageContext()!
		UIGraphicsEndImageContext()
		
		cell.imageV.image = imageTemp
		cell.imageV.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: (imageTemp?.size.width)!, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: (imageTemp?.size.height)!)
		
		cell.nameLabel.text = event.name
		cell.nameLabel.addConstraintsXY(xView: cell.imageV, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)

		
		let dateFormatter = DateFormatter()
		dateFormatter.dateFormat = "EEE dd.MM.YYYY HH:mm"
		
		cell.dateLabel.text = dateFormatter.string(from: event.dateStart)
		cell.dateLabel.addConstraintsXY(xView: cell.imageV, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
	
		
		return cell
	}
	
	internal func refresh(){
		print("Refresh")
		
		MainViewController.refresh()
		
		refreshControl.endRefreshing()
	}
	
	public func reset(){
		setupEvents()
		
		DispatchQueue.main.sync {
			eventTable.reloadData()
		}
	}

	private func filterEvents(searchText: String, scope: String){
		filteredEvents = allEvents.filter({ event in
			switch scope{
			case Filter.name.rawValue:
				return event.name.lowercased().contains(searchText.lowercased())
			case Filter.host.rawValue:
				return sponsors[event.hostId]!.name.lowercased().contains(searchText.lowercased())
			case Filter.age.rawValue:
				return event.ageMin <= (Int(searchText) == nil ? Int.max : Int(searchText)!) && event.ageMax >= (Int(searchText) == nil ? Int.min : Int(searchText)!)
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
				sort1 = sponsors[event1.hostId]!.name
				sort2 = sponsors[event2.hostId]!.name
				
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

	func exitSort(){
		picker.isHidden = true
	}
}
