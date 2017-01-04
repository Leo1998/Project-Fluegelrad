import UIKit

class HomeViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    private var homeView: HomeView!
    
    private var shownEvents = [Event]()
	
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
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return shownEvents.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:HomeViewCell = homeView.eventTable.dequeueReusableCell(withIdentifier: "cell")! as! HomeViewCell
        
        let event:Event = (shownEvents[indexPath.row] )
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE dd.MM.YYYY HH:mm"
        
        cell.locationLabel.text = event.location.title
        cell.dateLabel.text = dateFormatter.string(from: event.dateStart)
        cell.hostLabel.text = event.host.name
        
        return cell
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
