//
//  AppDelegate.swift
//  iOSApp
//
//  Created by Daniel on 02.11.16.
//  Copyright © 2016 Daniel. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
		
		UIApplication.shared.statusBarStyle = .lightContent
		
		let statusBar: UIView = UIApplication.shared.value(forKey: "statusBar") as! UIView
		if statusBar.responds(to: #selector(setter: UIView.backgroundColor)) {
			statusBar.backgroundColor = UIColor.secondary()
		}
		
        return true
    }
	
	func applicationDidBecomeActive(_ application: UIApplication) {
		application.registerUserNotificationSettings(UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil))

	}

}

