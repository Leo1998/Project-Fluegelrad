import UIKit
import MapKit

class CalendarMapViewController: UIViewController {

    @IBOutlet var mapView: MKMapView!
    
    public var location: Location!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        mapView.addAnnotation(location)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
