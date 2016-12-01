import UIKit

class CalendarGridView: UIView{

    private(set) var dayGrid: UICollectionView!
    
    var headerView: CalendarGridHeader!
    
    var calendar: Calendar!
    var date: Date!
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        date = Date()
        calendar = Calendar.autoupdatingCurrent
        calendar.firstWeekday = 2
 
        setupDayGrid()
        updateViews(fromReload: false)
        setupConstraints()
    }
    
    private func setupDayGrid(){
        let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
        layout.sectionInset = UIEdgeInsets(top: 5, left: 5, bottom: 5, right: 5)
        let dia = (frame.size.width-5-5 - (7-1))/7
        layout.itemSize = CGSize(width: dia, height: dia)
        layout.minimumInteritemSpacing = 1
        layout.minimumLineSpacing = layout.minimumInteritemSpacing
        layout.headerReferenceSize = CGSize(width: frame.size.width, height: (frame.size.width-5-5 - (7-1))/7)
        
        dayGrid = UICollectionView(frame: CGRect(), collectionViewLayout: layout)
        dayGrid.translatesAutoresizingMaskIntoConstraints = false
        dayGrid.register(CalendarGridHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "Header")
        dayGrid.register(CalendarGridCell.self, forCellWithReuseIdentifier: "Cell")
        dayGrid.backgroundColor = UIColor.clear
        
        addSubview(dayGrid)
    }
    
    private func setupConstraints(){
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
    
    internal func updateViews(fromReload: Bool){
        let monthInt = calendar.dateComponents([.month], from: date).month!
        let yearInt = calendar.dateComponents([.year], from: date).year!
        
        if !fromReload {
            dayGrid.reloadData()
        }
        
        if headerView != nil {
            headerView.month.text = calendar.monthSymbols[monthInt - 1] + " \(yearInt)"
        }
    }
}
