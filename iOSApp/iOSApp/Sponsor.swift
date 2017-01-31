import UIKit

@objc(Sponsor)
class Sponsor: NSObject, NSCoding{
	
	/**
	image path from where to download the image from
	*/
	private var imagePath: String?
	
	/**
	id of the Sponsor
	*/
	private(set) var id: Int!
	
	/**
	telephone number of the Sponsor
	*/
	private(set) var phone: String?
	
	/**
	E-Mail of the Sponsor
	*/
	private(set) var mail: String?
	
	/**
	website of the Sponsor
	*/
	private(set) var web: String?
	
	/**
	name of the Sponsor
	*/
	private(set) var name: String!
	
	/**
	description of the Sponsor
	*/
	private(set) var sponsorDescription: String?

	/**
	The image itself
	*/
	private var imageSave: UIImage?
	/**
	The reference to the image so it can download itself
	*/
	public var image: UIImage? {
		get {
			if self.imageSave == nil {
				
				imageSave = Storage.getImage(path: imagePath!)
			}
			
			return imageSave
		}
	}
	
	init(dict: NSDictionary) {
		imagePath = (dict.object(forKey: "imagePath") as? String)
		id = Int(dict.object(forKey: "id") as! String)
		phone = (dict.object(forKey: "phone") as? String)
		mail = (dict.object(forKey: "mail") as? String)
		web = (dict.object(forKey: "web") as? String)
		name = (dict.object(forKey: "name") as! String)
		sponsorDescription = (dict.object(forKey: "description") as! String)
	}
	
	
	required init(coder aDecoder: NSCoder) {
		imagePath = (aDecoder.decodeObject(forKey: "path") as? String)
		id = (aDecoder.decodeObject(forKey: "id") as! Int)
		phone = (aDecoder.decodeObject(forKey: "phone") as? String)
		mail = (aDecoder.decodeObject(forKey: "mail") as? String)
		web = (aDecoder.decodeObject(forKey: "web") as? String)
		name = (aDecoder.decodeObject(forKey: "name") as! String)
		sponsorDescription = (aDecoder.decodeObject(forKey: "sponsorDescription") as! String)
		
	}
	
	
	func encode(with aCoder: NSCoder) {
		aCoder.encode(imagePath, forKey: "path")
		aCoder.encode(id, forKey: "id")
		aCoder.encode(phone, forKey: "phone")
		aCoder.encode(mail, forKey: "mail")
		aCoder.encode(web, forKey: "web")
		aCoder.encode(name, forKey: "name")
		aCoder.encode(sponsorDescription, forKey: "sponsorDescription")

	}
}
