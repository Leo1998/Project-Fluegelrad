import UIKit

class CalendarDayView: UIView {
    var categoryLabel: UILabel = UILabel()
    var locationLabel: UILabel = UILabel()
    var dateLabel: UILabel = UILabel()
    var hostLabel: UILabel = UILabel()
    var descriptionLabel: UILabel = UILabel()
    var prizeLabel: UILabel = UILabel()
    
    var event:Event!

    public init(frame: CGRect, event: Event) {
        super.init(frame: frame)
        
        self.event = event
        
        addSubview(categoryLabel)
        addSubview(locationLabel)
        addSubview(dateLabel)
        addSubview(hostLabel)
        addSubview(descriptionLabel)
        addSubview(prizeLabel)
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
