import UIKit

extension UIView {
	/**
	A simple one line solution to adding 2 Constraints	
	*/
    func addConstraintsXY(xView: Any?, xSelfAttribute: NSLayoutAttribute, xViewAttribute: NSLayoutAttribute, xMultiplier: CGFloat, xConstant: CGFloat, yView: Any?, ySelfAttribute: NSLayoutAttribute, yViewAttribute: NSLayoutAttribute, yMultiplier: CGFloat, yConstant: CGFloat){
        
        let tempX = NSLayoutConstraint(item: self, attribute: xSelfAttribute, relatedBy: NSLayoutRelation.equal, toItem: xView, attribute: xViewAttribute, multiplier: xMultiplier, constant: xConstant)
        
        let tempY = NSLayoutConstraint(item: self, attribute: ySelfAttribute, relatedBy: NSLayoutRelation.equal, toItem: yView, attribute: yViewAttribute, multiplier: yMultiplier, constant: yConstant)
        
        NSLayoutConstraint.activate([tempX, tempY])
    }
	
	/**
	Choose which corner to round
	*/
	func roundCorner(corners: [UIRectCorner], radius: CGFloat){
		
		var corner = corners[0]
		for (index, value) in corners.enumerated(){
			if index != 0 {
				corner = corner.union(value)
			}
		}
				
		let maskPath: UIBezierPath = UIBezierPath(roundedRect: bounds, byRoundingCorners: corner, cornerRadii: CGSize(width: radius, height: radius))

		let maskLayer: CAShapeLayer = CAShapeLayer(layer: layer)
		maskLayer.frame = bounds
		maskLayer.path = maskPath.cgPath
		
		layer.mask = maskLayer
		
	}
}


