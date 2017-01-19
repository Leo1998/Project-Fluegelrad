import UIKit

class CalendarGridFooter: UICollectionReusableView {
	
	/**
	label to show which date is shown
	*/
	private(set) var dayLabel: UILabel!
	
	/**
	lists all events from one day
	*/
	private(set) var dayList: UITableView!

	override init(frame: CGRect){
		super.init(frame: frame)
		
		dayLabel = UILabel()
		dayLabel.text = "Test"
		dayLabel.font = UIFont.boldSystemFont(ofSize: 16)
		dayLabel.translatesAutoresizingMaskIntoConstraints = false
		addSubview(dayLabel)
		dayLabel.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		dayList = UITableView()
		dayList.register(CalendarListViewCell.self, forCellReuseIdentifier: "cell")
		// size because the host pictures height inside the cell is UIScreen.main.bounds.height/10
		dayList.rowHeight = UIScreen.main.bounds.height / 10
		addSubview(dayList)
		dayList.separatorColor = UIColor.primary()
		dayList.translatesAutoresizingMaskIntoConstraints = false
		dayList.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: dayLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		dayList.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		dayList.backgroundColor = UIColor.clear
		dayList.tableFooterView = UIView()
		
		

		
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}

}
