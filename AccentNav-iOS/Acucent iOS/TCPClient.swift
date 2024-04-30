//
//  TCPClient.swift
//  Acucent iOS
//
//  Created by Hosna Molavi on 2024-04-30.
//

import Foundation
import SocketIO


class TCPClient{
    
    func connectToServer(host: String, port: UInt32, dataToSend: Data) -> Data? {
        
        var inputStream: InputStream?
        var outputStream: OutputStream?
        
        //Create input and output stream for commmunication with server.
        Stream.getStreamsToHost(withName: host, port: Int(port), inputStream: &inputStream, outputStream: &outputStream)
                
        guard let input = inputStream, let output = outputStream else {
            print("Failed to create streams")
            return nil
        }
        
        //Opening the streams allows data to be sent and received through them.
        input.open()
        output.open()
        
        // Write the data to the output stream
        let bytesWritten = output.write((dataToSend as NSData).bytes.bindMemory(to: UInt8.self, capacity: dataToSend.count), maxLength: dataToSend.count)
        
        if bytesWritten == -1 {
            print("Failed to write data to server")
            return nil
        }
                
        // Assuming the server sends a byte array of fixed size.
        let bufferSize = 1024
        //This buffer store the bytes read from the stream.
        var buffer = [UInt8](repeating: 0, count: bufferSize)
        //This a data of object Data.
        var data = Data()
        //It reads the bytes from the input stream to the buffer.
        let bytesRead = input.read(&buffer, maxLength: bufferSize)
        if bytesRead > 0 {
            data.append(buffer, count: bytesRead)
            return data
        } else {
            print("Failed to read data from server")
            return nil
        }
        
    }
    
    func askServer(){
        
        let tcpclient = TCPClient()
        if let data = tcpclient.connectToServer(host: "vm.cloud.cbh.kth.se", port: 20013){
            print("Recieved data and connected")
        }
            else{
                print("Error")
            }
    }
    
    
}

