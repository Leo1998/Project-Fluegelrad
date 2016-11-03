import UIKit

class CalendarGridCell: UICollectionViewCell {
    var numberLabel: UILabel!
    
    override init(frame: CGRect){
        super.init(frame: frame)
        
        numberLabel = UILabel(frame: frame)
        numberLabel.backgroundColor = UIColor.yellow
        
        addSubview(numberLabel)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
