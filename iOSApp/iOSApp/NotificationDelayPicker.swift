import UIKit

class NotificationDelayPicker: UIView {

	/**
	Picker view from where to pick the delay
	*/
	private(set) var picker: UIDatePicker!
	
	/**
	View which dims the whole screen
	*/
	private(set) var dimView: UIView!
	
	/**
	Button to set the delay
	*/
	private(set) var done: UIButton!
	
	/**
	Button to cancel the reminder
	*/
	private(set) var cancel: UIButton!
	
	override init(frame: CGRect) {
		super.init(frame: frame)
		
		dimView = UIView(frame: frame)
		dimView.backgroundColor = UIColor.black.withAlphaComponent(0.6)
		addSubview(dimView)
		
		picker = UIDatePicker()
		picker.datePickerMode = .countDownTimer
		picker.minuteInterval = 5
		
		picker.backgroundColor = UIColor.accent()
		
		picker.roundCorner(corners: [.topLeft, .topRight], radius: 10)
		
		picker.layer.shadowOpacity = 0.8
		picker.layer.masksToBounds = true
		picker.layer.shadowOffset = CGSize(width: 0, height: 0)
		addSubview(picker)
		picker.translatesAutoresizingMaskIntoConstraints = false
		picker.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
		
	
		cancel = UIButton()
		cancel.backgroundColor = UIColor.accent()

		
		cancel.setTitleColor(UIColor.primary(), for: .normal)
		cancel.setTitle("Keine Benarchitigung erhalten", for: .normal)
		cancel.translatesAutoresizingMaskIntoConstraints = false
		addSubview(cancel)
		cancel.addConstraintsXY(xView: picker, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: picker, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		done = UIButton()
		done.backgroundColor = UIColor.accent()
		
		done.setTitleColor(UIColor.primary(), for: .normal)
		done.setTitle("OK", for: .normal)
		done.translatesAutoresizingMaskIntoConstraints = false
		addSubview(done)
		done.addConstraintsXY(xView: cancel, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: picker, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		done.addConstraintsXY(xView: picker, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: picker, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}

}
