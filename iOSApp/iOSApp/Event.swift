import Foundation

class Event: NSObject {
    var id:Int!
    var location:String!
    var category:String!
    var price:Int!
    var host:String!
    var date:Date!
    var descriptionEvent:String!
    
    init(id: Int, location: String, category: String, price: Int, host: String, date: Date, descriptionEvent: String){
        self.id = id
        self.location = location
        self.category = category
        self.price = price
        self.host = host
        self.date = date
        self.descriptionEvent = descriptionEvent
    }
    
    init(dict: NSDictionary) {
        self.id = dict.object(forKey: "id") as! Int
        self.location = dict.object(forKey: "location") as! String
        self.category = dict.object(forKey: "category") as! String
        self.price = dict.object(forKey: "price") as! Int
        self.host = dict.object(forKey: "host") as! String
        self.date = dict.object(forKey: "date") as! Date
        self.descriptionEvent = dict.object(forKey: "descriptionEvent") as! String

    }
    
    override var description: String{
        return "ID: \(id), Location: \(location), Category: \(category), Price: \(price), Host: \(host), Date: \(date), DescriptionEvent \(descriptionEvent)"
    }
    
    func getDictonary() -> NSDictionary {
        var dict = [String:NSObject]()
        dict["id"] = id as NSObject
        dict["location"] = location as NSObject
        dict["category"] = category as NSObject
        dict["price"] = price as NSObject
        dict["host"] = host as NSObject
        dict["date"] = date as NSObject
        dict["descriptionEvent"] = descriptionEvent as NSObject
        
        return dict as NSDictionary
    }
}
