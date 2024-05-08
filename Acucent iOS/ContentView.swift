//
//  ContentView.swift
//  Acucent iOS
//
//  Created by Turja Das on 2024-04-24.
//

import SwiftUI
import AVFoundation

struct ContentView: View {
    @State private var isStopwatchRunning = false
    @State private var elapsedTime: TimeInterval = 0
    @State private var timer: Timer?
    @State var progressValue: Float = 0.0

    var body: some View {
        
        ZStack {
            Color("bgColour").ignoresSafeArea()
            VStack() {
                VStack {
                    Image("logo")
                        .resizable()
                        .cornerRadius(15)
                        .frame(maxWidth:150 , maxHeight:80)
                        .aspectRatio(contentMode: .fit)
                        .padding()
                    
                    Text("Hold the microphone to record").font(.title3).fontWeight(.bold).multilineTextAlignment(.center).italic()
                        .padding(.vertical, 3)
                    
                    Text("For a more accurate result, try this text!").font(.subheadline).fontWeight(.light).multilineTextAlignment(.center)
                        .padding(.vertical, 3)
                    
                    Text("Minimum Recording Duration: 5 seconds").font(.body).fontWeight(.light).multilineTextAlignment(.center)
                        .padding()
                }
                Spacer()
                
                VStack {
                    
                    Text("\(formattedTime(elapsedTime))")
                                    .font(.title)
                                    .padding()
                    
                    ZStack {
                        
                        ProgressBar(elapsedTime: $elapsedTime)
                                                    .frame(width: 200, height: 200.0)
                                                    .padding(20.0).onAppear() {
                                                        self.elapsedTime = 0.01
                                                    }
                        
                        Button(action: {
                            if self.isStopwatchRunning {
                                                self.stopStopwatch()
                                            
                                            } else {
                                                self.startStopwatch()
                                            }
                                            self.isStopwatchRunning.toggle()
                        }) {
                                                Image(systemName: isStopwatchRunning ? "mic.fill" : "mic")
                                                    .resizable()
                                                    .foregroundColor(Color(hue: 0.776, saturation: 0.906, brightness: 0.746, opacity: 0.337))
                                                    .cornerRadius(15)
                                                    .frame(maxWidth:105 , maxHeight:145)
                                                    .aspectRatio(contentMode: .fit)
                                                    .padding(.all)
                        }
                    }
                    
                    Button(action: {
                                        self.resetStopwatch()
                                    }) {
                                        Text("Reset")
                                            .foregroundColor(.purple)
                                    }
                                                                          
                }
                
                Spacer()
                                                          
            }
                                                      
        }
                                                  
                                                      
    }
    
    
    func requestRecordPermission() {
        AVAudioSession.sharedInstance().requestRecordPermission { granted in
            if granted {
                // Permission granted
            } else {
                // Handle permission denied
            }
        }
    }
    
    func setupAudioSession() throws {
        let audioSession = AVAudioSession.sharedInstance()
        try audioSession.setCategory(.playAndRecord, mode: .default)
        try audioSession.setActive(true)
    }
    
    func formattedTime(_ timeInterval: TimeInterval) -> String {
            let minutes = Int(timeInterval) / 60
            let seconds = Int(timeInterval) % 60
            return String(format: "%02d:%02d", minutes, seconds)
        }
    
    func startStopwatch() {
            timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
                self.elapsedTime += 1
            }
        }
    
    func stopStopwatch() {
            timer?.invalidate()
            timer = nil
        }
    
    func resetStopwatch() {
        elapsedTime = 0.01
            stopStopwatch()
            isStopwatchRunning = false
        }
}

struct ProgressBar: View {
    @Binding var elapsedTime: TimeInterval
    let totalTime: TimeInterval = 60 // 1 minute
    var color: Color = Color("progbar")
    
    var body: some View {
        ZStack {
            Circle()
                .stroke(lineWidth: 20.0)
                .opacity(0.20)
                .foregroundColor(Color(hue: 0.776, saturation: 0.906, brightness: 0.746, opacity: 0.337))
            
            Circle()
                .trim(from: 0.0, to: CGFloat(min(elapsedTime / totalTime, 1.0)))
                .stroke(style: StrokeStyle(lineWidth: 12.0, lineCap:
                        .round, lineJoin: .round))
                .foregroundColor(color)
                .rotationEffect(Angle(degrees: 270))
                .animation(.linear(duration: 0.1)) // Adjust animation speed as needed
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
