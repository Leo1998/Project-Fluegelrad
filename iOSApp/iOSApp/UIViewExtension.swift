import UIKit

extension UIView {
    func addConstraintsXY(xView: Any?, xSelfAttribute: NSLayoutAttribute, xViewAttribute: NSLayoutAttribute, xMultiplier: CGFloat, xConstant: CGFloat, yView: Any?, ySelfAttribute: NSLayoutAttribute, yViewAttribute: NSLayoutAttribute, yMultiplier: CGFloat, yConstant: CGFloat){
        
        let tempX = NSLayoutConstraint(item: self, attribute: xSelfAttribute, relatedBy: NSLayoutRelation.equal, toItem: xView, attribute: xViewAttribute, multiplier: xMultiplier, constant: xConstant)
        
        let tempY = NSLayoutConstraint(item: self, attribute: ySelfAttribute, relatedBy: NSLayoutRelation.equal, toItem: yView, attribute: yViewAttribute, multiplier: yMultiplier, constant: yConstant)
        
        NSLayoutConstraint.activate([tempX, tempY])
    }
}


