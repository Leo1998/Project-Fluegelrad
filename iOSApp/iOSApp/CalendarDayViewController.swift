import UIKit
import MapKit
import EventKit

class CalendarDayViewController: UIViewController, MKMapViewDelegate {
    @IBOutlet var superView: UIView!
    
    var event: Event!
    
    private var header: CalendarDayViewHeader!
    private var descriptionLabel: UILabel!
    private var prizeLabel: UILabel!
    private var mapView: MKMapView!
    
    private var imageView: UIView!
    
    private var scrollView: UIScrollView!
    private var content: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        header = CalendarDayViewHeader(event: event)
        header.translatesAutoresizingMaskIntoConstraints = false
        superView.addSubview(header)
        header.addConstraintsXY(xView: superView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: superView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        header.addConstraintsXY(xView: superView, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: header.height)
        
        scrollView = UIScrollView()
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)
        scrollView.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: header, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        scrollView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: view.frame.width, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

        content = UIView()
        content.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(content)
        content.addConstraintsXY(xView: scrollView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: scrollView, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        content.addConstraintsXY(xView: scrollView, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: scrollView, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        
        
        descriptionLabel = UILabel()
        descriptionLabel.text = event.descriptionEvent
        descriptionLabel.lineBreakMode = .byWordWrapping
        descriptionLabel.numberOfLines = 0
        descriptionLabel.translatesAutoresizingMaskIntoConstraints = false
        content.addSubview(descriptionLabel)
        descriptionLabel.addConstraintsXY(xView: content, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: content, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        descriptionLabel.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: view.frame.width, yView: content, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        
        imageView = UIView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        var imageViewsTemp = [ImageView]()
        var imageViewHeight: CGFloat = 0
        for (index, item) in event.images.enumerated() {
			let imageTemp = ImageView(frame: view.frame, eventImage: item)
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
        content.addSubview(imageView)
        imageView.addConstraintsXY(xView: content, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: descriptionLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        imageView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: view.frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: imageViewHeight)
        
        prizeLabel = UILabel()
        prizeLabel.text = String(event.price)
        prizeLabel.translatesAutoresizingMaskIntoConstraints = false
        content.addSubview(prizeLabel)
        prizeLabel.addConstraintsXY(xView: content, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: imageView, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)

        
        mapView = MKMapView()
        mapView.translatesAutoresizingMaskIntoConstraints = false
        mapView.delegate = self
        mapView.addAnnotation(event.location)
        mapView.setCenter(event.location.coordinate, animated: true)
        let regionRadius: CLLocationDistance = 0.01
        let region = MKCoordinateRegion(center: self.event.location.coordinate, span: MKCoordinateSpan(latitudeDelta: regionRadius * 2, longitudeDelta: regionRadius * 2))
        mapView.setRegion(region, animated: true)
        content.addSubview(mapView)
        mapView.addConstraintsXY(xView: content, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: prizeLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        mapView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: view.frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: view.frame.width/2)

        
        view.layoutIfNeeded()
        
        var totalHeight: CGFloat = descriptionLabel.frame.height
        totalHeight += imageView.frame.height
        totalHeight += prizeLabel.frame.height
        totalHeight += mapView.frame.height
        
        scrollView.contentSize = CGSize(width: view.frame.width, height: totalHeight)
        
        let saveEventButton = UIBarButtonItem(image: #imageLiteral(resourceName: "ic_event_note"), style: .plain, target: self, action: #selector(CalendarDayViewController.saveEvent))
        let shareEventButton = UIBarButtonItem(image: #imageLiteral(resourceName: "ic_share"), style: .plain, target: self, action: #selector(CalendarDayViewController.share))

        navigationItem.setRightBarButtonItems([shareEventButton, saveEventButton], animated: false)
    }
    
    func saveEvent(){
        let eventStore = EKEventStore()
        
        switch EKEventStore.authorizationStatus(for: .event) {
        case .authorized:
            saving(eventStore: eventStore)
            break
        case .denied:
            print("Calndar Access denied")
            break
        case .notDetermined:
            eventStore.requestAccess(to: .event, completion: { (granted, error) in
                if granted {
                    self.saving(eventStore: eventStore)
                }else{
                    print("Calndar Access denied")
                }
            })
            break
        default:
            print("Calndar Access default")
        }
    }
    
    private func saving(eventStore: EKEventStore){
        let eventCalendar = eventStore.defaultCalendarForNewEvents
        eventCalendar.title = "Dortmunder Events"
        eventCalendar.cgColor = UIColor.green.cgColor
        
        var alreadySaved = false
        
        let predicate = eventStore.predicateForEvents(withStart: event.dateStart, end: event.dateEnd, calendars: [eventCalendar])
        let events = eventStore.events(matching: predicate)
        
        for value in events{
            if value.title == event.name && value.location == event.location.title && value.notes == event.descriptionEvent {
                alreadySaved = true
                
                let alert = UIAlertController(title: "Event already saved", message: nil, preferredStyle: .alert)
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
                
                let alert = UIAlertController(title: "Event saved", message: nil, preferredStyle: .alert)
                let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                alert.addAction(okAction)
                
                present(alert, animated: true, completion: nil)
            } catch {
                let alert = UIAlertController(title: "Event couldnot be saved", message: error.localizedDescription, preferredStyle: .alert)
                let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                alert.addAction(okAction)
                
                present(alert, animated: true, completion: nil)
            }
        }
    }
    
    func share(){
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
    
}
