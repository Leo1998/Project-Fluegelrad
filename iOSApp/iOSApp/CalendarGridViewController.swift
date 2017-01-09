import UIKit

class CalendarGridViewController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource {

	/**
	GridView ehere the calendar is shown
	*/
	private var dayGrid: UICollectionView!
	
	/**
	Pull to refresh control
	*/
	private var refreshControl: UIRefreshControl!
	
	/**
	Header of the grid where the weekdays are ahown
	*/
	private var headerView: CalendarGridHeader!
	
	/**
	All events
	*/
    private var events = [Event]()
	
	/**
	Shown events from the current shown month and some of the month before and after
	*/
    private var shownEvents = [Int: Event]()
	/**
	Shown days from the current shown month and some of the month before and after
	*/
    private var daysShown = [Date]()
	
	/**
	temporal reference to an evnet to pass it to the CalendarDayViewController on tap
	*/
    private var dayEvent: Event?
	
	/**
	All the sponosrs
	*/
	private var sponsors = [Int: Sponsor]()
	
	/**
	Calendar object
	*/
    private var calendar: Calendar!
	
	/**
	a date of the shown month
	*/
    private var date: Date!

    
    override func viewDidLoad() {
        super.viewDidLoad()
		
		setupEvents()
		
		let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
		layout.sectionInset = UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
		let dia = (view.frame.width-8-8 - (7-1))/7
		layout.itemSize = CGSize(width: dia, height: dia)
		layout.minimumInteritemSpacing = 1
		layout.minimumLineSpacing = layout.minimumInteritemSpacing
		layout.headerReferenceSize = CGSize(width: view.frame.width, height: 65)

		dayGrid = UICollectionView(frame: CGRect(), collectionViewLayout: layout)
		dayGrid.translatesAutoresizingMaskIntoConstraints = false
		dayGrid.register(CalendarGridHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "Header")
		dayGrid.register(CalendarGridCell.self, forCellWithReuseIdentifier: "Cell")
		dayGrid.backgroundColor = UIColor.clear
		view.addSubview(dayGrid)
		dayGrid.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: topLayoutGuide, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		dayGrid.addConstraintsXY(xView: view, xSelfAttribute: .width, xViewAttribute: .width, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .height, yViewAttribute: .height, yMultiplier: 1, yConstant: 0)
		
		refreshControl = UIRefreshControl()
		dayGrid.addSubview(refreshControl)
		dayGrid.alwaysBounceVertical = true
		
		
		dayGrid.delegate = self
		dayGrid.dataSource = self
		
		refreshControl.addTarget(self, action: #selector(CalendarGridViewController.refresh), for: .valueChanged)
		
		updateViews(fromReload: false)
		
		NotificationCenter.default.addObserver(self, selector: #selector(CalendarGridViewController.reset), name: Notification.Name(Bundle.main.bundleIdentifier!), object: nil)
	}

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "Cell", for: indexPath) as! CalendarGridCell
        
        cell.numberLabel.textColor = UIColor.black
        cell.numberLabel.backgroundColor = UIColor.clear
        
        let cellDate: Date = daysShown[indexPath.item]
        
        let cellDateComponents = calendar.dateComponents([.year, .month, .day], from: cellDate)
        let todayDateComponents = calendar.dateComponents([.year, .month, .day], from: Date())
        let currentDateComponents = calendar.dateComponents([.year, .month, .day], from: date)
        
        cell.numberLabel.text = "\(Int(cellDateComponents.day!))"
        
        if cellDateComponents.year == todayDateComponents.year && cellDateComponents.month == todayDateComponents.month && cellDateComponents.day == todayDateComponents.day {
            cell.numberLabel.textColor = UIColor.blue
        }else if cellDateComponents.month != currentDateComponents.month{
            cell.numberLabel.textColor = UIColor.gray
        }
        
        for event in events{
            let eventDateComponents = calendar.dateComponents([.year, .month, .day], from: (event ).dateStart!)
            
            if cellDateComponents.year == eventDateComponents.year && cellDateComponents.month == eventDateComponents.month && cellDateComponents.day == eventDateComponents.day {
                cell.numberLabel.backgroundColor = UIColor.red
                
                shownEvents[indexPath.item] = event
                
                break
            }
        }
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView,viewForSupplementaryElementOfKind kind: String,at indexPath: IndexPath) -> UICollectionReusableView {
        switch kind {
        case UICollectionElementKindSectionHeader:
            headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind,withReuseIdentifier: "Header",for: indexPath) as! CalendarGridHeader
            
            updateViews(fromReload: false)
            headerView.right.addTarget(self, action: #selector(CalendarGridViewController.buttonNextMonthClicked), for: .touchUpInside)
            headerView.left.addTarget(self, action: #selector(CalendarGridViewController.buttonPrevMonthClicked), for: .touchUpInside)
            
            
            return headerView
        default:
            assert(false, "Unexpected element kind")
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 42
    }
	
	/**
	updates the calendar to show the correct month dates and events
	*/
    private func updateCalendar() {
        daysShown.removeAll()
        
        var dateTemp = Date(timeIntervalSince1970: date.timeIntervalSince1970)
        
        var dateComponents = calendar.dateComponents([.era, .year, .month], from: dateTemp )
        dateComponents.day = 1
        
        dateTemp = calendar.date(from: dateComponents)!
        
        let monthBeginningCell = calendar.dateComponents([.weekday], from: dateTemp ).weekday! == 1 ? 7 : calendar.dateComponents([.weekday], from: dateTemp ).weekday! - 1
        
        dateComponents.day = -monthBeginningCell
        
        var dateBegin = calendar.date(byAdding: .day, value: -monthBeginningCell, to: dateTemp, wrappingComponents: false)
        
        while daysShown.count <= 42 {
            dateBegin = calendar.date(byAdding: .day, value: 1, to: dateBegin!, wrappingComponents: false)
            
            daysShown.append(dateBegin!)
        }
        
    }
	
	/**
	shows the previous month
	*/
    internal func buttonPrevMonthClicked(){
        date = calendar.date(byAdding: .month, value: -1, to: date, wrappingComponents: false)
        updateCalendar()
        updateViews(fromReload: true)
    }
	
	/**
	shows the next month
	*/
    internal func buttonNextMonthClicked(){
        date = calendar.date(byAdding: .month, value: 1, to: date, wrappingComponents: false)
        updateCalendar()
        updateViews(fromReload: true)
    }
    
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if shownEvents[indexPath.item] != nil {
            dayEvent = shownEvents[indexPath.item]
            performSegue(withIdentifier: "CalendarDayViewController", sender: self)
        }
    }

	/**
	sets the events and the sponsors of the CalendarDayViewController
	*/
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "CalendarDayViewController" {
            let vc = segue.destination as! CalendarDayViewController
            vc.event = dayEvent
            dayEvent = nil
			vc.sponsors = sponsors
        }
    }
	
	/**
	updates the views to show the correct dates, moth and events
	*/
    private func updateViews(fromReload: Bool){
        let monthInt = calendar.dateComponents([.month], from: date).month!
        let yearInt = calendar.dateComponents([.year], from: date).year!
        
        if fromReload {
            dayGrid.reloadData()
        }
        
        if headerView != nil {
            headerView.month.text = calendar.monthSymbols[monthInt - 1] + " \(yearInt)"
        }
    }

	/**
	Forwarding the refresh to the MainViewController
	*/
    internal func refresh(){
        print("Refresh")
        
        MainViewController.refresh()
        
        refreshControl.endRefreshing()
    }
	
	/**
	Gets all events and sponsors
	*/
	private func setupEvents(){
		let sponsorData = UserDefaults.standard.object(forKey: "sponsors")
		
		if sponsorData != nil {
			sponsors = NSKeyedUnarchiver.unarchiveObject(with: sponsorData as! Data) as! [Int: Sponsor]
		}
		
		date = Date()
		calendar = Calendar.autoupdatingCurrent
		calendar.firstWeekday = 2
		
		
		let eventData = UserDefaults.standard.object(forKey: "events")
		events = [Event]()
		if eventData != nil {
			events = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
		}
		
		updateCalendar()
	}
	
	/**
	(Re)loads all the events and sponsors
	*/
    public func reset(){
		setupEvents()
		
		DispatchQueue.main.sync {
			updateViews(fromReload: true)
		}
    }

}
