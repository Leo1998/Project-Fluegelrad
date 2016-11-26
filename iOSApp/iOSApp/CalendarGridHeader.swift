import UIKit
import QuartzCore

class CalendarGridHeader: UICollectionReusableView {
    var weekView: UIView!
    
    var left: UIButton!
    var right: UIButton!
    var month: UILabel!
    
    override init(frame: CGRect){
        super.init(frame: frame)
        
        let calendar = NSCalendar.autoupdatingCurrent
        let weekString = calendar.shortWeekdaySymbols
        
        weekView = UIView()
        weekView.translatesAutoresizingMaskIntoConstraints = false
        
        for week in 0...6{
            let tempLabel = UILabel(frame: CGRect(x: (frame.size.width/7) * CGFloat(week)-1, y: 0, width: frame.size.width/7+2, height: 35))
            tempLabel.textAlignment = NSTextAlignment.center
            tempLabel.backgroundColor = UIColor.lightGray
            
            if week == 6 {
                tempLabel.text = weekString[0]
            }else{
                tempLabel.text = weekString[week+1]
            }
            
            weekView.addSubview(tempLabel)
        }

        addSubview(weekView)
        
        left = UIButton()
        left.translatesAutoresizingMaskIntoConstraints = false
        left.setImage(#imageLiteral(resourceName: "ic_arrow_back"), for: UIControlState.normal)
        
        right = UIButton()
        right.translatesAutoresizingMaskIntoConstraints = false
        right.setImage(#imageLiteral(resourceName: "ic_arrow_forward"), for: UIControlState.normal)
        
        month = UILabel()
        month.translatesAutoresizingMaskIntoConstraints = false

        addSubview(left)
        addSubview(right)
        addSubview(month)
        
        
        let leftButtonX = NSLayoutConstraint(item: left, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let leftButtonY = NSLayoutConstraint(item: left, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([leftButtonX, leftButtonY])
        
        let rightButtonX = NSLayoutConstraint(item: right, attribute: NSLayoutAttribute.trailing, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.trailing, multiplier: 1, constant: 0)
        let rightButtonY = NSLayoutConstraint(item: right, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([rightButtonX, rightButtonY])
        
        let monthLabelX = NSLayoutConstraint(item: month, attribute: NSLayoutAttribute.centerX, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.centerX, multiplier: 1, constant: 0)
        let monthLabelY = NSLayoutConstraint(item: month, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([monthLabelX, monthLabelY])
        
        let weekViewX = NSLayoutConstraint(item: weekView, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let weekViewY = NSLayoutConstraint(item: weekView, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: left, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([weekViewX, weekViewY])
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
