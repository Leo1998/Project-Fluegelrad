import UIKit

class CalendarGridViewController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource {

    private var calendarGridView: CalendarGridView!
    
    private var events = [Event]()
    private var shownEvents = [Int: Event]()
    private var daysShown = [Date]()
    
    private var dayEvent: Event?
    
    private var calendar: Calendar!
    private var date: Date!

    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        reset()
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
            let eventDateComponents = calendar.dateComponents([.year, .month, .day], from: (event ).date!)
            
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
            calendarGridView.headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind,withReuseIdentifier: "Header",for: indexPath) as! CalendarGridHeader
            
            updateViews(fromReload: true)
            calendarGridView.headerView.right.addTarget(self, action: #selector(CalendarGridViewController.buttonNextMonthClicked), for: .touchUpInside)
            calendarGridView.headerView.left.addTarget(self, action: #selector(CalendarGridViewController.buttonPrevMonthClicked), for: .touchUpInside)
            
            
            return calendarGridView.headerView
        default:
            assert(false, "Unexpected element kind")
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 42
    }
    
    private func updateCalendar() -> Void {
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
    
    internal func buttonPrevMonthClicked(){
        date = calendar.date(byAdding: .month, value: -1, to: date, wrappingComponents: false)
        updateCalendar()
        updateViews(fromReload: false)
    }
    
    internal func buttonNextMonthClicked(){
        date = calendar.date(byAdding: .month, value: 1, to: date, wrappingComponents: false)
        updateCalendar()
        updateViews(fromReload: false)
    }
    
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if shownEvents[indexPath.item] != nil {
            dayEvent = shownEvents[indexPath.item]
            performSegue(withIdentifier: "CalendarDayViewController", sender: self)
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "CalendarDayViewController" {
            let vc = segue.destination as! CalendarDayViewController
            vc.event = dayEvent
            dayEvent = nil
        }
    }
    
    private func updateViews(fromReload: Bool){
        let monthInt = calendar.dateComponents([.month], from: date).month!
        let yearInt = calendar.dateComponents([.year], from: date).year!
        
        if !fromReload {
            calendarGridView.dayGrid.reloadData()
        }
        
        if calendarGridView.headerView != nil {
            calendarGridView.headerView.month.text = calendar.monthSymbols[monthInt - 1] + " \(yearInt)"
        }
    }

    internal func refresh(){
        print("Refresh")
        
        MainViewController.refresh()
        
        updateCalendar()
        updateViews(fromReload: false)
        
        calendarGridView.refreshControl.endRefreshing()
    }
    
    public func reset(){
        if calendarGridView != nil {
            calendarGridView.removeFromSuperview()
        }
        
        date = Date()
        calendar = Calendar.autoupdatingCurrent
        calendar.firstWeekday = 2
        
        
        let eventData = UserDefaults.standard.object(forKey: "events")
        events = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
        
        calendarGridView = CalendarGridView(frame: view.frame)
        calendarGridView.dayGrid.delegate = self
        calendarGridView.dayGrid.dataSource = self
        
        calendarGridView.refreshControl.addTarget(self, action: #selector(CalendarGridViewController.refresh), for: .valueChanged)
        view.addSubview(calendarGridView)
        
        updateCalendar()
        
        updateViews(fromReload: false)
    }
}
