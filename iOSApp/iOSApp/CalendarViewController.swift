import UIKit

class CalendarViewController: UIViewController {

    @IBOutlet var navigationBar: UINavigationBar!
    @IBOutlet var segmentController: UISegmentedControl!
    
    @IBOutlet var listContainer: UIView!
    @IBOutlet var gridContainer: UIView!

    @IBOutlet var item: UINavigationItem!

    @IBAction func indexChanged(_ sender: Any) {
        switch segmentController.selectedSegmentIndex {
        case 0:
            gridContainer.alpha = 1
            listContainer.alpha = 0
            
            break
        case 1:
            gridContainer.alpha = 0
            listContainer.alpha = 1
            
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
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
