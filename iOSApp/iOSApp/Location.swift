import Foundation
import MapKit
import Contacts

class Location: NSObject, MKAnnotation, NSCoding {
    private(set) var title: String?
    private(set) var coordinate: CLLocationCoordinate2D
    private(set) var id: Int
    
    init(dict: NSDictionary) {
        id = Int(dict.object(forKey: "locationId") as! String)!
        title = (dict.object(forKey: "address") as! String)
        
        let longitude = Double(dict.object(forKey: "longitude") as! String)!
        let latitude = Double(dict.object(forKey: "latitude") as! String)!
        self.coordinate = CLLocationCoordinate2D(latitude: longitude, longitude: latitude)
    }
    
    required init(coder aDecoder: NSCoder) {
        id = aDecoder.decodeInteger(forKey: "locationId")
        title = (aDecoder.decodeObject(forKey: "address") as! String)
        
        let longitude = aDecoder.decodeDouble(forKey: "longitude")
        let latitude = aDecoder.decodeDouble(forKey: "latitude")
        self.coordinate = CLLocationCoordinate2D(latitude: longitude, longitude: latitude)
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(id, forKey: "locationId")
        aCoder.encode(title, forKey: "address")
        aCoder.encode(coordinate.longitude, forKey: "longitude")
        aCoder.encode(coordinate.latitude, forKey: "latitude")
    }
    
    func mapItem() -> MKMapItem {
        let addressDictionary = [String(CNPostalAddressStreetKey): title]
        let placemark = MKPlacemark(coordinate: coordinate, addressDictionary: addressDictionary)

        let mapItem = MKMapItem(placemark: placemark)
        mapItem.name = title

        return mapItem
    }
}
