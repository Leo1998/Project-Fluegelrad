import UIKit

class CalendarDayViewHeader: UIView {
	
	/**
	the name of the evnet
	*/
    private var nameLabel: UILabel!
	
	/**
	date of the event
	*/
    private var dateLabel: UILabel!
	
	/**
	total height of the view
	*/
    private(set) var height: CGFloat = 0

	init(event: Event, sponsor: [Int: Sponsor]) {
        super.init(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
		
		backgroundColor = UIColor.primary()
		
        nameLabel = UILabel()
		nameLabel.textColor = UIColor.accent()
        nameLabel.text = event.name
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(nameLabel)
        nameLabel.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE dd.MM.YYYY HH:mm"
        
        dateLabel = UILabel()
		dateLabel.textColor = UIColor.accent()
        dateLabel.text = "von \(dateFormatter.string(from: event.dateStart)) bis \(dateFormatter.string(from: event.dateEnd))"
        dateLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(dateLabel)
        dateLabel.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
        layoutIfNeeded()
        height += nameLabel.frame.height
        height += dateLabel.frame.height
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
