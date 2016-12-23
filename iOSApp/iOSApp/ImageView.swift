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
        
        imageDescription = UILabel()
        imageDescription.translatesAutoresizingMaskIntoConstraints = false
        imageDescription.lineBreakMode = .byWordWrapping
        imageDescription.numberOfLines = 0
        imageDescription.text = eventImage.imageDescription
        
        addSubview(image)
        addSubview(imageDescription)
        
        setupConstraints()
        
        layoutIfNeeded()
        height += image.frame.height
        height += imageDescription.frame.height
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupConstraints(){
        let imageX = NSLayoutConstraint(item: image, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let imageY = NSLayoutConstraint(item: image, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([imageX, imageY])
        
        let imageDescriptionX = NSLayoutConstraint(item: imageDescription, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let imageDescriptionY = NSLayoutConstraint(item: imageDescription, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: image, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([imageDescriptionX, imageDescriptionY])
    }
}
