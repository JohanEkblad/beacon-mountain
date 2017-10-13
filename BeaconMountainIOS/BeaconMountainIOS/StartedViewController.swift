//
//  StartedViewController.swift
//  BeaconMountainIOS
//
//  Created by Björn Nyberg on 2017-10-13.
//  Copyright © 2017 Jon Larsson. All rights reserved.
//

import UIKit

class StartedViewController: UIViewController {
    
    @IBOutlet weak var serverLabel: UILabel!
    var serverMode: Bool?

    override func viewDidLoad() {
        super.viewDidLoad()

        serverLabel.text = serverMode! ? "Server" : "Client"
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
