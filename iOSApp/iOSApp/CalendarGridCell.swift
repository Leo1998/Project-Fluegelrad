import UIKit

class CalendarGridCell: UICollectionViewCell {
    var numberLabel: UILabel!
    
    override init(frame: CGRect){
        super.init(frame: frame)
        
        numberLabel = UILabel(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: frame.size.height))
        numberLabel.backgroundColor = UIColor.yellow
        numberLabel.textAlignment = NSTextAlignment.center
        
        addSubview(numberLabel)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
