import UIKit
import MapKit
import EventKit

class CalendarDayViewController: UIViewController, MKMapViewDelegate  {
	
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
	A refference to itself to use in static methods
	*/
	private static var selfish: CalendarDayViewController!
	
	/**
	the picker for setting the notification delay
	*/
	private static var picker: NotificationDelayPicker!

	
    override func viewDidLoad() {
        super.viewDidLoad()
		
		CalendarDayViewController.selfish = self
		
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
		
		CalendarDayViewController.picker = NotificationDelayPicker(frame: UIScreen.main.bounds)
		view.addSubview(CalendarDayViewController.picker)
		CalendarDayViewController.picker.isHidden = true
		
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
        imageView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: participationView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 5)
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
		hostView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: hostLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 5)
		hostView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: hostView.height())
		
		sponsorView = UIView()
		sponsorView.translatesAutoresizingMaskIntoConstraints = false
		var sponsorViews = [SponsorViewButton]()
		
		var sponsorViewsHeight: CGFloat = 0
		
		let sponsorLabel = UILabel()
		if event.sponsorIds.count >= 1 {
			sponsorLabel.text = "Sponsoren"
			sponsorLabel.translatesAutoresizingMaskIntoConstraints = false
			sponsorView.addSubview(sponsorLabel)
			sponsorLabel.addConstraintsXY(xView: sponsorView, xSelfAttribute: .centerX, xViewAttribute: .centerX, xMultiplier: 1, xConstant: 0, yView: sponsorView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
			
			sponsorLabel.layoutIfNeeded()
			sponsorViewsHeight += sponsorLabel.frame.height
		}
		
		
		
		for (index, item) in event.sponsorIds.enumerated() {
			let sponsorTemp = SponsorViewButton(frame: frame, sponsor: sponsors[item]!)
			sponsorTemp.tag = item
			sponsorTemp.addTarget(self, action: #selector(CalendarDayViewController.sponsorTap), for: .touchUpInside)
			
			sponsorTemp.translatesAutoresizingMaskIntoConstraints = false
			sponsorView.addSubview(sponsorTemp)
			
			if index == 0 {
				sponsorTemp.addConstraintsXY(xView: sponsorView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: sponsorLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
			}else{
				sponsorTemp.addConstraintsXY(xView: sponsorView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: sponsorViews[index-1], ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 5)
				
			}
			
			sponsorTemp.addConstraintsXY(xView: sponsorView, xSelfAttribute: .width, xViewAttribute: .width, xMultiplier: 1, xConstant: 0, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: sponsorTemp.height())
			
			sponsorViewsHeight += sponsorTemp.height()
			
			sponsorViews.append(sponsorTemp)
		}
		scrollView.addSubview(sponsorView)
		sponsorView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: hostView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 5)
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
        mapView.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: sponsorView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 10)
        mapView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: frame.width/2)

        
        view.layoutIfNeeded()
        
        var totalHeight: CGFloat = 0
		for view in scrollView.subviews {
			totalHeight += view.frame.height
		}
		totalHeight -= header.frame.height
        
        scrollView.contentSize = CGSize(width: frame.width, height: totalHeight)
		
		var buttonsTemp = [UIBarButtonItem]()
		buttonsTemp.append(UIBarButtonItem(image: #imageLiteral(resourceName: "ic_share"), style: .plain, target: self, action: #selector(CalendarDayViewController.share)))

		if EKEventStore.authorizationStatus(for: .event) != .denied {
			buttonsTemp.append(UIBarButtonItem(image: #imageLiteral(resourceName: "ic_event_note"), style: .plain, target: self, action: #selector(CalendarDayViewController.saveEvent)))
		}

		navigationItem.setRightBarButtonItems(buttonsTemp, animated: false)
		
		NotificationCenter.default.addObserver(self, selector: #selector(CalendarDayViewController.segueBack), name: Notification.Name(Bundle.main.bundleIdentifier! + "segueBack"), object: nil)

    }
	
	func segueBack(){
		_ = navigationController?.popViewController(animated: false)
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
		Storage.participate(event: event)
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
	gets notified about the participation status
	*/
	static func participation(status: ParticipationStatus){
		if status == .success {
			
			picker.isHidden = false
			
			picker.done.addTarget(selfish, action: #selector(CalendarDayViewController.delayChosen), for: .touchUpInside)
			picker.cancel.addTarget(selfish, action: #selector(CalendarDayViewController.noNotification), for: .touchUpInside)
			
		}else{
			CalendarDayViewController.delayClosed(status: status)
		}

	}
	
	/**
	executed when delay is chosen
	*/
	func delayChosen(){
		let notification = UILocalNotification()
		notification.alertBody = "Test"
		notification.alertAction = "open"
		
		let calendar = Calendar.autoupdatingCurrent
		let dateComponents = calendar.dateComponents([.hour, .minute], from: CalendarDayViewController.picker.picker.date)
		
		var date = Calendar.autoupdatingCurrent.date(byAdding: .hour, value: -dateComponents.hour!, to: event.dateStart, wrappingComponents: false)
		date = Calendar.autoupdatingCurrent.date(byAdding: .minute, value: -dateComponents.minute!, to: date!, wrappingComponents: false)

		
		notification.fireDate = date
		
		UIApplication.shared.scheduleLocalNotification(notification)
		
		CalendarDayViewController.picker.isHidden = true
		CalendarDayViewController.delayClosed(status: .success)
	}
	
	/**
	executed when the user doesn't want to be notified
	*/
	func noNotification(){
		CalendarDayViewController.picker.isHidden = true
		CalendarDayViewController.delayClosed(status: .success)
	}
	
	/**
	executed when the delay window is closed
	*/
	private static func delayClosed(status: ParticipationStatus){
		var alert: UIAlertController

		
		switch status {
		case .success:
			
			alert = UIAlertController(title: "Du hast dich erfolgreich bei dem Event angemeldet", message: nil, preferredStyle: .alert)
			break
		case .alreadyParticipating:
			alert = UIAlertController(title: "Du bist bereits zu diesem Event angelmeldet", message: nil, preferredStyle: .alert)
			break
		case .maxReached:
			alert = UIAlertController(title: "Es gibt keinen freien Platz mehr für dich", message: nil, preferredStyle: .alert)
			break
		}
		
		let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
		alert.addAction(okAction)
		
		selfish.present(alert, animated: true, completion: nil)	}
	
	/**
	shares the event if share is pressed
	*/
    func share(){
		//TODO
        let activityViewController = UIActivityViewController(activityItems: ["Test"], applicationActivities: nil)
        present(activityViewController, animated: true, completion: nil)
    }
	
	/**
	saves the event in the calendar
	*/
	func saveEvent(){
		Storage.saveEventInCalendar(event: event)
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
	(Re)loads all the sponsors and the event data
	*/
	public func reset(){
		
		let events = Storage.getEvents()
		for value in events {
			if value.id == event.id {
				event = value
				break
			}
		}
		
		participationView.updateCurrentParticipants(event: event)
	}
}
