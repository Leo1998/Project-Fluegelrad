import UIKit

class CalendarDayViewHeader: UIView {
	
	/**
	the name of the evnet
	*/
    private var nameLabel: UILabel!
	
	/**
	starting date of the event
	*/
    private var dateStartLabel: UILabel!
	
	/**
	ending date of the event
	*/
    private var dateEndLabel: UILabel!
	
	/**
	host name of the event
	*/
    private var hostLabel: UILabel!
	
	/**
	total height of the view
	*/
    private(set) var height: CGFloat = 0

	init(event: Event, sponsor: [Int: Sponsor]) {
        super.init(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
        
        nameLabel = UILabel()
        nameLabel.text = event.name
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(nameLabel)
        nameLabel.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE dd.MM.YYYY HH:mm"
        
        dateStartLabel = UILabel()
        dateStartLabel.text = dateFormatter.string(from: event.dateStart)
        dateStartLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(dateStartLabel)
        dateStartLabel.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)

        dateEndLabel = UILabel()
        dateEndLabel.text = dateFormatter.string(from: event.dateEnd)
        dateEndLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(dateEndLabel)
        dateEndLabel.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: dateStartLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

        hostLabel = UILabel()
        hostLabel.text = sponsor[event.hostId]?.name
        hostLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(hostLabel)
        hostLabel.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        
        layoutIfNeeded()
        height += nameLabel.frame.height
        height += hostLabel.frame.height
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
