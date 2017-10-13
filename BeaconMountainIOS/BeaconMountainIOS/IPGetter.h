//
//  IPGetter.h
//  BeaconMountainIOS
//
//  Created by Jon Larsson on 2017-10-13.
//  Copyright © 2017 Jon Larsson. All rights reserved.
//

#import <Foundation/Foundation.h>
@interface IPGetter : NSObject

+ (NSString *)getIPAddress:(BOOL)preferIPv4;
+ (NSDictionary *)getIPAddresses;

- (void) run;

@end

