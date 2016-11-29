import UIKit

class CalendarListCell: UITableViewCell {
    
    private(set) var categoryLabel: UILabel!
    private(set) var locationLabel: UILabel!
    private(set) var dateLabel: UILabel!
    private(set) var hostLabel: UILabel!
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupViews()
        setupConstraints()
    }
    
    private func setupViews(){
        categoryLabel = UILabel()
        categoryLabel.translatesAutoresizingMaskIntoConstraints = false
        
        locationLabel = UILabel()
        locationLabel.translatesAutoresizingMaskIntoConstraints = false
        
        dateLabel = UILabel()
        dateLabel.translatesAutoresizingMaskIntoConstraints = false
        
        hostLabel = UILabel()
        hostLabel.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(categoryLabel)
        addSubview(locationLabel)
        addSubview(dateLabel)
        addSubview(hostLabel)
    }
    
    private func setupConstraints(){
        let categoryLabelX = NSLayoutConstraint(item: categoryLabel, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let categoryLabelY = NSLayoutConstraint(item: categoryLabel, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([categoryLabelX, categoryLabelY])
        
        let locationLabelLabelX = NSLayoutConstraint(item: locationLabel, attribute: NSLayoutAttribute.leading, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.leading, multiplier: 1, constant: 0)
        let locationLabelLabelY = NSLayoutConstraint(item: locationLabel, attribute: NSLayoutAttribute.bottom, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([locationLabelLabelX, locationLabelLabelY])
        
        let dateLabelLabelX = NSLayoutConstraint(item: dateLabel, attribute: NSLayoutAttribute.trailing, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.trailing, multiplier: 1, constant: 0)
        let dateLabelLabelY = NSLayoutConstraint(item: dateLabel, attribute: NSLayoutAttribute.top, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.top, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([dateLabelLabelX, dateLabelLabelY])
        
        let hostLabelX = NSLayoutConstraint(item: hostLabel, attribute: NSLayoutAttribute.trailing, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.trailing, multiplier: 1, constant: 0)
        let hostLabelY = NSLayoutConstraint(item: hostLabel, attribute: NSLayoutAttribute.bottom, relatedBy: NSLayoutRelation.equal, toItem: self, attribute: NSLayoutAttribute.bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([hostLabelX, hostLabelY])
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()

    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

}
