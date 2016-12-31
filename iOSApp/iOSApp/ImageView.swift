import UIKit

class ImageView: UIView {
    private var image: UIImageView!
    private var imageDescription: UILabel!
    
    private(set) var height: CGFloat = 0
    
    init(eventImage: EventImage) {
        super.init(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
        
        if !eventImage.scaled {
            let imageTemp = eventImage.image
            
            let size = CGSize(width: UIScreen.main.bounds.width, height: (imageTemp?.size.height)! / ((imageTemp?.size.width)! / UIScreen.main.bounds.width))
            
            UIGraphicsBeginImageContext(size)
            imageTemp?.draw(in: CGRect(origin: .zero, size: size))
            
            eventImage.image = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
        }
        image = UIImageView(image: eventImage.image)
        image.translatesAutoresizingMaskIntoConstraints = false
        addSubview(image)
        image.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)

        imageDescription = UILabel()
        imageDescription.translatesAutoresizingMaskIntoConstraints = false
        imageDescription.lineBreakMode = .byWordWrapping
        imageDescription.numberOfLines = 0
        imageDescription.text = eventImage.imageDescription
        addSubview(imageDescription)
        imageDescription.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: image, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        
        
        layoutIfNeeded()
        height += image.frame.height
        height += imageDescription.frame.height
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
