//
//  ThirdPage.swift
//  Acucent iOS
//
//  Created by Hosna Molavi on 2024-05-08.
//

import SwiftUI

func processJsonString(_ jsonString: String) -> [String]{
    
    //remove curly bracet
    var cleanedString = jsonString.replacingOccurrences(of: "[{}]", with: "", options: .regularExpression)
    //remove quote
    cleanedString = cleanedString.replacingOccurrences(of: "\"", with: "")
    //Split by commas
    var components = cleanedString.split(separator: ",").map { String($0)}
    components = components.map{$0.capitalized}
   
    
    return components
}

let jsonString = """
{
    "swedish": 0.6,
    "german": 0.1,
    "english": 0.2,
    "indian": 0.1
}
"""
let result = processJsonString(jsonString)
let languages = result.map { $0.split(separator: ":")[0].trimmingCharacters(in: .whitespaces) }
var percentage = result.map { $0.split(separator: ":")[1].trimmingCharacters(in: .whitespaces) }
var doubles = percentage.compactMap(Double.init)
//var doubles = percentage.map{Int($0)}
var percentages = doubles.map {($0 * 100)}

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
                Text("\(languages[0])")
                    .font(.title3)
                    .fontWeight(/*@START_MENU_TOKEN@*/.bold/*@END_MENU_TOKEN@*/)
                    .foregroundColor(Color("DarkPurple"))
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
                    Text("\(languages[0])... \(percentages[0])%")
                    /*VStack{
                        Text("60 percent .... English")
                        Text("30 percent .... German")
                    }
                    .foregroundColor(.black) // Set the color as desired
                    .padding()
                    .background(Color("bgColour")) // Set the background color as desired
                    .cornerRadius(10)*/
                    
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

