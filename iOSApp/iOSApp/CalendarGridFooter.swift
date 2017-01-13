import UIKit

class CalendarGridFooter: UICollectionReusableView {
	
	/**
	lists all events from one day
	*/
	private(set) var dayList: UITableView!

	override init(frame: CGRect){
		super.init(frame: frame)
		
		dayList = UITableView()
		dayList.register(CalendarListViewCell.self, forCellReuseIdentifier: "cell")
		// size because the host pictures height inside the cell is UIScreen.main.bounds.height/10
		dayList.rowHeight = UIScreen.main.bounds.height / 10
		addSubview(dayList)
		dayList.separatorColor = UIColor.primary()
		dayList.translatesAutoresizingMaskIntoConstraints = false
		dayList.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		dayList.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		dayList.backgroundColor = UIColor.clear

		
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}

}
