import Foundation

protocol DatabaseManagerProtocol: class {
    func itemsDownloaded(items: [Event])
}

class DatabaseManager: NSObject, URLSessionDataDelegate{
    weak var delegate: DatabaseManagerProtocol!
    
    private var data: NSMutableData = NSMutableData()
    
    private let urlPath: String = "http://pipigift.ddns.net/dbService.php"
    
    private var events = [Event]()
    
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
        events = UserDefaults.standard.array(forKey: "events") as! [Event]
        if error != nil {
            print("Failed to download data")
        }else {
            print("Data downloaded")
            self.parseJSON()
        }
        
        let queue = DispatchQueue(label: "de.project.iOSApp.DatabaseManager", qos: .utility, target: nil)
        queue.async {
            self.delegate.itemsDownloaded(items: self.events)
        }
    }

    private func parseJSON() {
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
            
            
            if let id = jsonElement["id"] as? String,let location = jsonElement["location"] as? String,let category = jsonElement["category"] as? String,let price = jsonElement["price"] as? String,let host = jsonElement["host"] as? String,let dateString = jsonElement["date"] as? String,let description = jsonElement["description"] as? String{
                
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
                let date = dateFormatter.date(from: dateString)
                
                let event = Event(id: Int(id)!, location: location, category: category, price: Int(price)!, host: host, date: date!, descriptionEvent: description)
                events.add(event)
            }
            
            
        }
        
        self.events = events as NSArray as! [Event]

    }
}
