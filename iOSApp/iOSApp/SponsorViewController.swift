import UIKit

class SponsorViewController: UIViewController {
	
	/**
	sponsor object which is shown
	*/
	public var sponsor: Sponsor!
	
	/**
	image of the sponsor
	*/
	private var imageView: UIImageView!
	
	/**
	name of the sponsor
	*/
	private var name: UILabel!
	
	/**
	website of the sponsor
	*/
	private var web: UILabel!
	
	/**
	E-Mail of the sponsor
	*/
	private var mail: UILabel!
	
	/**
	phone number of the sponsor
	*/
	private var phone: UILabel!
	
	/**
	description of the sponsor
	*/
	private var sponsorDescription: UILabel!
	
	/**
	view to scoll through all the sponsor data
	*/
	private var scrollView: UIScrollView!

    override func viewDidLoad() {
        super.viewDidLoad()
		
		let frame = CGRect(x: 8, y: 0, width: view.frame.width - 16, height: view.frame.height)

		
		scrollView = UIScrollView()
		scrollView.translatesAutoresizingMaskIntoConstraints = false
		scrollView.contentInset = UIEdgeInsetsMake(0, 8, 0, 8)
		view.addSubview(scrollView)
		scrollView.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		scrollView.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

		
		imageView = UIImageView()
		scrollView.addSubview(imageView)
		imageView.translatesAutoresizingMaskIntoConstraints = false
		imageView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: scrollView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		var imageTemp = sponsor.image
		
		if imageTemp != nil {
			let size = CGSize(width: UIScreen.main.bounds.width, height: (imageTemp?.size.height)! / ((imageTemp?.size.width)! / UIScreen.main.bounds.width))
			
			UIGraphicsBeginImageContext(size)
			imageTemp?.draw(in: CGRect(origin: .zero, size: size))
			
			imageTemp = UIGraphicsGetImageFromCurrentImageContext()
			UIGraphicsEndImageContext()

			imageView.image = imageTemp
		}
		
		name = UILabel()
		name.text = sponsor.name
		name.translatesAutoresizingMaskIntoConstraints = false
		scrollView.addSubview(name)
		name.addConstraintsXY(xView: scrollView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: imageView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		web = UILabel()
		web.text = sponsor.web
		web.textColor = UIColor.primary()
		web.isUserInteractionEnabled = true
		web.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(SponsorViewController.webTap)))
		web.translatesAutoresizingMaskIntoConstraints = false
		scrollView.addSubview(web)
		web.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: name, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		mail = UILabel()
		mail.text = sponsor.mail
		mail.textColor = UIColor.primary()
		mail.isUserInteractionEnabled = true
		mail.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(SponsorViewController.mailTap)))
		mail.translatesAutoresizingMaskIntoConstraints = false
		scrollView.addSubview(mail)
		mail.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: web, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		phone = UILabel()
		phone.text = sponsor.phone
		phone.textColor = UIColor.primary()
		phone.isUserInteractionEnabled = true
		phone.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(SponsorViewController.phoneTap)))
		phone.translatesAutoresizingMaskIntoConstraints = false
		scrollView.addSubview(phone)
		phone.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: mail, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		sponsorDescription = UILabel()
		sponsorDescription.text = sponsor.sponsorDescription
		sponsorDescription.lineBreakMode = .byWordWrapping
		sponsorDescription.numberOfLines = 0
		sponsorDescription.translatesAutoresizingMaskIntoConstraints = false
		scrollView.addSubview(sponsorDescription)
		sponsorDescription.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: phone, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		sponsorDescription.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: phone, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		view.layoutIfNeeded()
		
		var totalHeight: CGFloat = 0
		for view in scrollView.subviews {
			totalHeight += view.frame.height
		}
		
		scrollView.contentSize = CGSize(width: frame.width, height: totalHeight)

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	/**
	called when the website is tapped shows the website
	*/
	func webTap(){
		let url = URL(string: (sponsor.web!))!

		if UIApplication.shared.canOpenURL(url){
			if #available(iOS 10, *){
				UIApplication.shared.open(url, options: [:], completionHandler: nil)
			}else{
				UIApplication.shared.openURL(url)
			}
		}
	}
	
	/**
	called when the mail is tapped forwards to an e-mail program
	*/
	func mailTap(){
		let url = URL(string: "mailto://\(sponsor.mail!)")!
		
		if UIApplication.shared.canOpenURL(url){
			if #available(iOS 10, *){
				UIApplication.shared.open(url, options: [:], completionHandler: nil)
			}else{
				UIApplication.shared.openURL(url)
			}
		}
	}
	
	/**
	called when the phone number is tapped forwards to the call
	*/
	func phoneTap(){
		var realPhoneNumber = sponsor.phone?.replacingOccurrences(of: "-", with: "")
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
