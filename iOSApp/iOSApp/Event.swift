import Foundation

class Event: NSObject, NSCoding {
	
	/**
	id of the Event
	*/
	private(set) var id:Int!
	
	/**
	name of the Event
	*/
	private(set) var name:String!
	
	/**
	price of the Event
	*/
	private(set) var price:Int!
	
	/**
	starting date of the Event
	*/
	private(set) var dateStart:Date!
	
	/**
	ending date of the Event
	*/
	private(set) var dateEnd:Date!
	
	/**
	description of the Event
	*/
	private(set) var descriptionEvent:String!
	
	/**
	maximum count of participants of the Event
	*/
	private(set) var maxParticipants:Int!
	
	/**
	count of participating people of the Event
	*/
	private(set) var participants: Int!
	
	/**
	minmum age for the Event
	*/
	private(set) var ageMin:Int!
	
	/**
	maximum age for the Event
	*/
	private(set) var ageMax:Int!
	
	/**
	an array of all sponsor Ids from the Event
	*/
	private(set) var sponsorIds = [Int]()
	
	/**
	id of the host from the Event
	*/
	private(set) var hostId:Int!
	
	/**
	Location object of the Event
	*/
	private(set) var location:Location!
	
	/**
	all Image objects associated with the Event
	*/
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
