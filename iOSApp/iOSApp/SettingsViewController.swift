import UIKit

class SettingsViewController: UIViewController {
	
	/**
	button to show all used licenses
	*/
	private var licensesButton: UIButton!
	
    override func viewDidLoad() {
        super.viewDidLoad()
		
		licensesButton = UIButton()
		licensesButton.setTitle("Push", for: .normal)
		licensesButton.backgroundColor = UIColor.primary()
		licensesButton.addSubview(licensesButton)
		licensesButton.addTarget(self, action: #selector(SettingsViewController.licensePush), for: .touchUpInside)
		licensesButton.translatesAutoresizingMaskIntoConstraints = false
		licensesButton.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		licensesButton.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	/**
	shows all the licenses when the button is pushed
	*/
	func licensePush(){
		performSegue(withIdentifier: "LicensesViewController", sender: self)
	}
}
