import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Arrays;
import java.util.Scanner; // Import the Scanner class to read text files
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.io.IOException;
public class MoveFilesWav {

    public static void main (String[] args) {
        readFile("query.csv");
    }

    public static void readFile(String filepath) {
        try {
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            int i = 0;
            while (myReader.hasNextLine()) {
            data = myReader.nextLine();
            i++;
            processData(data);
            }
            System.out.println(i);
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
        String[] separatedData = data.split(";");
        System.out.println(Arrays.toString(separatedData));
        int charrep = separatedData[0].length()-4;
        System.out.println(separatedData[0].substring(0, charrep) + ".wav");
        Path source = Paths.get("allwav\\" + separatedData[0].substring(0, charrep) + ".wav");
        Path target = Paths.get("accentwav\\" + separatedData[1] + "\\" + separatedData[0].substring(0, charrep) + ".wav");
        
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        
    }

}