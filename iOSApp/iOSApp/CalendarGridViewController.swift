import UIKit

class CalendarGridViewController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource, UITableViewDelegate, UITableViewDataSource {

	/**
	GridView where the calendar is shown
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
	Fotter of the grid where the events for the chosen day are ahown
	*/
	private var footerView: CalendarGridFooter!
	
	/**
	All events
	*/
    private var events = [Event]()
	
	/**
	All events from the clicked day
	*/
	private var eventsDay = [Event]()
	
	/**
	Shown events from the current shown month and some of the month before and after
	*/
    private var shownEvents = [Int: [Event]]()
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
		layout.footerReferenceSize = CGSize(width: view.frame.width, height: view.frame.height - (view.frame.height / 3.5 * 2))

		dayGrid = UICollectionView(frame: CGRect(), collectionViewLayout: layout)
		dayGrid.translatesAutoresizingMaskIntoConstraints = false
		dayGrid.register(CalendarGridHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "Header")
		dayGrid.register(CalendarGridFooter.self, forSupplementaryViewOfKind: UICollectionElementKindSectionFooter, withReuseIdentifier: "Footer")
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
		
		let today = Date()
		let cellDateTodayComponents = calendar.dateComponents([.year, .month, .day], from: today)

		eventsDay = events.filter({	(event) in
			let cellDateEventComponents = calendar.dateComponents([.year, .month, .day], from: event.dateStart)
			
			if cellDateTodayComponents.year == cellDateEventComponents.year && cellDateTodayComponents.month == cellDateEventComponents.month && cellDateTodayComponents.day ==		cellDateEventComponents.day {
				return true
			}else{
				return false
			}
		})
		
		refreshControl.addTarget(self, action: #selector(CalendarGridViewController.refresh), for: .valueChanged)
		
		
		updateViews(fromReload: false)
		
		NotificationCenter.default.addObserver(self, selector: #selector(CalendarGridViewController.reset), name: Notification.Name(Bundle.main.bundleIdentifier! + "downloaded"), object: nil)
	}
	
	override func viewWillAppear(_ animated: Bool) {
		navigationController?.setNavigationBarHidden(true, animated: false)
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
		
		var eventsTemp = [Event]()
        for event in events{
            let eventDateComponents = calendar.dateComponents([.year, .month, .day], from: (event ).dateStart!)
            
            if cellDateComponents.year == eventDateComponents.year && cellDateComponents.month == eventDateComponents.month && cellDateComponents.day == eventDateComponents.day {
                cell.numberLabel.backgroundColor =  UIColor(red: 224/255, green: 58/255, blue: 188/255, alpha: 255/255)

				eventsTemp.append(event)
            }
        }
		shownEvents[indexPath.item] = eventsTemp
		
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
		case UICollectionElementKindSectionFooter:
			footerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind,withReuseIdentifier: "Footer",for: indexPath) as! CalendarGridFooter
			
			footerView.dayList.delegate = self
			footerView.dayList.dataSource = self
			
			let dateFormatter = DateFormatter()
			dateFormatter.dateFormat = "EEE dd.MM.YYYY"
			if eventsDay.count == 1 {
				footerView.dayLabel.text = "Events vom \(dateFormatter.string(from: Date()))"
			}else{
				footerView.dayLabel.text = "Keine Events an diesem Tag"
			}

			
			return footerView
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
        
        var dateTemp = Date()
        
        var dateComponents = calendar.dateComponents([.year, .month], from: dateTemp )
        dateComponents.day = 1
        
        dateTemp = calendar.date(from: dateComponents)!
        
        var monthBeginningCell = 7
		if calendar.dateComponents([.weekday], from: dateTemp ).weekday! != 1 {
			monthBeginningCell = calendar.dateComponents([.weekday], from: dateTemp ).weekday! - 1
		}
		
		
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
            eventsDay = shownEvents[indexPath.item]!
			
			let dateFormatter = DateFormatter()
			dateFormatter.dateFormat = "EEE dd.MM.YYYY"
			
			if eventsDay.count >= 1 {
				footerView.dayLabel.text = "Events vom \(dateFormatter.string(from: eventsDay[0].dateStart))"
			}else{
				footerView.dayLabel.text = "Keine Events an diesem Tag"
			}
			
			footerView.dayList.reloadData()
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
        
        Storage.refresh()
        
        refreshControl.endRefreshing()
    }
	
	/**
	Gets all events and sponsors
	*/
	private func setupEvents(){
		sponsors = Storage.getSponsors()
		
		date = Date()
		calendar = Calendar.autoupdatingCurrent
		calendar.firstWeekday = 2
		
		
		events = Storage.getEvents()
		
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
	
	func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
		dayEvent = eventsDay[indexPath.item]
		performSegue(withIdentifier: "CalendarDayViewController", sender: self)
	}

	func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		return eventsDay.count
	}
	
	func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		let cell:CalendarListViewCell = footerView.dayList.dequeueReusableCell(withIdentifier: "cell")! as! CalendarListViewCell
		
		cell.selectionStyle = .none
		cell.separatorInset = UIEdgeInsetsMake(0, 8, 0, 8)
		
		let	event = (eventsDay[indexPath.row] )
		
		var imageTemp = sponsors[event.hostId]?.image
		
		if imageTemp != nil {
			let size = CGSize(width: (imageTemp?.size.width)! * ((UIScreen.main.bounds.height/10) / (imageTemp?.size.height)!), height: UIScreen.main.bounds.height/10)
			
			UIGraphicsBeginImageContext(size)
			imageTemp?.draw(in: CGRect(origin: .zero, size: size))
			
			imageTemp = UIGraphicsGetImageFromCurrentImageContext()!
			UIGraphicsEndImageContext()
			
			cell.imageV.image = imageTemp
			cell.imageV.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: (imageTemp?.size.width)!, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: (imageTemp?.size.height)!)
		}
		
		
		cell.nameLabel.text = event.name
		cell.nameLabel.addConstraintsXY(xView: cell.imageV, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 10)
		cell.nameLabel.addConstraintsXY(xView: cell, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 10)

		
		
		let dateFormatter = DateFormatter()
		dateFormatter.dateFormat = "EEE dd.MM.YYYY 'um' HH:mm"
		
		cell.dateLabel.text = "am \(dateFormatter.string(from: event.dateStart)) Uhr"
		cell.dateLabel.addConstraintsXY(xView: cell.imageV, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		cell.dateLabel.addConstraintsXY(xView: cell, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		return cell
	}
}
