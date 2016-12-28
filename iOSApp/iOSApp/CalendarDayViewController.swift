import UIKit
import MapKit

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
            let imageTemp = ImageView(eventImage: item)
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
        content.addSubview(mapView)
        mapView.addConstraintsXY(xView: content, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: prizeLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        mapView.addConstraintsXY(xView: nil, xSelfAttribute: .width, xViewAttribute: .notAnAttribute, xMultiplier: 1, xConstant: view.frame.width, yView: nil, ySelfAttribute: .height, yViewAttribute: .notAnAttribute, yMultiplier: 1, yConstant: view.frame.width/2)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(2), execute: {
            let regionRadius: CLLocationDistance = 0.01
            let region = MKCoordinateRegion(center: self.event.location.coordinate, span: MKCoordinateSpan(latitudeDelta: regionRadius * 2, longitudeDelta: regionRadius * 2))
            self.mapView.setRegion(region, animated: true)
            
        })
        
        view.layoutIfNeeded()
        
        var totalHeight: CGFloat = descriptionLabel.frame.height
        totalHeight += imageView.frame.height
        totalHeight += prizeLabel.frame.height
        totalHeight += mapView.frame.height
        
        scrollView.contentSize = CGSize(width: view.frame.width, height: totalHeight)
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
