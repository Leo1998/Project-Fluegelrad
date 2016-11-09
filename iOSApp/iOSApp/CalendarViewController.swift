import UIKit

class CalenderViewController: UIViewController {
    
    @IBOutlet var calendarViewPlaceHolder: UIView!
    
    @IBOutlet var calendarViewPH: UIToolbar!
    var calendarView: CalendarView!

    
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }



    override func viewDidLoad() {
        calendarView = CalendarView(frame: calendarViewPlaceHolder.bounds)
        calendarViewPH.addSubview(calendarView)

        
        super.viewDidLoad()
    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
