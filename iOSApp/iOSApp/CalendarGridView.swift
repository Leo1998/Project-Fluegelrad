import UIKit

class CalendarGridView: UIView{

    private(set) var dayGrid: UICollectionView!
    private(set) var refreshControl: UIRefreshControl!
    
    public var headerView: CalendarGridHeader!
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        
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
        dayGrid.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        dayGrid.addConstraintsXY(xView: self, xSelfAttribute: .width, xViewAttribute: .width, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .height, yViewAttribute: .height, yMultiplier: 1, yConstant: 0)
        
        refreshControl = UIRefreshControl()
        dayGrid.addSubview(refreshControl)
        dayGrid.alwaysBounceVertical = true
    }
    
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
}
