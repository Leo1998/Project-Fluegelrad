import Foundation

class Event: NSObject {
    var id:Int?
    var location:String?
    var category:String?
    var price:Int?
    var host:String?
    var date:Date?
    var descriptionEvent:String?
    
    override init() {
        
    }
    
    init(id: Int, location: String, category: String, price: Int, host: String, date: Date, descriptionEvent: String){
        self.id = id
        self.location = location
        self.category = category
        self.price = price
        self.host = host
        self.date = date
        self.descriptionEvent = descriptionEvent
    }
    
    override var description: String{
        return "ID: \(id), Location: \(location), Category: \(category), Price: \(price), Host: \(host), Date: \(date), DescriptionEvent \(descriptionEvent)"
    }
}
