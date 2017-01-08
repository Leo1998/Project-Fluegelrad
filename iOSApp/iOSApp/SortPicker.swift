import UIKit

class SortPicker: UIView {
 
	/**
	Picker view from where to pick the sorting method
	*/
	private(set) var picker: UIPickerView!
	
	/**
	View which dims the whole screen
	*/
	private(set) var dimView: UIView!
	
	override init(frame: CGRect) {
		super.init(frame: frame)
		
		dimView = UIView(frame: frame)
		dimView.backgroundColor = UIColor.black.withAlphaComponent(0.6)
		addSubview(dimView)

		picker = UIPickerView()
		picker.backgroundColor = UIColor.accent()
		picker.layer.cornerRadius = 10
		picker.layer.shadowOpacity = 0.8
		picker.layer.shadowOffset = CGSize(width: 0, height: 0)
		addSubview(picker)
		picker.translatesAutoresizingMaskIntoConstraints = false
		picker.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}
