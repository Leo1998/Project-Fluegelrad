import Foundation

protocol DatabaseManagerProtocol: class {
    func itemsDownloaded(items: NSArray)
}

class DatabaseManager: NSObject, URLSessionDataDelegate{
    weak var delegate: DatabaseManagerProtocol!
    
    var data: NSMutableData = NSMutableData()
    
    let urlPath: String = "http://pipigift.ddns.net/dbService.php"
    
    func downloadItems() {
        let url: URL = URL(string: urlPath)!
        var session: URLSession!
        let configuration = URLSessionConfiguration.default
        
        session = URLSession(configuration: configuration, delegate: self, delegateQueue: nil)
        
        let task = session.dataTask(with: url)
        
        task.resume()
    }
    
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive data: Data) {
        self.data.append(data);
        
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        if error != nil {
            print("Failed to download data")
        }else {
            print("Data downloaded")
            self.parseJSON()
        }
        
    }

    func parseJSON() {
        var jsonResult: NSArray = NSArray()
        
        do{
            jsonResult = try JSONSerialization.jsonObject(with: self.data as Data, options:JSONSerialization.ReadingOptions.allowFragments) as! NSArray
        } catch let error as NSError {
            print(error)
            
        }
        
        var jsonElement: NSDictionary = NSDictionary()
        let events: NSMutableArray = NSMutableArray()
        
        for i in 0 ..< jsonResult.count{
            
            jsonElement = jsonResult[i] as! NSDictionary
            
            let event = Event()
            
            if let id = jsonElement["id"] as? String,let location = jsonElement["location"] as? String,let category = jsonElement["category"] as? String,let price = jsonElement["price"] as? String,let host = jsonElement["host"] as? String,let date = jsonElement["date"] as? String,let description = jsonElement["description"] as? String{
                
                event.id = Int(id)
                event.location = location
                event.category = category
                event.price = Int(price)
                event.host = host
                event.date = Date()
                event.descriptionEvent = description
                
            }
            
            events.add(event)
            
        }
        
        let queue = DispatchQueue(label: "de.project.iOSApp.DatabaseManager", qos: .utility, target: nil)
            queue.async {
            self.delegate.itemsDownloaded(items: events)
        }

    }

}
