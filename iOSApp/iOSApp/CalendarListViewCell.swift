import UIKit

class CalendarListViewCell: UITableViewCell {
	
	/**
	Showing the image of the event
	*/
	private(set) var imageV: UIImageView!
	
	/**
	Showing the name of the event
	*/
	private(set) var nameLabel: UILabel!
	
	/**
	Showing the starting of the event
	*/
	private(set) var dateLabel: UILabel!
	
	override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
		super.init(style: style, reuseIdentifier: reuseIdentifier)
		
		contentView.translatesAutoresizingMaskIntoConstraints = false
		contentView.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 8, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		contentView.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: -8, yView: self, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		imageV = UIImageView()
		addSubview(imageV)
		imageV.translatesAutoresizingMaskIntoConstraints = false
		imageV.addConstraintsXY(xView: contentView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: contentView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		nameLabel = UILabel()
		nameLabel.adjustsFontSizeToFitWidth = true
		nameLabel.translatesAutoresizingMaskIntoConstraints = false
		addSubview(nameLabel)
		
		dateLabel = UILabel()
		dateLabel.adjustsFontSizeToFitWidth = true
		dateLabel.translatesAutoresizingMaskIntoConstraints = false
		addSubview(dateLabel)
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
