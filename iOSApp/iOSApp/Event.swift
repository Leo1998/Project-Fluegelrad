import Foundation

class Event: NSObject, NSCoding {
    var id:Int!
    var name:String!
    var location:Location!
    var category:String!
    var price:Int!
    var hostId:Int!
    var date:Date!
    var descriptionEvent:String!
    var maxParticipants:Int!
    var participants:Int!
    var age:Int!
    
    init(id: Int, name: String, location: String, category: String, price: Int, host: Int, date: Date, descriptionEvent: String, maxParticipants: Int, participants: Int, age: Int){
        self.id = id
        self.name = name
        self.location = Location(address: location)
        self.category = category
        self.price = price
        self.hostId = host
        self.date = date
        self.descriptionEvent = descriptionEvent
        self.maxParticipants = maxParticipants
        self.participants = participants
        self.age = age

    }
    
    init(dict: NSDictionary) {
        self.id = Int(dict.object(forKey: "id") as! String)
        self.name = dict.object(forKey: "name") as! String
        self.location = Location(address: dict.object(forKey: "location") as! String)
        self.category = dict.object(forKey: "category") as! String
        self.price = Int(dict.object(forKey: "price") as! String)
        self.hostId = Int(dict.object(forKey: "hostId") as! String)
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        self.date = dateFormatter.date(from: dict.object(forKey: "date") as! String)
        
        self.descriptionEvent = dict.object(forKey: "description") as! String
        self.maxParticipants = Int(dict.object(forKey: "maxParticipants") as! String)
        self.participants = Int(dict.object(forKey: "participants") as! String)
        self.age = Int(dict.object(forKey: "age") as! String)

    }
    
    func getDictonary() -> NSDictionary {
        var dict = [String:NSObject]()
        dict["id"] = id as NSObject
        dict["name"] = name as NSObject
        dict["location"] = location as NSObject
        dict["category"] = category as NSObject
        dict["price"] = price as NSObject
        dict["hostId"] = hostId as NSObject
        dict["date"] = date as NSObject
        dict["description"] = descriptionEvent as NSObject
        dict["maxParticipants"] = maxParticipants as NSObject
        dict["participants"] = participants as NSObject
        dict["age"] = age as NSObject

        return dict as NSDictionary
    }
    
    required init(coder aDecoder: NSCoder) {
        id = aDecoder.decodeObject(forKey: "id") as! Int
        name = aDecoder.decodeObject(forKey: "name") as! String
        location = aDecoder.decodeObject(forKey: "location") as! Location
        category = aDecoder.decodeObject(forKey: "category") as! String
        price = aDecoder.decodeObject(forKey: "price") as! Int
        hostId = aDecoder.decodeObject(forKey: "hostId") as! Int
        date = aDecoder.decodeObject(forKey: "date") as! Date
        descriptionEvent = aDecoder.decodeObject(forKey: "description") as! String
        maxParticipants = aDecoder.decodeObject(forKey: "maxParticipants") as! Int
        participants = aDecoder.decodeObject(forKey: "participants") as! Int
        age = aDecoder.decodeObject(forKey: "age") as! Int
        
        

    }
    
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(id, forKey: "id")
        aCoder.encode(name, forKey: "name")
        aCoder.encode(location, forKey: "location")
        aCoder.encode(category, forKey: "category")
        aCoder.encode(price, forKey: "price")
        aCoder.encode(hostId, forKey: "hostId")
        aCoder.encode(date, forKey: "date")
        aCoder.encode(descriptionEvent, forKey: "description")
        aCoder.encode(maxParticipants, forKey: "maxParticipants")
        aCoder.encode(participants, forKey: "participants")
        aCoder.encode(age, forKey: "age")

    }
}
