import UIKit

class SettingsViewController: UIViewController {
	
	private var button: UIButton!
	
    override func viewDidLoad() {
        super.viewDidLoad()
		
		button = UIButton(frame: view.frame)
		button.setTitle("Push", for: .normal)
		button.backgroundColor = UIColor.primary()
		view.addSubview(button)
		button.addTarget(self, action: #selector(SettingsViewController.push), for: .touchUpInside)
		

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	func push(){
		performSegue(withIdentifier: "LicensesViewController", sender: self)
	}
}
