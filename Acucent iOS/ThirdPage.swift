//
//  ThirdPage.swift
//  Acucent iOS
//
//  Created by Hosna Molavi on 2024-05-08.
//

import SwiftUI

struct ThirdPage: View {
    
    @State private var isExpanded = false
    @State var textView: UILabel!
    
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
                Spacer()
                Text(" Our guess is that your accent is:  ").font(.title3).fontWeight(.bold).multilineTextAlignment(.center).italic()
                    .padding(.vertical, 3)
                Text("English").font(.title3).fontWeight(/*@START_MENU_TOKEN@*/.bold/*@END_MENU_TOKEN@*/).foregroundColor(Color("DarkPurple"))
                Text("Expand for more details")
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
                    
                    VStack{
                        Text("60 percent .... English")
                        Text("30 percent .... German")
                    }
                    .foregroundColor(.black) // Set the color as desired
                    .padding()
                    .background(Color("bgColour")) // Set the background color as desired
                    .cornerRadius(10)
                    
                }
                Spacer()
                
            }
        }
        
    }
    
}

struct ThirdPage_Previews: PreviewProvider {
    static var previews: some View{
        ThirdPage()
    }
}

