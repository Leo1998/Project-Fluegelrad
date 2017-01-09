import UIKit
import MapKit
import EventKit

class CalendarDayViewController: UIViewController, MKMapViewDelegate {
	
	/**
	the event which is shown
	*/
    var event: Event!
	
	/**
	All the sponosrs
	*/
	var sponsors: [Int: Sponsor]!
	
	/**
	a header where some of the event information is always shown
	*/
    private var header: CalendarDayViewHeader!
	
	/**
	label with the description of the event
	*/
    private var descriptionLabel: UILabel!
	
	/**
	label with the age of the event
	*/
	private var ageLabel: UILabel!
	
	/**
	label with the price of the event
	*/
    private var priceLabel: UILabel!
	
	/**
	map of the position where the event is held
	*/
    private var mapView: MKMapView!
	
	/**
	label which says host
	*/
	private var hostLabel: UILabel!
	
	/**
	button which is forwarding to the host of the event
	*/
	private var hostView: SponsorViewButton!
	
	/**
	buttons which is forwarding to the sponsors of the event
	*/
	private var sponsorView: UIView!
	
	/**
	button with wich you can participate to the event
	*/
	private var participationView: ParticipationView!
	
	/**
	All the images of the event
	*/
    private var imageView: UIView!
	
	/**
	view to scoll through all the event data
	*/
    private var scrollView: UIScrollView!
	
	/**
	the sponsor which was pressed to segue there
	*/
	private var segueSponsor: Sponsor?
	
	/**
	Pull to refresh control
	*/
	private var refreshControl: UIRefreshControl!
	
    override func viewDidLoad() {
        super.viewDidLoad()
		
		let frame = CGRect(x: 8, y: 0, width: view.frame.width - 16, height: view.frame.height)
		
		navigationController?.setNavigationBarHidden(false, animated: true)
        
        header = CalendarDayViewHeader(event: event, sponsor: sponsors)
        header.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(header)
        header.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        header.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: header.height)
		
		
        scrollView = UIScrollView()
        scrollView.translatesAutoresizingMaskIntoConstraints = false
		scrollView.contentInset = UIEdgeInsetsMake(0, 8, 0, 8)
        view.addSubview(scrollView)
        scrollView.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: header, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        scrollView.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		refreshControl = UIRefreshControl()
		scrollView.addSubview(refreshControl)
		refreshControl.addTarget(self, action: #selector(CalendarDayViewController.refresh), for: .valueChanged)
		NotificationCenter.default.addObserver(self, selector: #selector(CalendarDayViewController.reset), name: Notification.Name(Bundle.main.bundleIdentifier!), object: nil)
		
        descriptionLabel = UILabel()
        descriptionLabel.lineBreakMode = .byWordWrapping
        descriptionLabel.numberOfLines = 0
		descriptionLabel.text = event.descriptionEvent
        descriptionLabel.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(descriptionLabel)
        descriptionLabel.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: scrollView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        descriptionLabel.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: scrollView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		ageLabel = UILabel()
		if event.ageMin == 0 && event.ageMax < 99 {
			ageLabel.text = "Für jeden bis zu \(event.ageMax!) Jahren"
		}else if event.ageMin > 0 && event.ageMax == 99 {
			ageLabel.text = "Für jeden ab \(event.ageMin!) Jahren"
		}else if event.ageMin == 0 && event.ageMax >= 99 {
			ageLabel.text = "Es gibt keine Alterbeschränkung"
		}else{
			ageLabel.text = "Für jeden ab \(event.ageMin!) Jahren und bis zu \(event.ageMax!) Jahren"
		}
		ageLabel.translatesAutoresizingMaskIntoConstraints = false
		scrollView.addSubview(ageLabel)
		ageLabel.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: descriptionLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		priceLabel = UILabel()
		if event.price == 0 {
			priceLabel.text = "Der Eintritt ist kostenlos"
			
		}else {
			priceLabel.text = "Der Eintritt kostet \(event.price!)€"
		}
		priceLabel.translatesAutoresizingMaskIntoConstraints = false
		scrollView.addSubview(priceLabel)
		priceLabel.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: ageLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		participationView = ParticipationView(frame: frame, event: event)
		participationView.participationButton.addTarget(self, action: #selector(CalendarDayViewController.participate), for: .touchUpInside)
		scrollView.addSubview(participationView)
		participationView.translatesAutoresizingMaskIntoConstraints = false
		participationView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: priceLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		participationView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: participationView.height)


        imageView = UIView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        var imageViewsTemp = [ImageView]()
        var imageViewHeight: CGFloat = 0
        for (index, item) in event.images.enumerated() {
			let imageTemp = ImageView(frame: frame, eventImage: item)
            imageTemp.translatesAutoresizingMaskIntoConstraints = false
            imageView.addSubview(imageTemp)

            if index == 0 {
                imageTemp.addConstraintsXY(xView: imageView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: imageView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
            }else{
                imageTemp.addConstraintsXY(xView: imageView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: imageViewsTemp[index-1], ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

            }
            
            imageTemp.addConstraintsXY(xView: imageView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: imageTemp.height)

            imageViewHeight += imageTemp.height
            
            imageViewsTemp.append(imageTemp)
        }
        scrollView.addSubview(imageView)
        imageView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: participationView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        imageView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: imageViewHeight)
		
		hostLabel = UILabel()
		hostLabel.text = "Veranstalter"
		hostLabel.translatesAutoresizingMaskIntoConstraints = false
		scrollView.addSubview(hostLabel)
		hostLabel.addConstraintsXY(xView: scrollView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: imageView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		hostView = SponsorViewButton(frame: frame, sponsor: sponsors[event.hostId]!)
		hostView.addTarget(self, action: #selector(CalendarDayViewController.host), for: .touchUpInside)
		scrollView.addSubview(hostView)
		hostView.translatesAutoresizingMaskIntoConstraints = false
		hostView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: hostLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		hostView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: hostView.height())
		
		sponsorView = UIView()
		sponsorView.translatesAutoresizingMaskIntoConstraints = false
		var sponsorViews = [SponsorViewButton]()
		
		let sponsorLabel = UILabel()
		sponsorLabel.text = "Sponsoren"
		sponsorLabel.translatesAutoresizingMaskIntoConstraints = false
		sponsorView.addSubview(sponsorLabel)
		sponsorLabel.addConstraintsXY(xView: sponsorView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: sponsorView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
		
		sponsorLabel.layoutIfNeeded()
		var sponsorViewsHeight: CGFloat = sponsorLabel.frame.height
		
		for (index, item) in event.sponsorIds.enumerated() {
			let sponsorTemp = SponsorViewButton(frame: frame, sponsor: sponsors[item]!)
			sponsorTemp.tag = item
			sponsorTemp.addTarget(self, action: #selector(CalendarDayViewController.sponsorTap), for: .touchUpInside)
			
			sponsorTemp.translatesAutoresizingMaskIntoConstraints = false
			sponsorView.addSubview(sponsorTemp)
			
			if index == 0 {
				sponsorTemp.addConstraintsXY(xView: sponsorView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: sponsorLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
			}else{
				sponsorTemp.addConstraintsXY(xView: sponsorView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: sponsorViews[index-1], ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
				
			}
			
			sponsorTemp.addConstraintsXY(xView: sponsorView, xSelfAttribute: .width, xViewAttribute: .width, xMultiplier: 1, xConstant: 0, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: sponsorTemp.height())
			
			sponsorViewsHeight += sponsorTemp.height()
			
			sponsorViews.append(sponsorTemp)
		}
		scrollView.addSubview(sponsorView)
		sponsorView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: hostView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		sponsorView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: sponsorViewsHeight)
		
        mapView = MKMapView()
		mapView.translatesAutoresizingMaskIntoConstraints = false
        mapView.delegate = self
        mapView.addAnnotation(event.location)
        mapView.setCenter(event.location.coordinate, animated: true)
        let regionRadius: CLLocationDistance = 0.01
        let region = MKCoordinateRegion(center: self.event.location.coordinate, span: MKCoordinateSpan(latitudeDelta: regionRadius * 2, longitudeDelta: regionRadius * 2))
        mapView.setRegion(region, animated: true)
        scrollView.addSubview(mapView)
        mapView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: sponsorView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        mapView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: frame.width/2)

        
        view.layoutIfNeeded()
        
        var totalHeight: CGFloat = descriptionLabel.frame.height
        totalHeight += imageView.frame.height
		totalHeight += ageLabel.frame.height
        totalHeight += priceLabel.frame.height
        totalHeight += mapView.frame.height
		totalHeight += hostLabel.frame.height
		totalHeight += hostView.frame.height
		totalHeight += participationView.frame.height
		totalHeight += sponsorView.frame.height
        
        scrollView.contentSize = CGSize(width: frame.width, height: totalHeight)
        
        let saveEventButton = UIBarButtonItem(image: #imageLiteral(resourceName: "ic_event_note"), style: .plain, target: self, action: #selector(CalendarDayViewController.saveEvent))
        let shareEventButton = UIBarButtonItem(image: #imageLiteral(resourceName: "ic_share"), style: .plain, target: self, action: #selector(CalendarDayViewController.share))

        navigationItem.setRightBarButtonItems([shareEventButton, saveEventButton], animated: false)
    }
	
	/**
	called when a sponsor button is pressed
	*/
	func sponsorTap(sender: UIButton){
		segueSponsor = sponsors[sender.tag]
		performSegue(withIdentifier: "SponsorViewController", sender: self)
	}
	
	/**
	called when the participation button is pressed
	*/
	func participate(){
		MainViewController.participate(event: event)
	}
	
	/**
	called when the host button is pressed
	*/
	func host(){
		segueSponsor = sponsors[event.hostId]
		performSegue(withIdentifier: "SponsorViewController", sender: self)
	}
	
	override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
		if segue.identifier == "SponsorViewController" {
			let vc = segue.destination as! SponsorViewController
			vc.sponsor = segueSponsor
		}
	}
	
	override func viewWillDisappear(_ animated: Bool) {
		super.viewWillDisappear(animated)
		
		if self.isMovingFromParentViewController {
			navigationController?.setNavigationBarHidden(true, animated: animated)
		}
	}
	
	/**
	checks if event can be localy saved
	*/
    func saveEvent(){
        let eventStore = EKEventStore()
        
        switch EKEventStore.authorizationStatus(for: .event) {
        case .authorized:
            saving(eventStore: eventStore)
            break
        case .denied:
            print("Calendar Access denied")
            break
        case .notDetermined:
            eventStore.requestAccess(to: .event, completion: { (granted, error) in
                if granted {
                    self.saving(eventStore: eventStore)
                }else{
                    print("Calendar Access denied")
                }
            })
            break
        default:
            print("Calendar Access default")
        }
    }
	
	/**
	saves the event to the local calendar
	*/
    private func saving(eventStore: EKEventStore){
		let calendarData = UserDefaults.standard.object(forKey: "calendar")
		let eventCalendarIdentifier = NSKeyedUnarchiver.unarchiveObject(with: calendarData as! Data) as! String
		let eventCalendar = eventStore.calendar(withIdentifier: eventCalendarIdentifier)!
		
        var alreadySaved = false
		
        let predicate = eventStore.predicateForEvents(withStart: event.dateStart, end: event.dateEnd, calendars: [eventCalendar])
        let events = eventStore.events(matching: predicate)
        
        for value in events{
            if value.title == event.name && value.location == event.location.title && value.notes == event.descriptionEvent {
                alreadySaved = true
                
                let alert = UIAlertController(title: "Event im Kalendar schon vorhanden", message: nil, preferredStyle: .alert)
                let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                alert.addAction(okAction)
                
                present(alert, animated: true, completion: nil)
                break
            }
        }

        if !alreadySaved {
            let newEvent = EKEvent(eventStore: eventStore)
            newEvent.calendar = eventCalendar
            newEvent.title = event.name
            newEvent.startDate = event.dateStart
            newEvent.endDate = event.dateEnd
            newEvent.location = event.location.title
            newEvent.notes = event.descriptionEvent
            
            do {
                try eventStore.save(newEvent, span: .thisEvent, commit: true)
                
                let alert = UIAlertController(title: "Event gespeichert", message: nil, preferredStyle: .alert)
                let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                alert.addAction(okAction)
                
                present(alert, animated: true, completion: nil)
            } catch {
                let alert = UIAlertController(title: "Event konnte nicht gespeichert werden", message: error.localizedDescription, preferredStyle: .alert)
                let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                alert.addAction(okAction)
                
                present(alert, animated: true, completion: nil)
            }
        }
    }
	
	/**
	shares the event if share is pressed
	*/
    func share(){
		//TODO
        let activityViewController = UIActivityViewController(activityItems: ["Test"], applicationActivities: nil)
        present(activityViewController, animated: true, completion: nil)
    }
    

    func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView, calloutAccessoryControlTapped control: UIControl) {
        let launchOptions = [MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving]
        event.location.mapItem().openInMaps(launchOptions: launchOptions)
    }
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if let annotation = annotation as? Location {
            let identifier = "pin"
            var view: MKPinAnnotationView
            if let dequeuedView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier) as? MKPinAnnotationView {
                dequeuedView.annotation = annotation
                view = dequeuedView
            } else {
                view = MKPinAnnotationView(annotation: annotation, reuseIdentifier: identifier)
                view.canShowCallout = true
                view.calloutOffset = CGPoint(x: -5, y: 5)
                view.rightCalloutAccessoryView = UIButton(type: .detailDisclosure) as UIView
            }
            return view
        }
        return nil
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
	
	/**
	Forwarding the refresh to the MainViewController
	*/
	internal func refresh(){
		print("Refresh")
		
		MainViewController.refresh()
		
		refreshControl.endRefreshing()
	}
	
	/**
	(Re)loads all the sponsors and the event data
	*/
	public func reset(){
		let sponsorData = UserDefaults.standard.object(forKey: "sponsors")
		
		if sponsorData != nil {
			sponsors = NSKeyedUnarchiver.unarchiveObject(with: sponsorData as! Data) as! [Int: Sponsor]
		}
		
		let eventData = UserDefaults.standard.object(forKey: "events")
		var events = [Event]()
		if eventData != nil {
			events = NSKeyedUnarchiver.unarchiveObject(with: eventData as! Data) as! [Event]
		}
		for value in events {
			if value.id == event.id {
				event = value
				break
			}
		}
		
		DispatchQueue.main.sync {
			updateViews()
		}
	}
	
	/**
	(Re)loads the participationView after refreshing
	*/
	private func updateViews(){
		participationView.updateCurrentParticipants(event: event)
	}
}
