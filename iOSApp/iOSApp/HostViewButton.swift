import UIKit

class HostViewButton: UIButton {
	
	private var image: UIImageView!
	private var name: UILabel!

	init(frame: CGRect, event: Event, sponsors: [Int: Sponsor]){
		super.init(frame: frame)
		
		backgroundColor = UIColor.lightGray
		
		if !(sponsors[event.hostId]?.scaled)! {
			let imageTemp = sponsors[event.hostId]?.image
			
			let size = CGSize(width: (imageTemp?.size.width)! * ((UIScreen.main.bounds.height/5) / (imageTemp?.size.height)!), height: UIScreen.main.bounds.height/5)
			
			UIGraphicsBeginImageContext(size)
			imageTemp?.draw(in: CGRect(origin: .zero, size: size))
			
			sponsors[event.hostId]?.image = UIGraphicsGetImageFromCurrentImageContext()
			UIGraphicsEndImageContext()
		}

		image = UIImageView(image: sponsors[event.hostId]?.image)
		image.translatesAutoresizingMaskIntoConstraints = false
		addSubview(image)
		image.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		name = UILabel()
		name.text = sponsors[event.hostId]?.name
		addSubview(name)
		name.translatesAutoresizingMaskIntoConstraints = false
		name.addConstraintsXY(xView: image, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
	}
	
	public func height() -> CGFloat {
		layoutIfNeeded()
		return image.frame.size.height
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}
