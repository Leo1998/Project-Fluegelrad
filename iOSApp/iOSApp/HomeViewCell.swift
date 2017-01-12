import UIKit

class HomeViewCell: UITableViewCell {
	
	/**
	Showing the title of the event
	*/
	private(set) var titleLabel: UILabel!
	
	/**
	Showing the image of the event
	*/
	private(set) var imageV: UIImageView!
	
	/**
	Showing the hosts name of the event
	*/
    private(set) var hostNameLabel: UILabel!
	
	/**
	Showing the name of the event
	*/
    private(set) var nameLabel: UILabel!
	
	/**
	Showing the starting and ending date of the event
	*/
    private(set) var dateLabel: UILabel!
	
	/**
	Showing the minimum and maximum age of the event
	*/
    private(set) var ageLabel: UILabel!
	
	/**
	Showing the price of the event
	*/
	private(set) var priceLabel: UILabel!
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
		
		contentView.translatesAutoresizingMaskIntoConstraints = false
		contentView.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 8, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		contentView.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: -8, yView: self, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
				
		titleLabel = UILabel()
		titleLabel.font = UIFont.boldSystemFont(ofSize: 16)
		contentView.addSubview(titleLabel)
		titleLabel.translatesAutoresizingMaskIntoConstraints = false
		titleLabel.addConstraintsXY(xView: contentView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: contentView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		nameLabel = UILabel()
		nameLabel.translatesAutoresizingMaskIntoConstraints = false
		contentView.addSubview(nameLabel)
		nameLabel.addConstraintsXY(xView: contentView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: titleLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

		
		imageV = UIImageView()
		contentView.addSubview(imageV)
		imageV.translatesAutoresizingMaskIntoConstraints = false
		imageV.addConstraintsXY(xView: contentView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        
        hostNameLabel = UILabel()
        hostNameLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(hostNameLabel)
		
        dateLabel = UILabel()
        dateLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(dateLabel)
        
        ageLabel = UILabel()
        ageLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(ageLabel)
		
		priceLabel = UILabel()
		priceLabel.translatesAutoresizingMaskIntoConstraints = false
		contentView.addSubview(priceLabel)
		
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
