import UIKit

class ContainerViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        performSegue(withIdentifier: "CalendarGridViewController", sender: nil)


    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func setGridView(){
        performSegue(withIdentifier: "CalendarGridViewController", sender: nil)
    }
}
