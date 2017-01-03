import UIKit

class CalendarListView: UIView {
    
    private(set) var eventTable: UITableView!
    private(set) var refreshControl: UIRefreshControl!

    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        
        eventTable = UITableView(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: frame.size.height))
        eventTable.register(CalendarListCell.self, forCellReuseIdentifier: "cell")
        addSubview(eventTable)
        
        refreshControl = UIRefreshControl()
        eventTable.addSubview(refreshControl)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
