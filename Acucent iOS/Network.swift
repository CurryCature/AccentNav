//
//  Network.swift
//  Acucent iOS
//
//  Created by Hosna Molavi on 2024-05-07.
//
import Foundation
import Socket
import Dispatch


func createHelper(family: Socket.ProtocolFamily = .inet) throws -> Socket {
    
    let socket = try Socket.create(family: family)
    
    return socket
}

func createUDPHelper(family: Socket.ProtocolFamily = .inet) throws -> Socket {
    
    let socket = try Socket.create(family: family, type: .datagram, proto: .udp)
    
    return socket
}

func readAndPrint(socket: Socket, data: inout Data) throws -> String? {
        
        data.count = 0
        let    bytesRead = try socket.read(into: &data)
        if bytesRead > 0 {
            
            print("Read \(bytesRead) from socket...")
            
            guard let response = NSString(data: data as Data, encoding: String.Encoding.utf8.rawValue) else {
                
                print("Error accessing received data...")
                return nil
            }
            
            print("Response:\n\(response)")
            return String(describing: response)
        }

        return nil
    }

func TCPClient() {

        let hostname = "vm.cloud.cbh.kth.se"
        let port: Int32 = 20013

        var data = Data()

        do {

            // Create the signature...
            let signature = try Socket.Signature(protocolFamily: .inet, socketType: .stream, proto: .tcp, hostname: hostname, port: port)!

            // Create the socket...
            let socket = try createHelper()

            // Defer cleanup...
            defer {
                // Close the socket...
                socket.close()
            
            }

            // Connect to the server helper...
            try socket.connect(using: signature)
            if !socket.isConnected {

                fatalError("Failed to connect to the server...")
            }

            print("\nConnected to host: \(hostname):\(port)")
            print("\tSocket signature: \(socket.signature!.description)\n")

            let (isReadable, isWritable) = try socket.isReadableOrWritable(waitForever: false, timeout: 5)

            print("Socket is readable: \(isReadable)")
            print("Socket is writable: \(isWritable)")

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


            let response2 = try readAndPrint(socket: socket, data: &data)


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
                return
            }

            print("testTruncateTCP Error reported: \(socketError.description)")
        }
        
}



