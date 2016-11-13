import UIKit

class CalenderViewController: UIViewController {
    
    @IBOutlet var calendarViewPlaceHolder: UIView!
    
    @IBOutlet var segmentController: UISegmentedControl!

    var calendarView: CalendarView!
    @IBAction func indexChanged(_ sender: Any) {
        switch segmentController.selectedSegmentIndex {
        case 0:
            calendarViewPlaceHolder.addSubview(calendarView)
            
            break
        case 1:
            calendarView.removeFromSuperview()
            
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
        
        calendarView = CalendarView(frame: CGRect(x: 0, y: segmentController.frame.size.height*2, width: calendarViewPlaceHolder.frame.size.width, height: calendarViewPlaceHolder.frame.size.height - segmentController.frame.size.height))
        //calendarView = CalendarView(frame: calendarViewPlaceHolder.frame)
        calendarViewPlaceHolder.addSubview(calendarView)

        
        
        

    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
