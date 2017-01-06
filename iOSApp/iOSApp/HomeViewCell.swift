import UIKit

class HomeViewCell: UITableViewCell {
	
	private(set) var titleLabel: UILabel!
	private(set) var imageV: UIImageView!
    private(set) var hostNameLabel: UILabel!
    private(set) var nameLabel: UILabel!
    private(set) var dateLabel: UILabel!
    private(set) var ageLabel: UILabel!
	private(set) var priceLabel: UILabel!
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
		
		titleLabel = UILabel()
		titleLabel.font = UIFont.boldSystemFont(ofSize: 16)
		addSubview(titleLabel)
		titleLabel.translatesAutoresizingMaskIntoConstraints = false
		titleLabel.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		nameLabel = UILabel()
		nameLabel.translatesAutoresizingMaskIntoConstraints = false
		addSubview(nameLabel)
		nameLabel.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: titleLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

		
		imageV = UIImageView()
		addSubview(imageV)
		imageV.translatesAutoresizingMaskIntoConstraints = false
		imageV.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        
        hostNameLabel = UILabel()
        hostNameLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(hostNameLabel)
		
        dateLabel = UILabel()
        dateLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(dateLabel)
        
        ageLabel = UILabel()
        ageLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(ageLabel)
		
		priceLabel = UILabel()
		priceLabel.translatesAutoresizingMaskIntoConstraints = false
		addSubview(priceLabel)
		
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
