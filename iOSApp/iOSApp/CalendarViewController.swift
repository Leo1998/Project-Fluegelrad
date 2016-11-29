import UIKit

class CalenderViewController: UIViewController, UICollectionViewDelegate {
    
    @IBOutlet var calendarViewPlaceHolder: UIView!

    @IBOutlet var navigationBar: UINavigationBar!
    @IBOutlet var segmentController: UISegmentedControl!
    
    var calendarGridView: CalendarGridView!
    
    var calendarListView: CalendarListView!
    
    private var frame: CGRect!
    
    @IBAction func indexChanged(_ sender: Any) {
        switch segmentController.selectedSegmentIndex {
        case 0:
            calendarViewPlaceHolder.addSubview(calendarGridView)
            calendarGridView.dayGrid.delegate = self
          
            calendarListView.removeFromSuperview()
            break
        case 1:
            calendarViewPlaceHolder.addSubview(calendarListView)
            
            calendarGridView.removeFromSuperview()
            break
        default:
            break
        }
    }

    
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }



    override func viewDidLoad() {
        super.viewDidLoad()
        
        frame = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: calendarViewPlaceHolder.frame.size.height)
        
        calendarGridView = CalendarGridView(frame: frame)
        calendarViewPlaceHolder.addSubview(calendarGridView)
        calendarGridView.dayGrid.delegate = self

        
        calendarListView = CalendarListView(frame: frame)
        
    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if calendarGridView.shownEvents[indexPath.item] != nil {
            let dayView = CalendarDayView(frame: self.frame, event: calendarGridView.shownEvents[indexPath.item]!)
            
            calendarViewPlaceHolder.addSubview(dayView)
            
            self.addBackbutton(title: "Back")

            calendarGridView.removeFromSuperview()
            
        }
    }
    
    func backButtonAction() {
        self.dismiss(animated: true, completion: nil)
    }
 
    func addBackbutton(title: String) {
        navigationItem.backBarButtonItem = UIBarButtonItem(title: title, style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.backButtonAction))

    }

}
