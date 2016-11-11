import UIKit

class CalenderViewController: UIViewController {
    
    @IBOutlet var calendarViewPlaceHolder: UIView!
    
    @IBOutlet var calendarViewPH: UIToolbar!
    var calendarView: CalendarView!

    
    required init?(coder aDecoder: NSCoder){
        super.init(coder: aDecoder);
    }



    override func viewDidLoad() {
        super.viewDidLoad()
        
        calendarView = CalendarView(frame: calendarViewPlaceHolder.bounds)
        calendarViewPH.addSubview(calendarView)

        
        
        

    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
