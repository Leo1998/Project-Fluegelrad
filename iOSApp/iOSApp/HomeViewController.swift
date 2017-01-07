import UIKit

class HomeViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    private var homeView: HomeView!
    
    private var shownEvents = [Event]()
	private var titleEvents = [Event: String]()
	private var sponsors = [Int: Sponsor]()
	
    private var dayEvent: Event?
	
    override func viewDidLoad() {
        super.viewDidLoad()
		
		setupEvents()
		
		homeView = HomeView(frame: view.frame)
		view.addSubview(homeView)
		homeView.translatesAutoresizingMaskIntoConstraints = false
		homeView.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: topLayoutGuide, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		homeView.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		homeView.eventTable.delegate = self
		homeView.eventTable.dataSource = self
		
		
		homeView.refreshControl.addTarget(self, action: #selector(HomeViewController.refresh), for: .valueChanged)

		
		NotificationCenter.default.addObserver(self, selector: #selector(HomeViewController.reset), name: Notification.Name(Bundle.main.bundleIdentifier!), object: nil)
    }
	

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    private func setupEvents(){
		let sponsorData = UserDefaults.standard.object(forKey: "sponsors")
		
		if sponsorData != nil {
			sponsors = NSKeyedUnarchiver.unarchiveObject(with: sponsorData as! Data) as! [Int: Sponsor]
		}
		
		
        let eventData = UserDefaults.standard.object(forKey: "events")
		
		var events = [Event]()
		if eventData != nil {
			events = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
		}
		
        shownEvents = events.sorted(by: {
            (event1, event2) -> Bool in
            
            let date1 = (event1 ).dateStart
            let date2 = (event2 ).dateStart
            
            return (date1!.compare(date2!)) == ComparisonResult.orderedAscending
            
        })
        
        let today = Date()
        for (index, value) in events.enumerated(){
            if (value ).dateStart.compare(today) == ComparisonResult.orderedAscending{
                shownEvents.remove(at: index)
            }
        }
		
		var newShownEvents = [Event]()
		
		newShownEvents.append(shownEvents[0])
		titleEvents[shownEvents[0]] = "Das nächste Event"
		
		var eventTemp = shownEvents[1]
		for value in shownEvents {
			if value.participants > eventTemp.participants && value != shownEvents[0] {
				eventTemp = value
			}
		}
		newShownEvents.append(eventTemp)
		titleEvents[eventTemp] = "Das beliebteste Event"
		
		shownEvents = newShownEvents
    }

    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        dayEvent = shownEvents[indexPath.item]
        performSegue(withIdentifier: "CalendarDayViewController", sender: self)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "CalendarDayViewController" {
            let vc = segue.destination as! CalendarDayViewController
            vc.event = dayEvent
            dayEvent = nil
			vc.sponsors = sponsors
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return shownEvents.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:HomeViewCell = homeView.eventTable.dequeueReusableCell(withIdentifier: "cell")! as! HomeViewCell
		let event:Event = (shownEvents[indexPath.row] )

		cell.titleLabel.text = titleEvents[event]
		
		var imageTemp = sponsors[event.hostId]?.image
		
		let size = CGSize(width: (imageTemp?.size.width)! * ((UIScreen.main.bounds.height/10) / (imageTemp?.size.height)!), height: UIScreen.main.bounds.height/10)
		
		UIGraphicsBeginImageContext(size)
		imageTemp?.draw(in: CGRect(origin: .zero, size: size))
		
		imageTemp = UIGraphicsGetImageFromCurrentImageContext()!
		UIGraphicsEndImageContext()
		
		cell.imageV.image = imageTemp
		cell.imageV.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: (imageTemp?.size.width)!, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: (imageTemp?.size.height)!)
		cell.hostNameLabel.text = sponsors[event.hostId]?.name
		cell.hostNameLabel.addConstraintsXY(xView: cell.imageV, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.imageV, ySelfAttribute: .centerY, yViewAttribute: .centerY, yMultiplier: 1, yConstant: 0)
		
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE dd.MM.YYYY 'um' HH:mm"
        
        cell.dateLabel.text = "Das Event startet am \(dateFormatter.string(from: event.dateStart!))"
		cell.dateLabel.addConstraintsXY(xView: cell, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: cell.imageV, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		if event.ageMin == 0 && event.ageMax < 99 {
			cell.ageLabel.text = "Für jeden bis zu \(event.ageMax!) Jahren"
		}else if event.ageMin > 0 && event.ageMax == 99 {
			cell.ageLabel.text = "Für jeden ab \(event.ageMin!) Jahren"
		}else if event.ageMin == 0 && event.ageMax >= 99 {
			cell.ageLabel.text = "Es gibt keine Alterbeschränkung"
		}else{
			cell.ageLabel.text = "Für jeden ab \(event.ageMin!) Jahren und bis zu \(event.ageMax!) Jahren"
		}
		cell.ageLabel.addConstraintsXY(xView: cell, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: cell.dateLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		if event.price == 0 {
			cell.priceLabel.text = "Der Eintritt ist kostenlos"

		}else {
			cell.priceLabel.text = "Der Eintritt kostet \(event.price!)€"
		}
		cell.priceLabel.addConstraintsXY(xView: cell, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.ageLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		cell.priceLabel.addConstraintsXY(xView: cell, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		cell.layoutIfNeeded()
		
        return cell
    }
	
	func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
		return UITableViewAutomaticDimension
	}
	
    
    internal func refresh(){
        print("Refresh")
        
        MainViewController.refresh()
		
		homeView.refreshControl.endRefreshing()
    }
    
    public func reset(){
        setupEvents()
		
		DispatchQueue.main.sync {
			homeView.eventTable.reloadData()
		}
    }
}
