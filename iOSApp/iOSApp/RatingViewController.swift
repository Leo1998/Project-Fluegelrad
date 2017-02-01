import UIKit

class RatingViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
	
	/**
	Table View for the events
	*/
	private var eventTable: UITableView!
	
	/**
	rateable events
	*/
	private var rateableEvents = [Event]()
	
	/**
	All the sponosrs
	*/
	private var sponsors = [Int: Sponsor]()



    override func viewDidLoad() {
        super.viewDidLoad()
		
		rateableEvents = Storage.getEventsRatable()
		sponsors = Storage.getSponsors()
		
		eventTable = UITableView()
		eventTable.register(CalendarListViewCell.self, forCellReuseIdentifier: "cell")
		// size because the host pictures height inside the cell is UIScreen.main.bounds.height/10
		eventTable.rowHeight = UIScreen.main.bounds.height / 10
		view.addSubview(eventTable)
		eventTable.separatorColor = UIColor.primary()
		eventTable.translatesAutoresizingMaskIntoConstraints = false
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: topLayoutGuide, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		eventTable.delegate = self
		eventTable.dataSource = self
		eventTable.backgroundColor = UIColor.clear
		eventTable.tableFooterView = UIView()

		let title = UILabel(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 25))
		title.text = "Noch zu bewertende Events"
		title.textAlignment = .center
		title.adjustsFontSizeToFitWidth = true
		
		eventTable.tableHeaderView = title


		NotificationCenter.default.addObserver(self, selector: #selector(RatingViewController.segueBack), name: Notification.Name(Bundle.main.bundleIdentifier! + "segueBack"), object: nil)
		
	}
	
	func segueBack(){
		_ = navigationController?.popViewController(animated: false)
	}

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		return rateableEvents.count
	}
	
	func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		let cell:CalendarListViewCell = eventTable.dequeueReusableCell(withIdentifier: "cell")! as! CalendarListViewCell
		
		cell.selectionStyle = .none
		cell.separatorInset = UIEdgeInsetsMake(0, 8, 0, 8)
		
		let event = rateableEvents[indexPath.row]
		
		var imageTemp = sponsors[event.hostId]?.image
		
		if imageTemp != nil {
			let size = CGSize(width: (imageTemp?.size.width)! * ((UIScreen.main.bounds.height/10) / (imageTemp?.size.height)!), height: UIScreen.main.bounds.height/10)
			
			UIGraphicsBeginImageContext(size)
			imageTemp?.draw(in: CGRect(origin: .zero, size: size))
			
			imageTemp = UIGraphicsGetImageFromCurrentImageContext()!
			UIGraphicsEndImageContext()
			
			cell.imageV.image = imageTemp
			cell.imageV.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: (imageTemp?.size.width)!, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: (imageTemp?.size.height)!)
		}
		
		
		cell.nameLabel.text = event.name
		cell.nameLabel.addConstraintsXY(xView: cell.imageV, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 10)
		cell.nameLabel.addConstraintsXY(xView: cell, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 10)
		
		
		let dateFormatter = DateFormatter()
		dateFormatter.dateFormat = "EEE dd.MM.YYYY 'um' HH:mm"
		
		cell.dateLabel.text = "am \(dateFormatter.string(from: event.dateStart)) Uhr"
		cell.dateLabel.addConstraintsXY(xView: cell.imageV, xSelfAttribute: .leading, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		cell.dateLabel.addConstraintsXY(xView: cell, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		
		return cell
	}

}
