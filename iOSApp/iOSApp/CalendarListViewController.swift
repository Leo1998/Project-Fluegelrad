import UIKit

class CalendarListViewController: UIViewController, UITableViewDelegate, UITableViewDataSource  {

    private var calendarListView: CalendarListView!
    
    private var shownEvents = [Event]()
    
    private var dayEvent: Event?
    
    private var frame: CGRect!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        frame = view.frame
        
        reset()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    private func setupEvents(){
        let eventData = UserDefaults.standard.object(forKey: "events")
        let events = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
        
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
        let cell:CalendarListCell = calendarListView.eventTable.dequeueReusableCell(withIdentifier: "cell")! as! CalendarListCell
        
        let event:Event = (shownEvents[indexPath.row] )
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE dd.MM.YYYY HH:mm"
        
        cell.locationLabel.text = event.location.title
        cell.dateLabel.text = dateFormatter.string(from: event.dateStart)
        cell.hostLabel.text = String(event.hostId)
        
        return cell
    }
    
    internal func refresh(){
        print("Refresh")
        
        MainViewController.refresh()
        
        setupEvents()
        calendarListView.eventTable.reloadData()
        
        calendarListView.refreshControl.endRefreshing()
    }
    
    public func reset(){
        if calendarListView != nil {
            calendarListView.removeFromSuperview()
        }
        
        setupEvents()
        
        calendarListView = CalendarListView(frame: frame)
        calendarListView.eventTable.delegate = self
        calendarListView.eventTable.dataSource = self
        view.addSubview(calendarListView)

        
        calendarListView.refreshControl.addTarget(self, action: #selector(CalendarListViewController.refresh), for: .valueChanged)
        
    }
}
