import Foundation

class Event: NSObject, NSCoding {
	private(set) var id:Int!
	private(set) var name:String!
	//private(set) var category:String!
	private(set) var price:Int!
	private(set) var dateStart:Date!
	private(set) var dateEnd:Date!
	private(set) var descriptionEvent:String!
	private(set) var maxParticipants:Int!
	private(set) var participants: Int!
	private(set) var ageMin:Int!
	private(set) var ageMax:Int!
	
	private(set) var sponsorIds = [Int]()
	
	
	
	private(set) var hostId:Int!
	
	private(set) var location:Location!
	
	private(set) var images = [EventImage]()
	
	init(dict: NSDictionary) {
		self.id = Int(dict.object(forKey: "id") as! String)
		self.name = dict.object(forKey: "name") as! String
		self.price = Int(dict.object(forKey: "price") as! String)
		
		let dateFormatter = DateFormatter()
		dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
		self.dateStart = dateFormatter.date(from: dict.object(forKey: "dateStart") as! String)
		self.dateEnd = dateFormatter.date(from: dict.object(forKey: "dateEnd") as! String)
		
		self.descriptionEvent = dict.object(forKey: "description") as! String
		descriptionEvent = descriptionEvent.replacingOccurrences(of: "\\n", with: "\n")
		
		self.maxParticipants = Int(dict.object(forKey: "maxParticipants") as! String)
		self.participants = Int(dict.object(forKey: "participants") as! String)
		
		self.ageMin = Int(dict.object(forKey: "ageMin") as! String)
		self.ageMax = Int(dict.object(forKey: "ageMax") as! String)
		
		self.location = Location(dict: dict)
		
		self.hostId = Int(dict.object(forKey: "hostId") as! String)
		
		let sponsors = (dict.object(forKey: "sponsors") as! NSArray)
		for value in sponsors{
			self.sponsorIds.append(Int((value as! NSString) as String)!)
		}
	}
	
	public func addImage(dict: NSDictionary){
		images.append(EventImage(dict: dict))
	}
	
	required init(coder aDecoder: NSCoder) {
		id = aDecoder.decodeObject(forKey: "id") as! Int
		name = aDecoder.decodeObject(forKey: "name") as! String
		price = aDecoder.decodeObject(forKey: "price") as! Int
		dateStart = aDecoder.decodeObject(forKey: "dateStart") as! Date
		dateEnd = aDecoder.decodeObject(forKey: "dateEnd") as! Date
		descriptionEvent = aDecoder.decodeObject(forKey: "description") as! String
		
		maxParticipants = aDecoder.decodeObject(forKey: "maxParticipants") as! Int
		participants = aDecoder.decodeObject(forKey: "participants") as! Int

		ageMin = aDecoder.decodeObject(forKey: "ageMin") as! Int
		ageMax = aDecoder.decodeObject(forKey: "ageMax") as! Int
		
		location = aDecoder.decodeObject(forKey: "location") as! Location!
		
		images = aDecoder.decodeObject(forKey: "images") as! [EventImage]
		
		hostId = aDecoder.decodeObject(forKey: "hostId") as! Int
		
		sponsorIds = aDecoder.decodeObject(forKey: "sponsors") as! [Int]
		
		//let formId = aDecoder.decodeObject(forKey: "formId") as! Int
		
	}
	
	
	func encode(with aCoder: NSCoder) {
		aCoder.encode(id, forKey: "id")
		aCoder.encode(name, forKey: "name")
		aCoder.encode(price, forKey: "price")
		aCoder.encode(dateStart, forKey: "dateStart")
		aCoder.encode(dateEnd, forKey: "dateEnd")
		aCoder.encode(descriptionEvent, forKey: "description")
		
		aCoder.encode(maxParticipants, forKey: "maxParticipants")
		aCoder.encode(participants, forKey: "participants")
		
		aCoder.encode(ageMin, forKey: "ageMin")
		aCoder.encode(ageMax, forKey: "ageMax")
		
		aCoder.encode(location, forKey: "location")
		
		aCoder.encode(images, forKey: "images")
		
		aCoder.encode(hostId, forKey: "hostId")
		
		aCoder.encode(sponsorIds, forKey: "sponsors")
	}
}
