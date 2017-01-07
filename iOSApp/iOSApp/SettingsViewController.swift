import UIKit

class SettingsViewController: UIViewController {
	
	private var button: UIButton!
	
    override func viewDidLoad() {
        super.viewDidLoad()
		
		button = UIButton()
		button.setTitle("Push", for: .normal)
		button.backgroundColor = UIColor.primary()
		view.addSubview(button)
		button.addTarget(self, action: #selector(SettingsViewController.push), for: .touchUpInside)
		button.translatesAutoresizingMaskIntoConstraints = false
		button.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		button.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	func push(){
		performSegue(withIdentifier: "LicensesViewController", sender: self)
	}
}
