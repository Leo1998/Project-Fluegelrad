import UIKit

class CalendarGridView: UIView, UICollectionViewDataSource, UICollectionViewDelegate{

    var calendar: NSCalendar!
    var date: NSDate!
    
    var daysShown = [Date]()
    
    
    var dayGrid: UICollectionView!
    
    var events: NSArray!
    
    var headerView: CalendarGridHeader!
    
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        if let array: NSArray = UserDefaults.standard.object(forKey: "events") as! NSArray?{
            
            let eventsMutable = NSMutableArray()
            for dict in array {
                eventsMutable.add(Event(dict: dict as! NSDictionary))
            }
            
            events = eventsMutable as NSArray
        }
        
        date = NSDate()
        calendar = Calendar.autoupdatingCurrent as NSCalendar!
        calendar.firstWeekday = 2
        
        updateCalendar()
        
        
        let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
        layout.sectionInset = UIEdgeInsets(top: 5, left: 5, bottom: 5, right: 5)
        let dia = (frame.size.width-5-5 - (7-1))/7
        layout.itemSize = CGSize(width: dia, height: dia)
        layout.minimumInteritemSpacing = 1
        layout.minimumLineSpacing = layout.minimumInteritemSpacing
        layout.headerReferenceSize = CGSize(width: frame.size.width, height: (frame.size.width-5-5 - (7-1))/7)
        
        dayGrid = UICollectionView(frame: CGRect(), collectionViewLayout: layout)
        dayGrid.translatesAutoresizingMaskIntoConstraints = false
        dayGrid.dataSource = self
        dayGrid.delegate = self
        dayGrid.register(CalendarGridHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "Header")
        dayGrid.register(CalendarGridCell.self, forCellWithReuseIdentifier: "Cell")
        dayGrid.backgroundColor = UIColor.clear
        
        updateViews(fromReload: false)
        
        addSubview(dayGrid)
        
        let widthConstraints = NSLayoutConstraint(item: dayGrid, attribute: NSLayoutAttribute.width, relatedBy: NSLayoutRelation.equal, toItem: nil, attribute: NSLayoutAttribute.notAnAttribute, multiplier: 1, constant: frame.size.width)
        let heightConstraints = NSLayoutConstraint(item: dayGrid, attribute: NSLayoutAttribute.height, relatedBy: NSLayoutRelation.equal, toItem: nil, attribute: NSLayoutAttribute.notAnAttribute, multiplier: 1, constant: frame.size.height)
        NSLayoutConstraint.activate([widthConstraints, heightConstraints])

        let dayGridX = NSLayoutConstraint(item: dayGrid, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let dayGridY = NSLayoutConstraint(item: dayGrid, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([dayGridX, dayGridY])
    }
    
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func buttonLeftClicked(){
        date = calendar.date(byAdding: [.month], value: -1, to: date as Date, options: []) as NSDate!
        updateCalendar()
        updateViews(fromReload: false)
    }
    
    func buttonRightClicked(){
        date = calendar.date(byAdding: [.month], value: 1, to: date as Date, options: []) as NSDate!
        updateCalendar()
        updateViews(fromReload: false)
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 42
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "Cell", for: indexPath) as! CalendarGridCell
        
        cell.numberLabel.textColor = UIColor.black
        cell.numberLabel.backgroundColor = UIColor.clear
        
        let cellDate: Date = daysShown[indexPath.item]
        
        let cellDateComponents = calendar.components([.year, .month, .day], from: cellDate)
        let todayDateComponents = calendar.components([.year, .month, .day], from: NSDate() as Date)
        let currentDateComponents = calendar.components([.year, .month, .day], from: date as Date)
        
        cell.numberLabel.text = "\(Int(cellDateComponents.day!))"
        
        if cellDateComponents.year == todayDateComponents.year && cellDateComponents.month == todayDateComponents.month && cellDateComponents.day == todayDateComponents.day {
            cell.numberLabel.textColor = UIColor.blue
        }else if cellDateComponents.month != currentDateComponents.month{
            cell.numberLabel.textColor = UIColor.gray
        }
        
        for event in events{
            let eventDateComponents = calendar.components([.year, .month, .day], from: (event as! Event).date!)

            if cellDateComponents.year == eventDateComponents.year && cellDateComponents.month == eventDateComponents.month && cellDateComponents.day == eventDateComponents.day {
                cell.numberLabel.backgroundColor = UIColor.red
                break
            }
        }
        
        
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView,viewForSupplementaryElementOfKind kind: String,at indexPath: IndexPath) -> UICollectionReusableView {
        switch kind {
            case UICollectionElementKindSectionHeader:
                headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind,withReuseIdentifier: "Header",for: indexPath) as! CalendarGridHeader
                
                updateViews(fromReload: true)
                headerView.right.addTarget(self, action: #selector(CalendarGridView.buttonRightClicked), for: .touchUpInside)
                headerView.left.addTarget(self, action: #selector(CalendarGridView.buttonLeftClicked), for: .touchUpInside)

                
                return headerView
            default:
                assert(false, "Unexpected element kind")
        }
    }
    
    func updateCalendar() -> Void {
        daysShown.removeAll()
        
        var dateTemp = date.copy()

        var dateComponents = calendar.components([.era, .year, .month], from: dateTemp as! Date)
        dateComponents.day = 1
        
        dateTemp = calendar.date(from: dateComponents)!
        
        let monthBeginningCell = calendar.components([.weekday], from: dateTemp as! Date).weekday! == 1 ? 7 : calendar.components([.weekday], from: dateTemp as! Date).weekday! - 1
        
        dateComponents.day = -monthBeginningCell

        var dateBegin = calendar.date(byAdding: [.day], value: -monthBeginningCell, to: dateTemp as! Date, options: [])
        
        while daysShown.count <= 42 {
            dateBegin = calendar.date(byAdding: [.day], value: 1, to: dateBegin!, options: [])
            
            daysShown.append(dateBegin!)
        }
        
    }
    
    func updateViews(fromReload: Bool){
        let monthInt = calendar.components([.month], from: date as Date).month!
        let yearInt = calendar.components([.year], from: date as Date).year!
        
        if !fromReload {
            dayGrid.reloadData()
        }
        
        if headerView != nil {
            headerView.month.text = calendar.monthSymbols[monthInt - 1] + " \(yearInt)"
        }
    }
}
