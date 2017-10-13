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
    var data: Data
    
    public init(inputStream: InputStream, outputStream: OutputStream) {
        self.inputStream = inputStream
        self.outputStream = outputStream
        self.data = Data()
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
            let data = reply.data(using: String.Encoding.utf8)!
            
            data.withUnsafeBytes {
                (_ bytes: UnsafePointer<UInt8>) -> ssize_t in
                let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: data.count + 1)
                buffer.assign(from: bytes, count: data.count)
                buffer[data.count] = 0
                return self.outputStream.write(buffer, maxLength: data.count + 1)
            }
        }
    }
    
    func handleBufferUpdate() {
        var eofStringIndex = self.data.index(of: 0)
        while(eofStringIndex != nil) {
            let subdata = self.data.subdata(in: 0..<eofStringIndex!)
            handle(message: String(data: subdata, encoding: String.Encoding.utf8)!)
            self.data = self.data.advanced(by: eofStringIndex! + 1)
            eofStringIndex = self.data.index(of: 0)
        }
    }
    
    func stream(_ aStream: Stream, handle eventCode: Stream.Event) {
        if (aStream == self.inputStream && eventCode == .hasBytesAvailable) {
            let bufferSize = 1024
            let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: bufferSize + 1)
            while (self.inputStream.hasBytesAvailable) {
                let count = self.inputStream.read(buffer, maxLength: bufferSize)
                for index in 0..<count {
                    os_log("%d %d", index, buffer[index])
                }
                self.data.append(buffer, count: count)
            }
            buffer.deallocate(capacity: bufferSize)
            handleBufferUpdate()
        }
    }
    
    func close() {
        self.inputStream.remove(from: RunLoop.current, forMode: .defaultRunLoopMode)
        self.inputStream.close()
        self.outputStream.remove(from: RunLoop.current, forMode: .defaultRunLoopMode)
        self.inputStream.close()
    }
}
