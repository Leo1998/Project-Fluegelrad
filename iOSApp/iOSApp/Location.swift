import Foundation
import MapKit

class Location: NSObject, MKAnnotation, NSCoding {
    public var title: String?
    public var coordinate: CLLocationCoordinate2D
    
    init(address: String){
        coordinate = CLLocationCoordinate2D(latitude: 0, longitude: 0)
        
        super.init()
        
        title = address
        
        addressToCoord(address: address)
    }
    
    private func addressToCoord(address: String){
        let geoCoder = CLGeocoder()
        
        geoCoder.geocodeAddressString(address, completionHandler: { placemarks, error in
            
            if error == nil {
                if placemarks!.count != 0 {
                    self.coordinate = (placemarks?.first?.location?.coordinate)!
                }
            }
        })
    }
    
    required init(coder aDecoder: NSCoder) {
        title = (aDecoder.decodeObject(forKey: "title") as! String)
        
        coordinate = CLLocationCoordinate2D(latitude: 0, longitude: 0)
        
        super.init()
        
        addressToCoord(address: title!)
    }

    
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(title, forKey: "title")
    }

}
