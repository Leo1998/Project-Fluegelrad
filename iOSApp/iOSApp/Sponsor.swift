import UIKit

class Sponsor: NSObject, NSCoding{
	private var imagePath: String?
	private(set) var id: Int!
	private(set) var phone: String?
	private(set) var mail: String?
	private(set) var web: String?
	private(set) var name: String!
	private(set) var sponsorDescription: String?

	private(set) var scaled = false
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
		
		set(image) {
			if !scaled {
				imageSave = image
				scaled = true
			}
		}
	}
	private var iconSave: UIImage?
	public var icon: UIImage? {
		get {
			if self.imageSave == nil {
				
				return nil
			}
			if iconSave == nil {
				let imageTemp = imageSave
				
				let size = CGSize(width: (imageTemp?.size.width)! / 2, height: (imageTemp?.size.height)! / 2)
				
				UIGraphicsBeginImageContext(size)
				imageTemp?.draw(in: CGRect(origin: .zero, size: size))
				
				self.iconSave = UIGraphicsGetImageFromCurrentImageContext()
				UIGraphicsEndImageContext()
			}
			
			return iconSave
		}
	}
	
	init(dict: NSDictionary) {
		imagePath = (dict.object(forKey: "image") as! String)
		id = Int(dict.object(forKey: "id") as! String)
		phone = (dict.object(forKey: "phone") as! String)
		mail = (dict.object(forKey: "mail") as! String)
		web = (dict.object(forKey: "web") as! String)
		name = (dict.object(forKey: "name") as! String)
		sponsorDescription = (dict.object(forKey: "description") as! String)
	}
	
	
	required init(coder aDecoder: NSCoder) {
		imagePath = (aDecoder.decodeObject(forKey: "path") as! String)
		id = (aDecoder.decodeObject(forKey: "id") as! Int)
		phone = (aDecoder.decodeObject(forKey: "phone") as! String)
		mail = (aDecoder.decodeObject(forKey: "mail") as! String)
		web = (aDecoder.decodeObject(forKey: "web") as! String)
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
