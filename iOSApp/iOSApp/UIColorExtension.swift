import UIKit

extension UIColor {
	/**
	The primary Color of the app
	*/
	static func primary() -> UIColor{
		return UIColor(red: 18/255, green: 183/255, blue: 54/255, alpha: 255/255)
	}
	
	/**
	The secondary Color of the app
	*/
	static func secondary() -> UIColor{
		return UIColor(red: 4/255, green: 155/255, blue: 37/255, alpha: 255/255)
	}
	
	/**
	The accent Color of the app
	*/
	static func accent() -> UIColor{
		return UIColor(red: 255/255, green: 255/255, blue: 255/255, alpha: 255/255)
	}
}
