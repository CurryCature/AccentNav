//
//  Network.swift
//  Acucent iOS
//
//  Created by Hosna Molavi on 2024-05-07.
//
import Foundation
import Socket
import Dispatch

//var responseData: [UInt8]
//var BUFFFERSIZE = 1024


func askServer(hostname: String, port: Int, inputBytes: [UInt8]) -> [UInt8]?{
    
    let port = 20013
    let hostname = "vm.cloud.cbh.kth.se"
    
    do {
        
        let mySocket = try Socket.create()
        mySocket.readBufferSize = 1024
        
        mySocket.connect(to: hostname, port: port)
    }
    
    
}

/*func testTruncateTCP() {

        let hostname = "127.0.0.1"
        let port: Int32 = 1337

        var data = Data()

        do {

            // Launch the server helper...
            //launchServerHelper()

            // Need to wait for the server to come up...
            /*#if os(Linux)
                _ = Glibc.sleep(2)
            #else
                _ = Darwin.sleep(2)
            #endif*/

            // Create the signature...
            let signature = try Socket.Signature(protocolFamily: .inet, socketType: .stream, proto: .tcp, hostname: hostname, port: port)!

            // Create the socket...
            let socket = try createHelper()

            // Defer cleanup...
            defer {
                // Close the socket...
                socket.close()
                XCTAssertFalse(socket.isActive)
            }

            // Connect to the server helper...
            try socket.connect(using: signature)
            if !socket.isConnected {

                fatalError("Failed to connect to the server...")
            }

            print("\nConnected to host: \(hostname):\(port)")
            print("\tSocket signature: \(socket.signature!.description)\n")

            _ = try readAndPrint(socket: socket, data: &data)

            let hello = "Hello from client..."
            try socket.write(from: hello)

            print("Wrote '\(hello)' to socket...")

            let buf = UnsafeMutablePointer<CChar>.allocate(capacity: 19)
            #if swift(>=4.1)
                buf.initialize(repeating: 0, count: 19)
            #else
                buf.initialize(to: 0, count: 19)
            #endif

            defer {
                #if swift(>=4.1)
                    buf.deinitialize(count: 19)
                    buf.deallocate()
                #else
                    buf.deinitialize()
                    buf.deallocate(capacity: 19)
                #endif
            }

            // Save room for a null character...
            _ = try socket.read(into: buf, bufSize: 18, truncate: true)
            let response = String(cString: buf)

            XCTAssertEqual(response, "Server response: \n")

            let response2 = try readAndPrint(socket: socket, data: &data)

            XCTAssertEqual(response2, "\(hello)\n")

            try socket.write(from: "QUIT")

            print("Sent quit to server...")

            // Need to wait for the server to go down before continuing...
            #if os(Linux)
                _ = Glibc.sleep(1)
            #else
                _ = Darwin.sleep(1)
            #endif

        } catch let error {

            // See if it's a socket error or something else...
            guard let socketError = error as? Socket.Error else {

                print("Unexpected error...")
                XCTFail()
                return
            }

            print("testTruncateTCP Error reported: \(socketError.description)")
            XCTFail()
        }
        
    }*/

