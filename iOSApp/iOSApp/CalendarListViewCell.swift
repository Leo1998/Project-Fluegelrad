import UIKit

class CalendarListViewCell: UITableViewCell {
	private(set) var imageV: UIImageView!
	private(set) var nameLabel: UILabel!
	private(set) var dateLabel: UILabel!
	
	override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
		super.init(style: style, reuseIdentifier: reuseIdentifier)
		
		
		imageV = UIImageView()
		addSubview(imageV)
		imageV.translatesAutoresizingMaskIntoConstraints = false
		imageV.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		nameLabel = UILabel()
		nameLabel.translatesAutoresizingMaskIntoConstraints = false
		addSubview(nameLabel)
		
		dateLabel = UILabel()
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
