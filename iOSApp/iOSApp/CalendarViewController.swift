import UIKit

class CalendarViewController: UIViewController {
    
    private var gridController: CalendarGridViewController!
    private var listController: CalendarListViewController!

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
            
            gridController.reset()
            
            break
        case 1:
            gridContainer.alpha = 0
            listContainer.alpha = 1
            
            listController.reset()
            
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
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell")!
        
        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "CalendarListSegue" {
            listController = segue.destination as! CalendarListViewController
        }else if segue.identifier == "CalendarGridSegue" {
            gridController = segue.destination as! CalendarGridViewController
        }
    }
}
