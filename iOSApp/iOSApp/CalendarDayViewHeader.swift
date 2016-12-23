import UIKit

class CalendarDayViewHeader: UIView {
    private var nameLabel: UILabel!
    private var dateStartLabel: UILabel!
    private var dateEndLabel: UILabel!
    private var hostLabel: UILabel!
    
    private(set) var height: CGFloat = 0

    init(event: Event) {
        super.init(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
        
        nameLabel = UILabel()
        nameLabel.text = event.name
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE dd.MM.YYYY HH:mm"
        
        dateStartLabel = UILabel()
        dateStartLabel.text = dateFormatter.string(from: event.dateStart)
        dateStartLabel.translatesAutoresizingMaskIntoConstraints = false
        
        dateEndLabel = UILabel()
        dateEndLabel.text = dateFormatter.string(from: event.dateEnd)
        dateEndLabel.translatesAutoresizingMaskIntoConstraints = false
        
        hostLabel = UILabel()
        hostLabel.text = String(event.hostId)
        hostLabel.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(nameLabel)
        addSubview(dateStartLabel)
        addSubview(dateEndLabel)
        addSubview(hostLabel)
        
        nameLabel.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        dateStartLabel.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: self, ySelfAttribute: .top, yViewAttribute: .top, yMultiplier: 1, yConstant: 0)
        dateEndLabel.addConstraintsXY(xView: self, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: dateStartLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        hostLabel.addConstraintsXY(xView: self, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: nameLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
        
        layoutIfNeeded()
        height += nameLabel.frame.height
        height += hostLabel.frame.height
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
