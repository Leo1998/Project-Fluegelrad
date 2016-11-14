import UIKit

class CalenderViewController: UIViewController {
    
    @IBOutlet var calendarViewPlaceHolder: UIView!
    
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
        
        calendarGridView = CalendarGridView(frame: CGRect(x: 0, y: segmentController.frame.size.height*2, width: calendarViewPlaceHolder.frame.size.width, height: calendarViewPlaceHolder.frame.size.height - segmentController.frame.size.height))
        calendarViewPlaceHolder.addSubview(calendarGridView)

        
        calendarListView = CalendarListView(frame: CGRect(x: 0, y: segmentController.frame.size.height*2, width: calendarViewPlaceHolder.frame.size.width, height: calendarViewPlaceHolder.frame.size.height - segmentController.frame.size.height))
        

    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
