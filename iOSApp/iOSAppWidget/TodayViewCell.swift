import UIKit

class TodayViewCell: UITableViewCell {
	
	/**
	Showing the name of the event
	*/
	private(set) var nameLabel: UILabel!
	
	/**
	Showing the starting Time of the event
	*/
	private(set) var startTimeLabel: UILabel!
	
	/**
	Showing the number of participants from the event
	*/
	private(set) var participantsLabel: UILabel!
	
	override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
		super.init(style: style, reuseIdentifier: reuseIdentifier)

		nameLabel = UILabel()
		nameLabel.font = UIFont.boldSystemFont(ofSize: 15)
		addSubview(nameLabel)
		nameLabel.translatesAutoresizingMaskIntoConstraints = false
		nameLabel.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)

		startTimeLabel = UILabel()
		addSubview(startTimeLabel)
		startTimeLabel.translatesAutoresizingMaskIntoConstraints = false
		startTimeLabel.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		participantsLabel = UILabel()
		addSubview(participantsLabel)
		participantsLabel.translatesAutoresizingMaskIntoConstraints = false
		participantsLabel.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: startTimeLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		participantsLabel.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

}
