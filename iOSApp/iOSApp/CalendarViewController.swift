import UIKit

class CalenderViewController: UIViewController, UICollectionViewDelegate, UITableViewDelegate {

    @IBOutlet var navigationBar: UINavigationBar!
    @IBOutlet var segmentController: UISegmentedControl!

    
    private var calendarGridView: CalendarGridView!
    private var calendarListView: CalendarListView!
    
    private var dayEvent: Event?
    
    @IBOutlet var item: UINavigationItem!

    @IBAction func indexChanged(_ sender: Any) {
        switch segmentController.selectedSegmentIndex {
        case 0:
            view.addSubview(calendarGridView)
            calendarGridView.dayGrid.delegate = self
          
            calendarListView.removeFromSuperview()
            break
        case 1:
            view.addSubview(calendarListView)
            calendarListView.translatesAutoresizingMaskIntoConstraints = false

            
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
        
        calendarGridView = CalendarGridView(frame: view.frame)
        view.addSubview(calendarGridView)
        calendarGridView.dayGrid.delegate = self
        
        calendarListView = CalendarListView(frame: view.frame)
        calendarListView.eventTable.delegate = self
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if calendarGridView.shownEvents[indexPath.item] != nil {
            performSegue(withIdentifier: "CalendarDayViewController", sender: self)
            dayEvent = calendarGridView.shownEvents[indexPath.item]
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        performSegue(withIdentifier: "CalendarDayViewController", sender: self)
        dayEvent = calendarListView.shownEvents[indexPath.item]
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "CalendarDayViewController" {
            let vc = segue.destination as! CalendarDayViewController
            vc.event = dayEvent
            dayEvent = nil
        }
    }
}
