import Foundation

protocol DatabaseManagerProtocol: class {
    func itemsDownloaded(items: [Event])
}

class DatabaseManager: NSObject, URLSessionDataDelegate{
    weak var delegate: DatabaseManagerProtocol!
    
    private var data: NSMutableData = NSMutableData()
    
    private var url: String = "http://fluegelrad.ddns.net/"
    private let getDatabase = "recieveDatabase.php"
    private let createUser =  "createUser.php"
    
    private var events = [Event]()
    
    private var user: User!
    
    private var session: URLSession!
    private let configuration = URLSessionConfiguration.default
    
    func downloadItems() {
        session = URLSession(configuration: configuration, delegate: self, delegateQueue: nil)

        
        let userData = UserDefaults.standard.object(forKey: "user")
        
        if userData == nil {
            newUser()
        }else{
            user = NSKeyedUnarchiver.unarchiveObject(with: userData as! Data) as! User
            
            getEvents()
        }
    }
    
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive data: Data) {
        self.data.append(data);
        
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?){
        if error != nil {
            print("Failed to download data")
            
            let tempArray = UserDefaults.standard.array(forKey: "events")
            if tempArray != nil {
                events = tempArray as! [Event]
            }else{
                events = [Event]()
            }

        }else {
            if task.currentRequest?.url?.absoluteString == url + createUser{
                print("User created")

                var jsonResult: NSArray = NSArray()

                do{
                    jsonResult = try JSONSerialization.jsonObject(with: self.data as Data, options:JSONSerialization.ReadingOptions.allowFragments) as! NSArray
                } catch let error as NSError {
                    print(error)
                }
                
                user = User(id: jsonResult[0] as! Int, token: jsonResult[1] as! String)
                
                getEvents()
            }else{
                print("Data downloaded")
                self.parseJSON()
            }
        }
        
        if task.currentRequest?.url?.absoluteString != url + createUser && String(data: data as Data, encoding: .utf8) != "" {
            let queue = DispatchQueue(label: "de.project.iOSApp.DatabaseManager", qos: .utility, target: nil)
            queue.async {
                self.delegate.itemsDownloaded(items: self.events)
            }
        }
        
        data = NSMutableData()
    }
    
    private func newUser(){
        var url: URL!
        url = URL(string: self.url + createUser)!
        
        let task = session.dataTask(with: url)
        task.resume()
    }
    
    private func getEvents(){
        let url1 = self.url + getDatabase
        let url2 = "?u=" + String(user.id) + "&t=" + user.token
        let url = URL(string: url1 + url2)!
        
        let task = session.dataTask(with: url)
        task.resume()
    }

    private func parseJSON() {
        let jsonDataAll = String(data: data as Data, encoding: .utf8)
        
        
        if (jsonDataAll == "Invalid Token" || jsonDataAll == "Unknown ID") {
            newUser()
            
            data = NSMutableData()
        }else{
        
            let jsonDataToken = jsonDataAll?.substring(to: (jsonDataAll?.characters.index(after: (jsonDataAll?.characters.index(of: "]"))!))!)

            var jsonDataEvents = jsonDataAll?.substring(from: (jsonDataAll?.characters.index(of: "]"))!)
            jsonDataEvents = jsonDataEvents?.substring(from: (jsonDataEvents?.characters.index(of: "["))!)
        
            var jsonResultEvent: NSArray = NSArray()
            var jsonResultToken: NSArray = NSArray()

            do{
                jsonResultToken = try JSONSerialization.jsonObject(with: (jsonDataToken?.data(using: .utf8))!, options:JSONSerialization.ReadingOptions.allowFragments) as! NSArray
                jsonResultEvent = try JSONSerialization.jsonObject(with: (jsonDataEvents?.data(using: .utf8))!, options:JSONSerialization.ReadingOptions.allowFragments) as! NSArray
            } catch let error as NSError {
                print(error)
            }
        
            var jsonElement: NSMutableDictionary = NSMutableDictionary()
            let events: NSMutableArray = NSMutableArray()
        
            for i in 0 ..< jsonResultEvent.count{
            
                jsonElement = (jsonResultEvent[i] as! NSDictionary).mutableCopy() as! NSMutableDictionary

            
                let event = Event.init(dict: jsonElement)
                events.add(event)
            
            
            }
        
            user.token = jsonResultToken[0] as! String
        
            UserDefaults.standard.set(NSKeyedArchiver.archivedData(withRootObject: user), forKey: "user")
            UserDefaults.standard.synchronize()
        
            self.events = events as NSArray as! [Event]
        }

    }
}
