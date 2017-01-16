import UIKit

class AboutViewController: UIViewController {
	
	/**
	button to show all used licenses
	*/
	private var licensesButton: UIButton!
	
	/**
	label which shows the app version
	*/
	private var versionLabel: UILabel!
	
	/**
	button to write a support e-mail
	*/
	private var supportButton: UIButton!
	
    override func viewDidLoad() {
        super.viewDidLoad()
				
		licensesButton = UIButton()
		licensesButton.setTitle("Lizenzen", for: .normal)
		licensesButton.setTitleColor(UIColor.primary(), for: .normal)
		view.addSubview(licensesButton)
		licensesButton.addTarget(self, action: #selector(AboutViewController.licensePush), for: .touchUpInside)
		licensesButton.translatesAutoresizingMaskIntoConstraints = false
		licensesButton.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: topLayoutGuide, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		licensesButton.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: view.frame.height / 10)

		supportButton = UIButton()
		supportButton.setTitle("Kontaktiere uns", for: .normal)
		view.addSubview(supportButton)
		supportButton.setTitleColor(UIColor.primary(), for: .normal)
		supportButton.addTarget(self, action: #selector(AboutViewController.support), for: .touchUpInside)
		supportButton.translatesAutoresizingMaskIntoConstraints = false
		supportButton.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: licensesButton, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		supportButton.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: view.frame.height / 10)
		
		versionLabel = UILabel()
		versionLabel.text =  "App Version \(Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString")!)"
		versionLabel.translatesAutoresizingMaskIntoConstraints = false
		view.addSubview(versionLabel)
		versionLabel.addConstraintsXY(xView: view, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: supportButton, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		versionLabel.addConstraintsXY(xView: view, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: view.frame.height / 10)
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
	
	/**
	forwards you to your email programm to write a support e-mail
	*/
	func support(){
		let url = URL(string: "mailto://kaleb.dk@gmail.com")!
		
		if UIApplication.shared.canOpenURL(url){
			if #available(iOS 10, *){
				UIApplication.shared.open(url, options: [:], completionHandler: nil)
			}else{
				UIApplication.shared.openURL(url)
			}
		}
	}
	
	override func viewWillAppear(_ animated: Bool) {
		navigationController?.setNavigationBarHidden(true, animated: false)
	}
}
