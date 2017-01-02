import UIKit

class SortPicker: UIView {
 
	private(set) var picker: UIPickerView!
	
	override init(frame: CGRect) {
		super.init(frame: frame)
		
		picker = UIPickerView()
		addSubview(picker)
		picker.translatesAutoresizingMaskIntoConstraints = false
		picker.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}
