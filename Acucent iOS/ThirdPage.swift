//
//  ThirdPage.swift
//  Acucent iOS
//
//  Created by Hosna Molavi on 2024-05-08.
//

import SwiftUI

struct ThirdPage: View {
    
    @State private var isExpanded = false
    
    var body: some View {
        ZStack{
            Color("bgColour").ignoresSafeArea()
            VStack(){
                Image("logo")
                    .resizable()
                    .cornerRadius(15)
                    .frame(maxWidth:150 , maxHeight:80)
                    .aspectRatio(contentMode: .fit)
                    .padding()
                Text(" Our guess is that your accent is:  ").font(.title3).fontWeight(.bold).multilineTextAlignment(.center).italic()
                    .padding(.vertical, 3)
                Button(action: {
                    withAnimation {
                        isExpanded.toggle()
                    }
                }) {
                    Image(systemName: isExpanded ? "arrowshape.up.fill" : "arrowshape.down.fill")
                        .resizable()
                        .foregroundColor(Color("DarkPurple"))
                        .cornerRadius(15)
                        .frame(maxWidth:20 , maxHeight:25)
                        .aspectRatio(contentMode: .fit)
                        .padding(.all)
                }
                
                if isExpanded {
                    BottomSheetView()
                        .frame(maxWidth: .infinity)
                        .frame(height: 200) // Set your desired height here
                        .background(Color("bgColour"))
                        .cornerRadius(20)
                        .shadow(radius: 5)
                }
                
            }
        }
        
    }
    
    struct BottomSheetView: View {
        @State private var text = ""
        
        var body: some View {
            VStack {
                Text("English")
                Text("German ")
                Button("Close") {
                    // Close the bottom sheet
                }
                .padding()
            }
            .padding()
            .background(Color("bgColour"))
            .frame(maxWidth: .infinity)
            .frame(minHeight: 500)
            .frame(height: 700)
            .cornerRadius(20)
            .shadow(radius: 5)
        }
        
    }
}

struct ThirdPage_Previews: PreviewProvider {
    static var previews: some View{
        ThirdPage()
    }
}

