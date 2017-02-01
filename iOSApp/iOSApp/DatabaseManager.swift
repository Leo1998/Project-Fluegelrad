import UIKit

/**
A Protocol to send the data to the MainViewController
*/
protocol DatabaseManagerProtocol: class {
	func itemsDownloaded(events: [Event], sponsors: [Int: Sponsor])
	func error()
	func participation(status: ParticipationStatus)
	
	var user: User! {get set}
}

class DatabaseManager: NSObject, URLSessionDataDelegate{
	
	/**
	Reference to the delegate
	*/
	public var delegate: DatabaseManagerProtocol!
	
	/**
	Data which is recieved
	*/
    private var data: NSMutableData = NSMutableData()
	
	/**
	Standard url of the server
	*/
    static var url: String = "http://fluegelrad.ddns.net/"
	
	/**
	file to access the database
	*/
    private let getDatabase = "scripts/getEvents.php"
	
	/**
	file to create a new User
	*/
    private let createUser = "scripts/createUser.php"
	
	/**
	file to send a participation
	*/
	private let participate = "scripts/participate.php"
	
	/**
	file to send a rate for an event
	*/
	private let rate = "scripts/rate.php"
	
	/**
	Save of all events
	*/
    private var events = [Event]()
	
	/**
	Save of all sponsors
	*/
	private var sponsors = [Int: Sponsor]()
	
	/**
	Number of failed tries
	*/
	private var tries = 0
	
	/**
	Method to (re)download the events and sponsors
	*/
    public func downloadItems() {
		
        if delegate.user == nil {
            newUser()
        }else{
            getEvents()
        }
    }
	
	/**
	Saves the recieved Data
	*/
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive data: Data) {
        self.data.append(data);
    }
	
	/**
	Processes the data
	*/
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?){
		if error != nil {
			print((error as! NSError).localizedDescription)
			retry(task: task)
		} else {
			let jsonString = String(data: data as Data, encoding: .utf8)!
			
			if jsonString.contains(error: .connectionFail) {
				retry(task: task)
			}else if jsonString.contains(error: .tooManyReq) {
				
				let firstPart = jsonString.replacingOccurrences(of: "Error: Please wait ", with: "")
				let final = firstPart.replacingOccurrences(of: " seconds before trying again", with: "")
				
				if final != "" {
					let waitFor = Int(final)
					
					DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + DispatchTimeInterval.seconds(waitFor!), execute: {
						self.retry(task: task)
					})
				} else {
					retry(task: task)
				}
				
			}else if jsonString.contains(error: .userIdMissing) {
				print(jsonString)
				newUser()
				retry(task: task)
			}else if jsonString.contains(error: .tokenMissing) {
				print(jsonString)
				newUser()
				retry(task: task)
			}else if jsonString.contains(error: .eventIdMissing) {
				print(jsonString)
				getEvents()
			}else if jsonString.contains(error: .unkownId) {
				print(jsonString)
				newUser()
				retry(task: task)
			}else if jsonString.contains(error: .invalidToken) {
				print(jsonString)
				newUser()
				retry(task: task)
			}else if jsonString.contains(error: .alreadyPart) {
				self.delegate.participation(status: participationToken(jsonString: jsonString))
			}else if jsonString.contains(error: .maxPartReached) {
				self.delegate.participation(status: participationToken(jsonString: jsonString))
			}else{
				tries = 0
				
				if task.currentRequest?.url?.absoluteString == DatabaseManager.url + createUser{
					print("User created")
					
					var jsonResult: NSArray = NSArray()
					
					do{
						jsonResult = try JSONSerialization.jsonObject(with: self.data as Data, options:JSONSerialization.ReadingOptions.allowFragments) as! NSArray
					} catch let error as NSError {
						print(error)
					}
					
					delegate.user = User(id: jsonResult[0] as! Int, token: jsonResult[1] as! String)
					
					getEvents()
				}else if (task.currentRequest?.url?.absoluteString.contains(DatabaseManager.url + participate))!{
					print("Participate")
					
					self.delegate.participation(status: participationToken(jsonString: jsonString))
					
					getEvents()
					
				}else if (task.currentRequest?.url?.absoluteString.contains(DatabaseManager.url + getDatabase))!{
					print("Data downloaded")
					self.parseJSON()
				}else if (task.currentRequest?.url?.absoluteString.contains(DatabaseManager.url + rate))!{
					print("Rated")
					rateToken(jsonString: jsonString)
				}
			}
		}
        data = NSMutableData()
    }

	/**
	Retry the task
	*/
	private func retry(task: URLSessionTask){
		print("Try number \(tries)")
		
		if tries < 3 {
			tries += 1
			
			let session = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
	
		
			let taskNew = session.dataTask(with: (task.currentRequest?.url)!)
			taskNew.resume()
		} else {
			tries = 0
			
			if task.currentRequest?.url?.absoluteString == DatabaseManager.url + getDatabase {
				sponsors = Storage.getSponsors()
				
				events = Storage.getEvents()
				
				self.delegate.itemsDownloaded(events: self.events, sponsors: self.sponsors)
				
				self.delegate.error()
			}

		}
	}
	
	/**
	Create a new User
	*/
    private func newUser(){
		let session = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
		
        var url: URL!
        url = URL(string: DatabaseManager.url + createUser)!
        
        let task = session.dataTask(with: url)
        task.resume()
    }
	
	/**
	Download all events and sponsors
	*/
    private func getEvents(){
		let session = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
		
        let url1 = DatabaseManager.url + getDatabase
        let url2 = "?u=" + String(delegate.user.id) + "&t=" + delegate.user.token
        let url = URL(string: url1 + url2)!
        
        let task = session.dataTask(with: url)
        task.resume()
    }

	/**
	Parsing the Data from the events and sponsors
	*/
    private func parseJSON() {
        let jsonDataAll = String(data: data as Data, encoding: .utf8)
        let jsonResult: NSDictionary = stringToJSonArray(jsonString: jsonDataAll!)
        let events: NSMutableArray = NSMutableArray()
		let sponsors: NSMutableArray = NSMutableArray()

		
        let jsonResultEvent: NSArray = jsonResult.object(forKey: "events") as! NSArray
		let jsonResultSponsors: NSArray = jsonResult.object(forKey: "sponsors") as! NSArray

		
        var jsonElement: NSMutableDictionary = NSMutableDictionary()
        for i in 0 ..< jsonResultEvent.count{
            jsonElement = (jsonResultEvent[i] as! NSDictionary).mutableCopy() as! NSMutableDictionary
            
            let event = Event(dict: jsonElement)
            events.add(event)
        }
		self.events = events as NSArray as! [Event]
		
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
	
	/**
	Splitting the data to the NSDictionary with the events and sponsors
	And saving the new user token
	*/
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

        delegate.user.token = jsonResultToken[0] as! String
		delegate.user = delegate.user
        
        return jsonResultEvent
    }
	
	/**
	Participating on an event
	*/
	public func participate(event: Event){
		let session = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
		
		let url1 = DatabaseManager.url + participate
		let url2 = "?k=" + String(event.id) + "&u=" + String(delegate.user.id) + "&t=" + delegate.user.token
		let url = URL(string: url1 + url2)!
		
		let task = session.dataTask(with: url)
		task.resume()

	}
	
	/**
	save the new user token and returning the status of the participation
	*/
	private func participationToken(jsonString: String) -> ParticipationStatus{
		let jsonDataToken = jsonString.substring(to: (jsonString.characters.index(of: ","))!)
		
		var jsonResultToken: NSArray = NSArray()
		
		do{
			jsonResultToken = try JSONSerialization.jsonObject(with: (jsonDataToken.data(using: .utf8))!, options:JSONSerialization.ReadingOptions.allowFragments) as! NSArray
		} catch let error as NSError {
			print(error)
		}
		
		delegate.user.token = jsonResultToken[0] as! String
		delegate.user = delegate.user
		
				
		if (jsonString.contains("Error: User is already participating")) {
			return .alreadyParticipating
		}else if (jsonString.contains("Error: max participants already reached")) {
			return .maxReached
		}else{
			return .success
		}
	}
	
	/**
	downloads the image
	*/
	public func getImage(path: String) -> UIImage?{
		let url = URL(string: DatabaseManager.url + path)!
		let data = try? Data(contentsOf: url)
		
		if data != nil {
			return UIImage(data: data!)

		}else{
			return nil
		}
	}
	
	/**
	rate an event
	*/
	public func rate(event: Event, rate: Int){
		let session = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
		
		let url1 = DatabaseManager.url + self.rate
		let url2 = "?&u=" + String(delegate.user.id) + "&t=" + delegate.user.token + "&k=" + String(event.id) + "&r=" + String(rate)
		let url = URL(string: url1 + url2)!
		
		let task = session.dataTask(with: url)
		task.resume()
	}
	
	/**
	save the new user token after rate
	*/
	private func rateToken(jsonString: String){
		let jsonDataToken = jsonString.substring(to: (jsonString.characters.index(of: ","))!)
		
		var jsonResultToken: NSArray = NSArray()
		
		do{
			jsonResultToken = try JSONSerialization.jsonObject(with: (jsonDataToken.data(using: .utf8))!, options:JSONSerialization.ReadingOptions.allowFragments) as! NSArray
		} catch let error as NSError {
			print(error)
		}
		
		delegate.user.token = jsonResultToken[0] as! String
		delegate.user = delegate.user
	}
}
