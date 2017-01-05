import UIKit

class HostViewController: UIViewController {
	
	public var event:Event!
	
	private var imageView: UIImageView!
	private var name: UILabel!
	private var web: UILabel!
	private var mail: UILabel!
	private var phone: UILabel!
	private var sponsorDescription: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
		
		imageView = UIImageView(image: event.host.image)
		view.addSubview(imageView)
		imageView.translatesAutoresizingMaskIntoConstraints = false
		imageView.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		name = UILabel()
		name.text = event.host.name
		name.translatesAutoresizingMaskIntoConstraints = false
		view.addSubview(name)
		name.addConstraintsXY(xView: imageView, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: imageView, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
		
		web = UILabel()
		web.text = event.host.web
		web.isUserInteractionEnabled = true
		web.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(HostViewController.webTap)))
		web.translatesAutoresizingMaskIntoConstraints = false
		view.addSubview(web)
		web.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: imageView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		mail = UILabel()
		mail.text = event.host.mail
		mail.isUserInteractionEnabled = true
		mail.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(HostViewController.mailTap)))
		mail.translatesAutoresizingMaskIntoConstraints = false
		view.addSubview(mail)
		mail.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: web, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		phone = UILabel()
		phone.text = event.host.phone
		phone.isUserInteractionEnabled = true
		phone.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(HostViewController.phoneTap)))
		phone.translatesAutoresizingMaskIntoConstraints = false
		view.addSubview(phone)
		phone.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: mail, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		sponsorDescription = UILabel()
		sponsorDescription.text = event.host.sponsorDescription
		sponsorDescription.lineBreakMode = .byWordWrapping
		sponsorDescription.numberOfLines = 0
		sponsorDescription.translatesAutoresizingMaskIntoConstraints = false
		view.addSubview(sponsorDescription)
		sponsorDescription.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: phone, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		sponsorDescription.addConstraintsXY(xView: view, xSelfAttribute: .width, xViewAttribute: .width, xMultiplier: 1, xConstant: 0, yView: phone, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	func webTap(){
		let url = URL(string: event.host.web!)!

		if UIApplication.shared.canOpenURL(url){
			if #available(iOS 10, *){
				UIApplication.shared.open(url, options: [:], completionHandler: nil)
			}else{
				UIApplication.shared.openURL(url)
			}
		}
	}
	
	func mailTap(){
		let url = URL(string: "mailto://\(event.host.mail!)")!
		
		if UIApplication.shared.canOpenURL(url){
			if #available(iOS 10, *){
				UIApplication.shared.open(url, options: [:], completionHandler: nil)
			}else{
				UIApplication.shared.openURL(url)
			}
		}
	}
	
	func phoneTap(){
		var realPhoneNumber = event.host.phone?.replacingOccurrences(of: "-", with: "")
		realPhoneNumber = realPhoneNumber?.replacingOccurrences(of: " ", with: "")
		
		let url = URL(string: "tel://\(realPhoneNumber!)")!
		
		if UIApplication.shared.canOpenURL(url){
			if #available(iOS 10, *){
				UIApplication.shared.open(url, options: [:], completionHandler: nil)
			}else{
				UIApplication.shared.openURL(url)
			}
		}
	}

}
