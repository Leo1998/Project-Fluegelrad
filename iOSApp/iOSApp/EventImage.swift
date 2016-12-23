import UIKit

class EventImage: NSObject, NSCoding{
    private var imagePath: String!
    private(set) var imageDescription: String!
    
    private(set) var scaled = false
    private var imageSave: UIImage?
    public var image: UIImage? {
        get {
            if self.imageSave == nil {
                
                let url = URL(string: DatabaseManager.url + self.imagePath)!
                let data = try? Data(contentsOf: url)
                imageSave = UIImage(data: data!)
            }
            
            return imageSave
        }
        
        set(image) {
            if !scaled {
                imageSave = image
                scaled = true
            }
        }
    }
    
    init(dict: NSDictionary) {
        imagePath = (dict.object(forKey: "path") as! String)
        imageDescription = (dict.object(forKey: "description") as! String)
    }

    
    required init(coder aDecoder: NSCoder) {
        imagePath = (aDecoder.decodeObject(forKey: "path") as! String)
        imageDescription = (aDecoder.decodeObject(forKey: "description") as! String)
    }
    
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(imagePath, forKey: "path")
        aCoder.encode(imageDescription, forKey: "description")
    }
}
