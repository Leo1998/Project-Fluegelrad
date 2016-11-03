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
        
        updateCalendar()
        
        autoresizesSubviews = false
        
        backgroundColor = tintColor
        
        left = UIButton(frame: CGRect(x: 0, y: 0, width: 50, height: 50))
        left.setImage(#imageLiteral(resourceName: "ic_arrow_back"), for: UIControlState.normal)
        //left.frame = CGRect(x: 0, y: 0, width: left.frame.width, height: left.frame.height)
        
        right = UIButton(frame: CGRect(x: frame.width-left.frame.width, y: 0, width: left.frame.width, height: left.frame.height))
        right.setImage(#imageLiteral(resourceName: "ic_arrow_forward"), for: UIControlState.normal)
        //right.frame = CGRect(x: frame.width-right.frame.width, y: 0, width: right.frame.width, height: right.frame.height)

        
        month = UILabel(frame: CGRect(x: left.frame.width * 2, y: 0, width: 200, height: 50))
        let monthInt = calendar.components([.month], from: date as Date).month!
        month.text = calendar.monthSymbols[monthInt - 1]
        
        let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
        layout.sectionInset = UIEdgeInsets(top: 20, left: 10, bottom: 10, right: 10)
        layout.itemSize = CGSize(width: 25, height: 25)
        
        dayGrid = UICollectionView(frame: frame, collectionViewLayout: layout)
        dayGrid.dataSource = self
        dayGrid.delegate = self
        dayGrid.register(CalendarGridCell.self, forCellWithReuseIdentifier: "Cell")

        addSubview(left)
        addSubview(right)
        addSubview(month)
        addSubview(dayGrid)
    }
    
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
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
    
    func updateCalendar() -> Void {
        daysShown.removeAll()
        
        var dateTemp = Date()

        var dateComponents = calendar.components([.era, .year, .month], from: dateTemp)
        dateComponents.day = 1
        
        dateTemp = calendar.date(from: dateComponents)!
        
        let monthBeginningCell = calendar.components([.weekday], from: dateTemp).weekday! - 1
        
        dateComponents.day = -monthBeginningCell

        var dateBegin = calendar.date(byAdding: [.day], value: -monthBeginningCell, to: dateTemp, options: [])
        
        while daysShown.count < 42 {
            dateBegin = calendar.date(byAdding: [.day], value: 1, to: dateBegin!, options: [])
            
            daysShown.append(dateBegin!)
        }
        
    }
}
