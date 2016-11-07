import UIKit

class CalendarGridHeader: UICollectionReusableView {
    var numberLabel: UILabel!
    
    override init(frame: CGRect){
        super.init(frame: frame)
        
        numberLabel = UILabel(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: frame.size.height))
        numberLabel.backgroundColor = UIColor.red
        numberLabel.textAlignment = NSTextAlignment.center
        numberLabel.text = "Hello World!"
        
        addSubview(numberLabel)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
