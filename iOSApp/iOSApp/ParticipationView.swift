import UIKit

class ParticipationView: UIView {
	private var currentParticipants: UILabel!
	private(set) var participationButton: UIButton!
	
	public var height: CGFloat {
		get {
			layoutIfNeeded()
			
			return participationButton.frame.height
		}
	}
	
	init(frame: CGRect, event: Event) {
		super.init(frame: frame)

		currentParticipants = UILabel()
		updateCurrentParticipants(event: event)
		addSubview(currentParticipants)
		currentParticipants.translatesAutoresizingMaskIntoConstraints = false
		currentParticipants.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
		
		participationButton = UIButton()
		participationButton.setTitle("Jetzt Anmelden", for: .normal)
		participationButton.backgroundColor = UIColor.primary()
		addSubview(participationButton)
		participationButton.translatesAutoresizingMaskIntoConstraints = false
		participationButton.addConstraintsXY(xView: currentParticipants, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		participationButton.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	public func updateCurrentParticipants(event: Event){
		currentParticipants.text = "Es sind noch \(event.maxParticipants - event.participants) Plätze frei."
	}
}
