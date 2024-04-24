import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Arrays;
import java.util.Scanner; // Import the Scanner class to read text files
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class FileConverter {
    
    public static void main (String[] args) {
        // createFile("validated.txt");
        readFile("cv-corpus-17.0-2024-03-15-en.tar/cv-corpus-17.0-2024-03-15/en/validated.tsv");
    }

    public static void readFile(String filepath) {
        try {
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj);
            File rep = new File("validated.sql");
            if (rep.delete()) { 
                System.out.println("Deleted the file: " + myObj.getName());
              } else {
                System.out.println("Failed to delete the file.");
              }             
            FileWriter fstream = new FileWriter("validated.sql", true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("DROP TABLE validated;\n");

            out.write("CREATE TABLE validated (path varchar(255), locale varchar(255));\n");
            out.write("INSERT INTO validated (path, locale)\nVALUES\n");
            String data = myReader.nextLine();
            String[] array = data.split("\t");
            int acc = -1;
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals("accents"))
                    acc = i;
            }
            while (myReader.hasNextLine()) {
            data = myReader.nextLine();
            processData(data, out, acc);
        }
            myReader.close();
            out.write(";");
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Sucks to suck");
            e.printStackTrace();
        }
    
    }

    public static void processData(String data, BufferedWriter out, int arracc) throws IOException{
        String[] array = data.split("\t");
        // System.out.println(Arrays.toString(array));
        for (int i = 0; i < array.length; i++) {
            if (array[i].contains("'")) 
                return;
        }
        String accent = null;
        try {
            accent = processAccent(array[arracc]);
            
        } catch (Exception e) {
            // System.out.println(e);
        }
        if (accent != null) {
            out.write("('" + array[1] +"','"+ accent + "'),");
            out.newLine();
        }
    }

    public static void createFile(String filename) {
        try {
            File myObj = new File(filename);
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
            } else {
              System.out.println("File already exists.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    public static String processAccent(String accent) throws Exception{
        if (accent.contains("United States")) {
            return "american";
        }
        if (accent.contains("Australian")) {
            return "australian";
        }
        if (accent.contains("Canadian")) {
            return "canadian";
        }        
        if (accent.contains("England")) {
            return "english";
        }        
        if (accent.contains("Scottish")) {
            return "scottish";
        }   
        if (accent.contains("German")) {
            return "german";
        }   
        if (accent.contains("Malaysian")) {
            return "malaysian";
        }
        if (accent.contains("Hong Kong")) {
            return "hongkong";
        }
        if (accent.contains("British English")) {
            return "english";
        }  
        if (accent.contains("New Zealand English")) {
            return "newzealand";
        }  
        if (accent.contains("India")) {
            return "indian";
        }  
        if (accent.contains("Southern African")) {
            return "southernafrica";
        }  
        if (accent.contains("Irish")) {
            return "irish";
        }  
        if (accent.contains("Welch")) {
            return "swedish";
        }  
        // if (accent.contains("Swed")) {
        //     return "swedish";
        // }  
        // if (accent.contains("Swed")) {
        //     return "swedish";
        // }  
        // if (accent.contains("Swed")) {
        //     return "swedish";
        // }  
        // if (accent.contains("Swed")) {
        //     return "swedish";
        // }  
        // if (accent.contains("Swed")) {
        //     return "swedish";
        // }  
        throw new Exception("could not proccess " + accent);

        // if (accent.isBlank()) {
        //     throw new Exception("could not proccess " + accent);
        // }
        // return accent;
    }
    
    
}
