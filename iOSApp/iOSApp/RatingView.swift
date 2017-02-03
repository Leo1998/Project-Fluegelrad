import UIKit

class RatingView: UIView {
	
	/**
	View which dims the whole screen
	*/
	private(set) var dimView: UIView!
	
	/**
	Star buttons
	*/
	private(set) var starButtons = [UIButton]()
	
	/**
	Button to set the rating
	*/
	private(set) var done: UIButton!
	
	/**
	Button to cancel the rating
	*/
	private(set) var cancel: UIButton!
	
	/**
	event to rate
	*/
	public var event: Event!

	/**
	rate shown
	*/
	public var rate: Int = 1


	override init(frame: CGRect) {
		super.init(frame: frame)
		
		
		dimView = UIView(frame: frame)
		dimView.backgroundColor = UIColor.black.withAlphaComponent(0.6)
		addSubview(dimView)
		
		let starView = UIView()
		starView.translatesAutoresizingMaskIntoConstraints = false
		addSubview(starView)
		starView.backgroundColor = UIColor.white
	
		var starImage = #imageLiteral(resourceName: "ic_star_18pt")
		let size = CGSize(width: 50, height: 50)
		UIGraphicsBeginImageContext(size)
		starImage.draw(in: CGRect(origin: .zero, size: size))
		starImage = UIGraphicsGetImageFromCurrentImageContext()!
		UIGraphicsEndImageContext()
		starImage = starImage.withRenderingMode(.alwaysTemplate)
		
		var borderstarImage = #imageLiteral(resourceName: "ic_star_border_18pt")
		UIGraphicsBeginImageContext(size)
		borderstarImage.draw(in: CGRect(origin: .zero, size: size))
		borderstarImage = UIGraphicsGetImageFromCurrentImageContext()!
		UIGraphicsEndImageContext()
		borderstarImage = borderstarImage.withRenderingMode(.alwaysTemplate)

		
		for index in 0...4 {
			let starButton = UIButton()
			starButton.tag = index
			starButton.translatesAutoresizingMaskIntoConstraints = false
			starView.addSubview(starButton)
			starButton.tintColor = UIColor.primary()

			if index == 0 {
				starButton.addConstraintsXY(xView: starView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: -100, yView: starView, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
				starButton.setImage(starImage, for: .normal)
			}else{
				starButton.addConstraintsXY(xView: starButtons[index-1], xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: starView, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
				starButton.setImage(borderstarImage, for: .normal)
			}
			
			starButtons.append(starButton)
		}
		
		starView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: 50*5+100, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: 50+100)
		starView.addConstraintsXY(xView: self, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
		
		cancel = UIButton()
		cancel.backgroundColor = UIColor.accent()
		
		cancel.setTitleColor(UIColor.primary(), for: .normal)
		cancel.setTitle("Abbrechen", for: .normal)
		cancel.translatesAutoresizingMaskIntoConstraints = false
		addSubview(cancel)
		cancel.addConstraintsXY(xView: starView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: starView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		cancel.addConstraintsXY(xView: starView, xSelfAttribute: .trailing, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: starView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		
		done = UIButton()
		done.backgroundColor = UIColor.accent()
		
		done.setTitleColor(UIColor.primary(), for: .normal)
		done.setTitle("OK", for: .normal)
		done.translatesAutoresizingMaskIntoConstraints = false
		addSubview(done)
		done.addConstraintsXY(xView: starView, xSelfAttribute: .leading, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: starView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		done.addConstraintsXY(xView: starView, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: starView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		

		layoutIfNeeded()
		starView.roundCorner(corners: [.topLeft, .topRight], radius: 10)
		cancel.roundCorner(corners: [.bottomLeft], radius: 10)
		done.roundCorner(corners: [.bottomRight], radius: 10)

	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	public func rateChange(rate: Int){
		var starImage = #imageLiteral(resourceName: "ic_star_18pt")
		let size = CGSize(width: 50, height: 50)
		UIGraphicsBeginImageContext(size)
		starImage.draw(in: CGRect(origin: .zero, size: size))
		starImage = UIGraphicsGetImageFromCurrentImageContext()!
		UIGraphicsEndImageContext()
		starImage = starImage.withRenderingMode(.alwaysTemplate)
		
		var borderstarImage = #imageLiteral(resourceName: "ic_star_border_18pt")
		UIGraphicsBeginImageContext(size)
		borderstarImage.draw(in: CGRect(origin: .zero, size: size))
		borderstarImage = UIGraphicsGetImageFromCurrentImageContext()!
		UIGraphicsEndImageContext()
		borderstarImage = borderstarImage.withRenderingMode(.alwaysTemplate)
		
		
		for (index, value) in starButtons.enumerated() {
			if index < rate {
				value.setImage(starImage, for: .normal)
			}else{
				value.setImage(borderstarImage, for: .normal)
			}
		}
		self.rate = rate
	}
}
