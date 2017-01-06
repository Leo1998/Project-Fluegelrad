import UIKit

protocol DatabaseManagerProtocol: class {
	func itemsDownloaded(events: [Event], sponsors: [Int: Sponsor])
	func error()
}

class DatabaseManager: NSObject, URLSessionDataDelegate{
    weak var delegate: DatabaseManagerProtocol!
    
    private var data: NSMutableData = NSMutableData()
    
    static var url: String = "http://fluegelrad.ddns.net/"
    private let getDatabase = "recieveDatabase.php"
    private let createUser = "createUser.php"
    
    private var events = [Event]()
	private var sponsors = [Int: Sponsor]()
	
	private var tries = 0
    
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
        if error != nil || (tries == 3 && (String(data: data as Data, encoding: .utf8)?.contains("Error"))!) {
            print("Failed to download data : \(error?.localizedDescription))")
			
			let sponsorData = UserDefaults.standard.object(forKey: "sponosrs")
			
			if sponsorData != nil {
				sponsors = NSKeyedUnarchiver.unarchiveObject(with: sponsorData as! Data) as! [Int: Sponsor]
			}
			
			
            let tempArray = UserDefaults.standard.object(forKey: "events")
            
            if tempArray != nil {
                events = NSKeyedUnarchiver.unarchiveObject(with: tempArray as! Data) as! [Event]

            }else{
                events = [Event]()
            }
            
			self.delegate.itemsDownloaded(events: self.events, sponsors: self.sponsors)
			
			self.delegate.error()
		}else if (String(data: data as Data, encoding: .utf8)?.contains("Error"))! {
			tries += 1
			print("Failed \(tries) times")
			
			newUser()
			
        }else {
            if task.currentRequest?.url?.absoluteString == DatabaseManager.url + createUser{
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
        
        data = NSMutableData()
    }
    
    private func newUser(){
        var url: URL!
        url = URL(string: DatabaseManager.url + createUser)!
        
        let task = session.dataTask(with: url)
        task.resume()
    }
    
    private func getEvents(){
        let url1 = DatabaseManager.url + getDatabase
        let url2 = "?u=" + String(user.id) + "&t=" + user.token
        let url = URL(string: url1 + url2)!
        
        let task = session.dataTask(with: url)
        task.resume()
    }

    private func parseJSON() {
        let jsonDataAll = String(data: data as Data, encoding: .utf8)
        let jsonResult: NSDictionary = stringToJSonArray(jsonString: jsonDataAll!)
        let events: NSMutableArray = NSMutableArray()
		let sponsors: NSMutableArray = NSMutableArray()

		
        let jsonResultEvent: NSArray = jsonResult.object(forKey: "events") as! NSArray
        let jsonResultImages: NSArray = jsonResult.object(forKey: "images") as! NSArray
		let jsonResultSponsors: NSArray = jsonResult.object(forKey: "sponsors") as! NSArray

		
        var jsonElement: NSMutableDictionary = NSMutableDictionary()
        for i in 0 ..< jsonResultEvent.count{
            jsonElement = (jsonResultEvent[i] as! NSDictionary).mutableCopy() as! NSMutableDictionary
            
            let event = Event(dict: jsonElement)
            events.add(event)
        }
		self.events = events as NSArray as! [Event]


		
        for i in 0 ..< jsonResultImages.count{
            jsonElement = (jsonResultImages[i] as! NSDictionary).mutableCopy() as! NSMutableDictionary
            
            for item in events{
                if (item as! Event).id == Int(jsonElement.object(forKey: "eventId") as! String)!{
                    (item as! Event).addImage(dict: jsonElement)
                }
            }
        }
		
		
		for i in 0 ..< jsonResultSponsors.count{
			jsonElement = (jsonResultSponsors[i] as! NSDictionary).mutableCopy() as! NSMutableDictionary
			
			let sponsor = Sponsor(dict: jsonElement)
			sponsors.add(sponsor)
		}
		for value in sponsors{
			let sponsorTemp = (value as! Sponsor)
			self.sponsors[sponsorTemp.id] = sponsorTemp
		}

		
        self.delegate.itemsDownloaded(events: self.events, sponsors: self.sponsors)
    }
    
    private func stringToJSonArray(jsonString: String) -> NSDictionary{
        let jsonDataToken = jsonString.substring(to: (jsonString.characters.index(of: ","))!)
        
        let jsonDataEvents = jsonString.substring(from: (jsonString.characters.index(after: jsonString.characters.index(of: ",")!)))
        
        var jsonResultEvent: NSDictionary = NSDictionary()
        var jsonResultToken: NSArray = NSArray()
        
        do{
            jsonResultToken = try JSONSerialization.jsonObject(with: (jsonDataToken.data(using: .utf8))!, options:JSONSerialization.ReadingOptions.allowFragments) as! NSArray
            jsonResultEvent = try JSONSerialization.jsonObject(with: (jsonDataEvents.data(using: .utf8))!, options:JSONSerialization.ReadingOptions.allowFragments) as! NSDictionary
        } catch let error as NSError {
            print(error)
        }

        user.token = jsonResultToken[0] as! String
        
        UserDefaults.standard.set(NSKeyedArchiver.archivedData(withRootObject: user), forKey: "user")
        UserDefaults.standard.synchronize()
        
        return jsonResultEvent
    }
    
    public static func loadImage(url: String, view: UIImageView){
        
        let url = URL(string: self.url + url)!
        
        let task = URLSession.shared.dataTask(with: url) { (responseData, responseUrl, error) -> Void in
            if let data = responseData{
                view.image = UIImage(data: data)
            }
        }
        
        task.resume()
    }
}
