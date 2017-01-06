import UIKit

class SponsorViewButton: UIButton {
	
	private var image: UIImageView!
	private var name: UILabel!

	init(frame: CGRect, sponsor: Sponsor){
		super.init(frame: frame)
		
		backgroundColor = UIColor.lightGray
		
		name = UILabel()
		name.text = sponsor.name
		addSubview(name)
		name.translatesAutoresizingMaskIntoConstraints = false
		name.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		var imageTemp = sponsor.image
			
		let size = CGSize(width: UIScreen.main.bounds.width, height: (imageTemp?.size.height)! / ((imageTemp?.size.width)! / UIScreen.main.bounds.width))
			
		UIGraphicsBeginImageContext(size)
		imageTemp?.draw(in: CGRect(origin: .zero, size: size))
			
		imageTemp = UIGraphicsGetImageFromCurrentImageContext()
		UIGraphicsEndImageContext()
		

		image = UIImageView(image: imageTemp)
		image.translatesAutoresizingMaskIntoConstraints = false
		addSubview(image)
		image.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: name, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		
	}
	
	public func height() -> CGFloat {
		layoutIfNeeded()
		
		var total = image.frame.size.height
		total += name.frame.size.height
		
		return total
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}
