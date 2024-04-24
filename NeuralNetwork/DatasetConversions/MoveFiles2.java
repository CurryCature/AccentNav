import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Arrays;
import java.util.Scanner; // Import the Scanner class to read text files
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
public class MoveFiles2 {

    public static void main (String[] args) {
        readFile("query.csv");
    }

    public static void readFile(String filepath) {
        try {
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();

            while (myReader.hasNextLine()) {
            data = myReader.nextLine();
            processData(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Sucks to suck");
            e.printStackTrace();
        }
    
    }

    public static void processData(String data) throws IOException {
        
        try {
            String[] separatedData = data.split(";");
            Path source = Paths.get("cv-corpus-17.0-2024-03-15\\en\\clips\\" + separatedData[0]);
            Path target = Paths.get("all\\" + separatedData[0]);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(Arrays.toString(separatedData));

        } catch (NoSuchFileException e){
            return;
        }
        
        
    }

}