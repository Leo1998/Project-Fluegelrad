import UIKit
import QuartzCore

class CalendarGridCell: UICollectionViewCell {
	
	/**
	the number of the day
	*/
    private(set) var numberLabel: UILabel!
    
    override init(frame: CGRect){
        super.init(frame: frame)
        
        numberLabel = UILabel(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: frame.size.height))
        numberLabel.textAlignment = NSTextAlignment.center
        numberLabel.layer.masksToBounds = true
        numberLabel.layer.cornerRadius = frame.size.width/2
        numberLabel.font = UIFont(name: numberLabel.font.fontName, size: frame.size.width/2)
		
        addSubview(numberLabel)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
