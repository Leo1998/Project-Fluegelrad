import UIKit

class RatingView: UIView {
	
	/**
	View which dims the whole screen
	*/
	private(set) var dimView: UIView!

	override init(frame: CGRect) {
		super.init(frame: frame)
		
		
		dimView = UIView(frame: frame)
		dimView.backgroundColor = UIColor.black.withAlphaComponent(0.6)
		addSubview(dimView)
		
		
		
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}
