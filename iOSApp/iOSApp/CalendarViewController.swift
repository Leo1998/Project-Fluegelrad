import UIKit

class CalenderViewController: UIViewController {
    
    @IBOutlet var calendarViewPlaceHolder: UIView!
    
    var calendarView: CalendarView!

    
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }



    override func viewDidLoad() {
        calendarView = CalendarView(frame: calendarViewPlaceHolder.bounds)
        calendarViewPlaceHolder.addSubview(calendarView)

        
        super.viewDidLoad()
    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
