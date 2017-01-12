import UIKit

class LicensesViewCell: UITableViewCell {
	
	/**
	name of the licensed product
	*/
	private(set) var nameLabel: UILabel!
	
	/**
	url of the licensed product
	*/
	private(set) var urlLabel: UILabel!
	
	/**
	a view where the license itself is shown
	*/
	private(set) var licenseView: UIView!
	
	/**
	copyright of the licensed product
	*/
	private(set) var copyright: UILabel!
	
	/**
	license of the licensed product
	*/
	private(set) var license: UILabel!
	
	override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
		super.init(style: style, reuseIdentifier: reuseIdentifier)
		
		contentView.translatesAutoresizingMaskIntoConstraints = false
		contentView.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 8, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		contentView.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: -8, yView: self, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

		
		nameLabel = UILabel()
		nameLabel.translatesAutoresizingMaskIntoConstraints = false
		contentView.addSubview(nameLabel)
		nameLabel.addConstraintsXY(xView: contentView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: contentView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		urlLabel = UILabel()
		urlLabel.translatesAutoresizingMaskIntoConstraints = false
		contentView.addSubview(urlLabel)
		urlLabel.addConstraintsXY(xView: contentView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		licenseView = UIView()
		licenseView.backgroundColor = UIColor.lightGray
		licenseView.translatesAutoresizingMaskIntoConstraints = false
		contentView.addSubview(licenseView)
		
		copyright = UILabel()
		copyright.translatesAutoresizingMaskIntoConstraints = false
		licenseView.addSubview(copyright)
		copyright.addConstraintsXY(xView: licenseView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: licenseView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		license = UILabel()
		license.translatesAutoresizingMaskIntoConstraints = false
		license.numberOfLines = 0
		license.lineBreakMode = .byWordWrapping
		licenseView.addSubview(license)
		license.addConstraintsXY(xView: licenseView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: copyright, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		license.addConstraintsXY(xView: licenseView, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: licenseView, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
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
