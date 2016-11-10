import UIKit

class CalendarView: UIView, UICollectionViewDataSource, UICollectionViewDelegate{

    var calendar: NSCalendar!
    var date: NSDate!
    
    var daysShown = [Date]()
    
    
    var left: UIButton!
    var right: UIButton!
    var month: UILabel!
    
    
    var dayGrid: UICollectionView!
    
    
    
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        date = NSDate()
        calendar = Calendar.autoupdatingCurrent as NSCalendar!
        calendar.firstWeekday = 2
        
        updateCalendar()
        
        backgroundColor = tintColor
        
        left = UIButton()
        left.translatesAutoresizingMaskIntoConstraints = false
        left.setImage(#imageLiteral(resourceName: "ic_arrow_back"), for: UIControlState.normal)
        left.addTarget(self, action: "buttonLeftClicked", for: .touchUpInside)
        
        right = UIButton()
        right.translatesAutoresizingMaskIntoConstraints = false
        right.setImage(#imageLiteral(resourceName: "ic_arrow_forward"), for: UIControlState.normal)
        right.addTarget(self, action: "buttonRightClicked", for: .touchUpInside)
        
        month = UILabel()
        month.translatesAutoresizingMaskIntoConstraints = false
        
        
        
        let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
        layout.sectionInset = UIEdgeInsets(top: 5, left: 5, bottom: 5, right: 5)
        let dia = (UIScreen.main.bounds.width-5-5 - (7-1))/7
        layout.itemSize = CGSize(width: dia, height: dia)
        layout.minimumInteritemSpacing = 1
        layout.minimumLineSpacing = layout.minimumInteritemSpacing
        layout.headerReferenceSize = CGSize(width: UIScreen.main.bounds.width, height: (UIScreen.main.bounds.width-5-5 - (7-1))/7)
        
        dayGrid = UICollectionView(frame: CGRect(), collectionViewLayout: layout)
        dayGrid.translatesAutoresizingMaskIntoConstraints = false
        dayGrid.dataSource = self
        dayGrid.delegate = self
        dayGrid.register(CalendarGridHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "Header")
        dayGrid.register(CalendarGridCell.self, forCellWithReuseIdentifier: "Cell")

        updateViews()
        
        addSubview(left)
        addSubview(right)
        addSubview(month)
        addSubview(dayGrid)
        
        let leftButtonX = NSLayoutConstraint(item: left, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let leftButtonY = NSLayoutConstraint(item: left, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([leftButtonX, leftButtonY])
        
        let rightButtonX = NSLayoutConstraint(item: right, attribute: NSLayoutAttribute.trailing, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.trailing, multiplier: 1, constant: 0)
        let rightButtonY = NSLayoutConstraint(item: right, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([rightButtonX, rightButtonY])
        
        let monthLabelX = NSLayoutConstraint(item: month, attribute: NSLayoutAttribute.centerX, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.centerX, multiplier: 1, constant: 0)
        let monthLabelY = NSLayoutConstraint(item: month, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([monthLabelX, monthLabelY])
        
        let views = ["dayGrid": dayGrid]
        let widthConstraints = NSLayoutConstraint.constraints(withVisualFormat: "H:[dayGrid(\(UIScreen.main.bounds.width))]", options: NSLayoutFormatOptions(rawValue: 0), metrics: nil, views: views)
        NSLayoutConstraint.activate(widthConstraints)

        let dayGridX = NSLayoutConstraint(item: dayGrid, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let dayGridY = NSLayoutConstraint(item: dayGrid, attribute: NSLayoutAttribute.bottom, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([dayGridX, dayGridY])
        
        let dayGridSpacingY = NSLayoutConstraint(item: dayGrid, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: left, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 50)
        NSLayoutConstraint.activate([dayGridSpacingY])
        
        
        


    }
    
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func buttonLeftClicked(){
        date = calendar.date(byAdding: [.month], value: -1, to: date as Date, options: []) as NSDate!
        updateCalendar()
        updateViews()
    }
    
    func buttonRightClicked(){
        date = calendar.date(byAdding: [.month], value: 1, to: date as Date, options: []) as NSDate!
        updateCalendar()
        updateViews()
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 42
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "Cell", for: indexPath) as! CalendarGridCell
        
        let dateDate: Date = daysShown[indexPath.item]
        
        let labelText = calendar.components([.day], from: dateDate).day!
        
        cell.numberLabel.text = "\(labelText)"
        
        print(labelText)

        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView,viewForSupplementaryElementOfKind kind: String,at indexPath: IndexPath) -> UICollectionReusableView {
        switch kind {
            case UICollectionElementKindSectionHeader:
                let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind,withReuseIdentifier: "Header",for: indexPath) as! CalendarGridHeader

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
        
        let monthBeginningCell = calendar.components([.weekday], from: dateTemp as! Date).weekday! - 1
        
        dateComponents.day = -monthBeginningCell

        var dateBegin = calendar.date(byAdding: [.day], value: -monthBeginningCell, to: dateTemp as! Date, options: [])
        
        while daysShown.count <= 42 {
            dateBegin = calendar.date(byAdding: [.day], value: 1, to: dateBegin!, options: [])
            
            daysShown.append(dateBegin!)
        }
        
    }
    
    func updateViews(){
        let monthInt = calendar.components([.month], from: date as Date).month!
        let yearInt = calendar.components([.year], from: date as Date).year!
        month.text = calendar.monthSymbols[monthInt - 1] + " \(yearInt)"
        
        dayGrid.reloadData()
    }
}
