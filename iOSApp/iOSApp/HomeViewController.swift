import UIKit

class HomeViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

	/**
	Table View for the events
	*/
	private var eventTable: UITableView!
	
	/**
	Pull to refresh control
	*/
	private var refreshControl: UIRefreshControl!
	
	/**
	events which are shown to the user
	*/
    private var shownEvents = [Event]()
	
	/**
	titles to the events which are shown
	*/
	private var titleEvents = [Event: String]()
	
	/**
	all sponsors
	*/
	private var sponsors = [Int: Sponsor]()
	
	/**
	temporal reference to an evnet to pass it to the CalendarDayViewController on tap
	*/
    private var dayEvent: Event?
	
	/**
	frame for the table content
	*/
	private var frame: CGRect!
	
    override func viewDidLoad() {
        super.viewDidLoad()
		
		frame = CGRect(x: 0, y: 0, width: view.frame.width, height: view.frame.height)
		
		setupEvents()
		
		eventTable = UITableView()
		eventTable.register(HomeViewCell.self, forCellReuseIdentifier: "cell")
		eventTable.separatorColor = UIColor.primary()
		view.addSubview(eventTable)
		eventTable.translatesAutoresizingMaskIntoConstraints = false
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: topLayoutGuide, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		eventTable.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		eventTable.delegate = self
		eventTable.dataSource = self
		
		// estimated size because the host pictures height inside the cell is frame.height/10
		eventTable.estimatedRowHeight = frame.height/10 * 2
		
		refreshControl = UIRefreshControl()
		eventTable.addSubview(refreshControl)
		refreshControl.addTarget(self, action: #selector(HomeViewController.refresh), for: .valueChanged)

		eventTable.tableFooterView = UIView()
		
		NotificationCenter.default.addObserver(self, selector: #selector(HomeViewController.reset), name: Notification.Name(Bundle.main.bundleIdentifier! + "downloaded"), object: nil)
		
		let butt = UIButton()
		butt.setImage(#imageLiteral(resourceName: "ic_rate_review_white"), for: .normal)
		butt.setTitle("Bewerten", for: .normal)
		butt.sizeToFit()
		butt.addTarget(self, action: #selector(HomeViewController.rate), for: .touchUpInside)
		
		
		navigationItem.setRightBarButton(UIBarButtonItem(customView: butt), animated: false)
    }
	
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	override func viewWillAppear(_ animated: Bool) {
		navigationController?.setNavigationBarHidden(false, animated: false)
	}
	
	/**
	Gets all events and sponsors
	decides which events to show and which title they have
	*/
    private func setupEvents(){
		sponsors = Storage.getSponsors()
		
		let events = Storage.getEvents()
		
		if events.count > 0 && sponsors.count > 0 {
			shownEvents = events.sorted(by: {
				(event1, event2) -> Bool in
            
				let date1 = (event1 ).dateStart
				let date2 = (event2 ).dateStart
            
				return (date1!.compare(date2!)) == ComparisonResult.orderedAscending
            
			})
		
			// deltes all outdated events
			let today = Date()
			shownEvents = shownEvents.filter(){event in
				return (event).dateStart.compare(today) == ComparisonResult.orderedDescending
			}
		
			var newShownEvents = [Event]()
		
			if shownEvents.count >= 1 {
				newShownEvents.append(shownEvents[0])
				titleEvents[shownEvents[0]] = "Das nächste Event"
				shownEvents.remove(at: 0)
			}
			
			if shownEvents.count >= 1 {
				newShownEvents.append(shownEvents[0])
				titleEvents[shownEvents[0]] = "Das übernächste Event"
				shownEvents.remove(at: 0)
			}

			if shownEvents.count >= 1 {
				var eventTemp = shownEvents[0]
				var indexTemp = 0
				for (index, value) in shownEvents.enumerated() {
					if value.participants > eventTemp.participants {
						eventTemp = value
						indexTemp = index
					}
				}
				newShownEvents.append(eventTemp)
				titleEvents[eventTemp] = "Das beliebteste Event"
				shownEvents.remove(at: indexTemp)
				
				if shownEvents.count >= 1 {
					var secondEventTemp = shownEvents[0]
					for value in shownEvents {
						if value.participants > eventTemp.participants{
							secondEventTemp = value
						}
					}
					newShownEvents.append(secondEventTemp)
					titleEvents[secondEventTemp] = "Das nächste beliebteste Event"
					
				}
			}
			
			
			
			shownEvents = newShownEvents
		}
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        dayEvent = shownEvents[indexPath.item]
        performSegue(withIdentifier: "CalendarDayViewController", sender: self)
    }
	
	/**
	sets the events and the sponsors of the CalendarDayViewController
	*/
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
        let cell:HomeViewCell = eventTable.dequeueReusableCell(withIdentifier: "cell")! as! HomeViewCell
		let event:Event = (shownEvents[indexPath.row] )
		
		cell.selectionStyle = .none
		
		cell.separatorInset = UIEdgeInsetsMake(0, 8, 0, 8)

		cell.titleLabel.text = titleEvents[event]
		
		var imageTemp = sponsors[event.hostId]?.image
		
		if imageTemp != nil {
			let size = CGSize(width: (imageTemp?.size.width)! * ((frame.height/10) / (imageTemp?.size.height)!), height: frame.height/10)
			
			UIGraphicsBeginImageContext(size)
			imageTemp?.draw(in: CGRect(origin: .zero, size: size))
			
			imageTemp = UIGraphicsGetImageFromCurrentImageContext()!
			UIGraphicsEndImageContext()
			
			cell.imageV.image = imageTemp
			cell.imageV.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: (imageTemp?.size.width)!, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: (imageTemp?.size.height)!)
		}
		
		
		cell.hostNameLabel.text = sponsors[event.hostId]?.name
		cell.hostNameLabel.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: cell.imageV, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE dd.MM.YYYY 'um' HH:mm"
        
        cell.dateLabel.text = "Das Event startet am \(dateFormatter.string(from: event.dateStart!))"
		cell.dateLabel.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: cell.hostNameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		cell.dateLabel.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.hostNameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		if event.ageMin == 0 && event.ageMax < 99 {
			cell.ageLabel.text = "Für jeden bis zu \(event.ageMax!) Jahren"
		}else if event.ageMin > 0 && event.ageMax == 99 {
			cell.ageLabel.text = "Für jeden ab \(event.ageMin!) Jahren"
		}else if event.ageMin == 0 && event.ageMax >= 99 {
			cell.ageLabel.text = "Es gibt keine Alterbeschränkung"
		}else{
			cell.ageLabel.text = "Für jeden ab \(event.ageMin!) Jahren und bis zu \(event.ageMax!) Jahren"
		}
		cell.ageLabel.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: cell.dateLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		if event.price == 0 {
			cell.priceLabel.text = "Der Eintritt ist kostenlos"

		}else {
			cell.priceLabel.text = "Der Eintritt kostet \(event.price!)€"
		}
		cell.priceLabel.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: cell.ageLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		
		if event.images.count >= 1 {
			cell.eventImage = ImageView(frame: cell.contentView.frame, eventImage: event.images[0])
			cell.contentView.addSubview(cell.eventImage)
			cell.eventImage.translatesAutoresizingMaskIntoConstraints = false
			cell.eventImage.addConstraintsXY(xView: cell.priceLabel, xSelfAttribute: .top, xViewAttribute: .bottom, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .leading, yViewAttribute: .leading, yMultiplier: 1, yConstant: 0)
			cell.eventImage.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .bottom, xViewAttribute: .bottom, xMultiplier: 1, xConstant: -75, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: cell.eventImage.height)
		}else{
			cell.priceLabel.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		}
		
		
		cell.layoutIfNeeded()
		
        return cell
    }
	
	func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
		return UITableViewAutomaticDimension
	}
	
	func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
		return UITableViewAutomaticDimension
	}
	
	/**
	Forwarding the refresh to the MainViewController
	*/
    internal func refresh(){
        print("Refresh")
        
        Storage.refresh()
		
		refreshControl.endRefreshing()
    }
	
	/**
	(Re)loads all the events and sponsors
	*/
    public func reset(){
        setupEvents()
		
		DispatchQueue.main.sync {
			eventTable.reloadData()
		}
    }
	
	internal func rate(){
		performSegue(withIdentifier: "RatingViewController", sender: self)
	}
}
