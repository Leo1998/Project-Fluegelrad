import UIKit

class CalendarDayView: UIView {
    private var categoryLabel: UILabel!
    private var locationLabel: UILabel!
    private var dateLabel: UILabel!
    private var hostLabel: UILabel!
    private var descriptionLabel: UILabel!
    private var prizeLabel: UILabel!
    
    private var event:Event!

    public init(frame: CGRect, event: Event) {
        super.init(frame: frame)
        
        self.event = event
        
        categoryLabel = UILabel()
        categoryLabel.text = event.category
        categoryLabel.translatesAutoresizingMaskIntoConstraints = false
        
        locationLabel = UILabel()
        locationLabel.text = event.location
        locationLabel.translatesAutoresizingMaskIntoConstraints = false
        
        dateLabel = UILabel()
        dateLabel.text = "Hello"
        dateLabel.translatesAutoresizingMaskIntoConstraints = false
        
        hostLabel = UILabel()
        hostLabel.text = event.host
        hostLabel.translatesAutoresizingMaskIntoConstraints = false
        
        descriptionLabel = UILabel()
        descriptionLabel.text = event.descriptionEvent
        descriptionLabel.translatesAutoresizingMaskIntoConstraints = false
        
        prizeLabel = UILabel()
        prizeLabel.text = String(event.price)
        prizeLabel.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(categoryLabel)
        addSubview(locationLabel)
        addSubview(dateLabel)
        addSubview(hostLabel)
        addSubview(descriptionLabel)
        addSubview(prizeLabel)
        
        
        let categoryLabelX = NSLayoutConstraint(item: categoryLabel, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let categoryLabelY = NSLayoutConstraint(item: categoryLabel, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([categoryLabelX, categoryLabelY])
        
        let locationLabelX = NSLayoutConstraint(item: locationLabel, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let locationLabelY = NSLayoutConstraint(item: locationLabel, attribute: NSLayoutAttribute.bottom, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([locationLabelX, locationLabelY])
        
        let dateLabelX = NSLayoutConstraint(item: dateLabel, attribute: NSLayoutAttribute.trailing, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.trailing, multiplier: 1, constant: 0)
        let dateLabelY = NSLayoutConstraint(item: dateLabel, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([dateLabelX, dateLabelY])
        
        let hostLabelX = NSLayoutConstraint(item: hostLabel, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let hostLabelY = NSLayoutConstraint(item: hostLabel, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: categoryLabel, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([hostLabelX, hostLabelY])
        
        let descriptionLabelX = NSLayoutConstraint(item: descriptionLabel, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let descriptionLabelY = NSLayoutConstraint(item: descriptionLabel, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: hostLabel, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([descriptionLabelX, descriptionLabelY])
        
        let prizeLabelX = NSLayoutConstraint(item: prizeLabel, attribute: NSLayoutAttribute.trailing, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.trailing, multiplier: 1, constant: 0)
        let prizeLabelY = NSLayoutConstraint(item: prizeLabel, attribute: NSLayoutAttribute.bottom, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([prizeLabelX, prizeLabelY])
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
