import UIKit

class CalenderViewController: UIViewController {
    
    @IBOutlet var calendarViewPlaceHolder: UIView!

    @IBOutlet var navigationBar: UINavigationBar!
    @IBOutlet var segmentController: UISegmentedControl!
    
    var calendarGridView: CalendarGridView!
    
    var calendarListView: CalendarListView!
    
    @IBAction func indexChanged(_ sender: Any) {
        switch segmentController.selectedSegmentIndex {
        case 0:
            calendarViewPlaceHolder.addSubview(calendarGridView)
          
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
        
        calendarGridView = CalendarGridView(frame: CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: calendarViewPlaceHolder.frame.size.height))
        calendarViewPlaceHolder.addSubview(calendarGridView)
        
        calendarListView = CalendarListView(frame: CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: calendarViewPlaceHolder.frame.size.height))
    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
