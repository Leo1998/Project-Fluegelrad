import Foundation
import MapKit
import Contacts

@objc(Location)
class Location: NSObject, MKAnnotation, NSCoding {
	
	/**
	Address of the location
	*/
    private(set) var title: String?
	
	/**
	Coordinates of the location
	*/
    private(set) var coordinate: CLLocationCoordinate2D
    
    init(dict: NSDictionary) {
        title = (dict.object(forKey: "location.address") as! String)
        
        let longitude = Double(dict.object(forKey: "location.longitude") as! String)!
        let latitude = Double(dict.object(forKey: "location.latitude") as! String)!
        self.coordinate = CLLocationCoordinate2D(latitude: longitude, longitude: latitude)
    }
    
    required init(coder aDecoder: NSCoder) {
        title = (aDecoder.decodeObject(forKey: "address") as! String)
        
        let longitude = aDecoder.decodeDouble(forKey: "longitude")
        let latitude = aDecoder.decodeDouble(forKey: "latitude")
        self.coordinate = CLLocationCoordinate2D(latitude: longitude, longitude: latitude)
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(title, forKey: "address")
        aCoder.encode(coordinate.longitude, forKey: "longitude")
        aCoder.encode(coordinate.latitude, forKey: "latitude")
    }
	
	/**
	Retrieve a Pin of the location
	*/
    func mapItem() -> MKMapItem {
        let addressDictionary = [String(CNPostalAddressStreetKey): title]
        let placemark = MKPlacemark(coordinate: coordinate, addressDictionary: addressDictionary)

        let mapItem = MKMapItem(placemark: placemark)
        mapItem.name = title

        return mapItem
    }
}
