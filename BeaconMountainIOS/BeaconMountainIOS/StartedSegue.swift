//
//  StartedSegue.swift
//  BeaconMountainIOS
//
//  Created by Björn Nyberg on 2017-10-13.
//  Copyright © 2017 Jon Larsson. All rights reserved.
//

import UIKit

class StartedSegue: UIStoryboardSegue {
    
    override func perform() {
        let source = self.source as! ViewController
        let destination = self.destination as! StartedViewController
        destination.serverMode = source.modeSwitch.isOn
        
        let window = UIApplication.shared.keyWindow
        window?.insertSubview(destination.view, aboveSubview: source.view)
    }
}
