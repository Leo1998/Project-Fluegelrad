import UIKit

class LicensesViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

	/**
	list with all the licenses
	*/
	private var licensesTable: UITableView!
	
	/**
	array with all the licenses
	*/
	private var licenses = [Licenses]()
	
	/**
	dictionary with all the gesturerecognizers to licenses
	*/
	private var gestures = [UITapGestureRecognizer: Licenses]()

    override func viewDidLoad() {
        super.viewDidLoad()
		
		licenses.append(Licenses(name: "Material Design Icons", url: "http://github.com/google/material-design-icons/", copyright: "Copyright (C) 2005 Android Open Source Project", license: .apache20))
		licenses.append(Licenses(name: "OpenStreetMapData", url: "www.openstreetmap.org/copyright", copyright: "(C) OpenStreetMap-Mitwirkende", license: .apache20))
		
		licensesTable = UITableView()
		licensesTable.register(LicensesViewCell.self, forCellReuseIdentifier: "cell")
		licensesTable.estimatedRowHeight = UIScreen.main.bounds.height / 10 * 2
		licensesTable.separatorColor = UIColor.primary()
		view.addSubview(licensesTable)
		licensesTable.translatesAutoresizingMaskIntoConstraints = false
		licensesTable.addConstraintsXY(xView: view, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: topLayoutGuide, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		licensesTable.addConstraintsXY(xView: view, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: view, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		licensesTable.delegate = self
		licensesTable.dataSource = self
		licensesTable.backgroundColor = UIColor.clear

		licensesTable.separatorColor = UIColor.clear
		licensesTable.tableFooterView = UIView()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

	func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		return licenses.count
	}
	
	func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		let cell:LicensesViewCell = licensesTable.dequeueReusableCell(withIdentifier: "cell")! as! LicensesViewCell
		let licence = (licenses[indexPath.row] )

		cell.nameLabel.text = licence.name
		cell.urlLabel.text = licence.url
		cell.urlLabel.isUserInteractionEnabled = true
		let gestureTemp = UITapGestureRecognizer(target: self, action: #selector(LicensesViewController.webTap))
		gestures[gestureTemp] = licence
		cell.urlLabel.addGestureRecognizer(gestureTemp)
		cell.copyright.text = licence.copyright
		cell.license.text = licence.license
		
		cell.licenseView.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .leading, xViewAttribute: .leading, xMultiplier: 1, xConstant: 0, yView: cell.urlLabel, ySelfAttribute: .top, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		cell.licenseView.addConstraintsXY(xView: cell.contentView, xSelfAttribute: .trailing, xViewAttribute: .trailing, xMultiplier: 1, xConstant: 0, yView: cell.contentView, ySelfAttribute: .bottom, yViewAttribute: .bottom, yMultiplier: 1, yConstant: 0)
		
		return cell
	}
	
	/**
	called when the website is tapped shows the website
	*/
	func webTap(sender: AnyObject){
		
		let url = URL(string: (gestures[sender as! UITapGestureRecognizer]?.url!)!)!
		
		if UIApplication.shared.canOpenURL(url){
			if #available(iOS 10, *){
				UIApplication.shared.open(url, options: [:], completionHandler: nil)
			}else{
				UIApplication.shared.openURL(url)
			}
		}
	}
	
	func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
		return UITableViewAutomaticDimension
	}

}
