//
//  ContentView.swift
//  Acucent iOS
//
//  Created by Turja Das on 2024-04-24.
//

import SwiftUI

struct ContentView: View {
    @State private var isPressed = false
    @State private var counter = 0
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
                    
                    ZStack {
                        
                        ProgressBar(progress: self.$progressValue)
                            .frame(width: 200, height: 200.0)
                            .padding(20.0).onAppear() {
                                self.progressValue = 0.5
                            }
                        
                        Image(systemName: "mic.fill")
                            .resizable()
                            .foregroundColor(Color(hue: 0.776, saturation: 0.906, brightness: 0.746, opacity: 0.337))
                            .cornerRadius(15)
                            .frame(maxWidth:105 , maxHeight:145)
                            .aspectRatio(contentMode: .fit)
                            .padding(.all)
                    }
                    .scaleEffect(isPressed ? 1.27 : 1.0)
                    .opacity(isPressed ? 0.6 : 1.0)
                    .onTapGesture {
                            counter += 1
                        }
                        .pressEvents {
                            // On press
                            withAnimation(.easeInOut(duration: 0.1)) {
                                isPressed = true
                            }
                        } onRelease: {
                            withAnimation {
                                isPressed = false
                            }
                        }
                }
                Spacer()
                
                
               
            }
            
        }
        
            
    }
}

struct ProgressBar: View {
    @Binding var progress: Float
    var color: Color = Color("progbar")
    
    var body: some View {
        ZStack {
            Circle()
                .stroke(lineWidth: 20.0)
                .opacity(0.20)
                .foregroundColor(Color(hue: 0.776, saturation: 0.906, brightness: 0.746, opacity: 0.337))
            
            Circle()
                .trim(from: 0.0, to: CGFloat(min(self.progress, 1.0)))
                .stroke(style: StrokeStyle(lineWidth: 12.0, lineCap:
                        .round, lineJoin: .round))
                .foregroundColor(color)
                .rotationEffect(Angle(degrees: 270))
                .animation(.easeIn(duration: 2.0))
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
