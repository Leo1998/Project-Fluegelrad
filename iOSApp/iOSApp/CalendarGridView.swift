import UIKit

class CalendarGridView: UIView{

    private(set) var dayGrid: UICollectionView!
    private(set) var refreshControl: UIRefreshControl!
    
    public var headerView: CalendarGridHeader!
    
    
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        refreshControl = UIRefreshControl()
        
        setupDayGrid()
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
        
        dayGrid.addSubview(refreshControl)
        dayGrid.alwaysBounceVertical = true
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
    
}
