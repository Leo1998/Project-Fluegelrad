import UIKit

class CalendarListView: UIView, UITableViewDelegate, UITableViewDataSource {
    
    private var eventTable: UITableView!
    
    private var events: NSArray!

    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupEvents()
        
        eventTable = UITableView(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: frame.size.height))
        eventTable.delegate = self
        eventTable.dataSource = self
        //eventTable.translatesAutoresizingMaskIntoConstraints = false
        eventTable.register(CalendarListCell.self, forCellReuseIdentifier: "cell")
        
        addSubview(eventTable)

    }
    
    private func setupEvents(){
        if let array: NSArray = UserDefaults.standard.object(forKey: "events") as! NSArray?{
            
            let eventsMutable = NSMutableArray()
            for dict in array {
                eventsMutable.add(Event(dict: dict as! NSDictionary))
            }
            
            events = eventsMutable as NSArray
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return events.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:CalendarListCell = eventTable.dequeueReusableCell(withIdentifier: "cell")! as! CalendarListCell
        
        let event:Event = (events[indexPath.row] as! Event)
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE dd.MM.YYYY HH:mm"
        
        cell.categoryLabel.text = event.category
        cell.locationLabel.text = event.location
        cell.dateLabel.text = dateFormatter.string(from: event.date)
        cell.hostLabel.text = event.host
        
        return cell
    }
}
