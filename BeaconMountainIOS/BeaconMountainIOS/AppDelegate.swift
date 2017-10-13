//
//  AppDelegate.swift
//  BeaconMountainIOS
//
//  Created by Jon Larsson on 2017-10-13.
//  Copyright © 2017 Jon Larsson. All rights reserved.
//

import UIKit
import CoreLocation
import os.log

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, NetServiceDelegate, StreamDelegate, CLLocationManagerDelegate {

    var window: UIWindow?
    var server: NetService?
    var clientSession: ClientSession?
    var locationManager:CLLocationManager!


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        self.server = NetService.init(domain: "", type: "_tcp", name: "BeaconMontain", port: 4711)
        self.server!.delegate = self
        self.server!.publish(options: .listenForConnections)
        os_log("%@", IPGetter.getIPAddress(true))
        return true
    }
    
    func netService(_: NetService, didAcceptConnectionWith inputstream: InputStream, outputStream: OutputStream) {
        if (self.clientSession == nil) {
            self.clientSession = ClientSession(inputStream: inputstream, outputStream: outputStream)
        } else {
            inputstream.close()
            outputStream.close()
        }
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
        os_log("%@", IPGetter.getIPAddress(true))
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        self.server?.stop()
        self.clientSession?.close()
        self.clientSession = nil
        self.locationManager.stopUpdatingLocation()
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
        self.server!.publish(options: .listenForConnections)
        os_log("%@", IPGetter.getIPAddress(true))
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        determineMyCurrentLocation()
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

    func determineMyCurrentLocation() {
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
        
        if CLLocationManager.locationServicesEnabled() {
            locationManager.startUpdatingLocation()
            //locationManager.startUpdatingHeading()
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation:CLLocation = locations[0] as CLLocation
        
        // Call stopUpdatingLocation() to stop listening for location updates,
        // other wise this function will be called every time when user location changes.
        
        // manager.stopUpdatingLocation()
        
        print("user latitude = \(userLocation.coordinate.latitude)")
        print("user longitude = \(userLocation.coordinate.longitude)")
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Error \(error)")
    }

}

