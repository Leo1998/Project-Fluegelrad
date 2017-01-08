import UIKit
import QuartzCore

class CalendarGridHeader: UICollectionReusableView {
	
	/**
	the view where the week days are shown
	*/
    private var weekView: UIView!
	
	/**
	button to get the previous month
	*/
    private(set) var left: UIButton!
	
	/**
	button to get the next month
	*/
    private(set) var right: UIButton!
	
	/**
	label which shows the current month
	*/
    private(set) var month: UILabel!
    
    override init(frame: CGRect){
        super.init(frame: frame)
		
		backgroundColor = UIColor.primary()
		tintColor = UIColor.accent()
        
        setupMonthChanger()
        setupWeekView()

    }
	
	/**
	setups the week day view
	*/
    private func setupWeekView(){
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
        weekView.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: left, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
    }
	
	/**
	setups the views to change through the months
	*/
    private func setupMonthChanger(){
        left = UIButton()
        left.translatesAutoresizingMaskIntoConstraints = false
        left.setImage(#imageLiteral(resourceName: "ic_arrow_back"), for: UIControlState.normal)
        addSubview(left)
        left.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)

        right = UIButton()
        right.translatesAutoresizingMaskIntoConstraints = false
        right.setImage(#imageLiteral(resourceName: "ic_arrow_forward"), for: UIControlState.normal)
        addSubview(right)
        right.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)

        month = UILabel()
		month.textColor = UIColor.accent()
        month.translatesAutoresizingMaskIntoConstraints = false
        addSubview(month)
        month.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
