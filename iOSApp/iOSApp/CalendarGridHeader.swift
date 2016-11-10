import UIKit
import QuartzCore

class CalendarGridHeader: UICollectionReusableView {
    var weekLabels = [UILabel]()
    
    override init(frame: CGRect){
        super.init(frame: frame)
        
        let calendar = NSCalendar.autoupdatingCurrent
        let weekString = calendar.shortWeekdaySymbols
        
        for week in 0...6{
            let tempLabel = UILabel(frame: CGRect(x: ((frame.size.width-5-5 - (7-1))/7+1) * CGFloat(week)+5, y: 0, width: (frame.size.width-5-5 - (7-1))/7, height: frame.size.height))
            tempLabel.backgroundColor = UIColor.cyan
            tempLabel.textAlignment = NSTextAlignment.center
            
            if week == 6 {
                tempLabel.text = weekString[0]
            }else{
                tempLabel.text = weekString[week+1]
            }
            
            weekLabels.append(tempLabel)
            
            addSubview(tempLabel)
            
        }
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
