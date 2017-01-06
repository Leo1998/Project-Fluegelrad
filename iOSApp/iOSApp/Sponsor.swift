import UIKit

class Sponsor: NSObject, NSCoding{
	private var imagePath: String?
	private(set) var id: Int!
	private(set) var phone: String?
	private(set) var mail: String?
	private(set) var web: String?
	private(set) var name: String!
	private(set) var sponsorDescription: String?

	private var imageSave: UIImage?
	public var image: UIImage? {
		get {
			if self.imageSave == nil {
				
				let url = URL(string: DatabaseManager.url + self.imagePath!)!
				let data = try? Data(contentsOf: url)
				imageSave = UIImage(data: data!)
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
