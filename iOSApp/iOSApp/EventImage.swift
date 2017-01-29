import UIKit

@objc(EventImage)
class EventImage: NSObject, NSCoding{
	
	/**
	image path from where to download the image from
	*/
    private var imagePath: String!
	
	/**
	description to the image
	*/
    private(set) var imageDescription: String!
	
	/**
	Check if the image is already scaled
	You should only scale the image once
	*/
    private(set) var scaled = false
	
	/**
	The image itself
	*/
    private var imageSave: UIImage?
	/**
	The reference to the image so it can download itself
	can only be set once for scaling
	*/
    public var image: UIImage? {
        get {
            if self.imageSave == nil {
                
				imageSave = Storage.getImage(path: imagePath)
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
