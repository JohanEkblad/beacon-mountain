//
//  ClientSession.swift
//  BeaconMountainIOS
//
//  Created by Jon Larsson on 2017-10-13.
//  Copyright © 2017 Jon Larsson. All rights reserved.
//

import Foundation
import os.log

class ClientSession: NSObject, StreamDelegate {
    var inputStream: InputStream
    var outputStream: OutputStream
    var buffer: String?
    
    public init(inputStream: InputStream, outputStream: OutputStream) {
        self.inputStream = inputStream
        self.outputStream = outputStream
        super.init()
        
        inputStream.delegate = self
        inputStream.schedule(in: RunLoop.current, forMode: .defaultRunLoopMode)
        inputStream.open()
        outputStream.delegate = self
        outputStream.schedule(in: RunLoop.current, forMode: .defaultRunLoopMode)
        outputStream.open()
    }
    
    func handle(message: String) {
        if (message.starts(with: "HELO")) {
            let reply = "YOLO"
            var data = reply.data(using: String.Encoding.utf8)!
            
            data.withUnsafeBytes {
                (_ bytes: UnsafePointer<UInt8>) -> ssize_t in
                let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: data.count + 1)
                buffer.assign(from: bytes, count: data.count)
                buffer[data.count] = 0
                return self.outputStream.write(buffer, maxLength: data.count + 1)
            }
        }
    }
    
    func stream(_ aStream: Stream, handle eventCode: Stream.Event) {
        if (aStream == self.inputStream && eventCode == .hasBytesAvailable) {
            let bufferSize = 1024
            let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: bufferSize + 1)
            var data = Data()
            let read = self.inputStream.read(buffer, maxLength: bufferSize)
            data.append(buffer, count: read)
            buffer[read] = 0
            let message = String(cString: buffer)
            buffer.deallocate(capacity: bufferSize)
            
            os_log("%@", message)
            handle(message: message)
        }
    }
    
    func close() {
        self.inputStream.remove(from: RunLoop.current, forMode: .defaultRunLoopMode)
        self.inputStream.close()
        self.outputStream.remove(from: RunLoop.current, forMode: .defaultRunLoopMode)
        self.inputStream.close()
    }
}
