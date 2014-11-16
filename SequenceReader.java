import java.io.*;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;
/**
 * @author Haohan Zhu
 * This code is Sequence Reader
 */
public class SequenceReader {
    
    // Sequences is in CSV File. Different elements of sequences are separated by comma. Each element is a vector of integer numbers separated by white space
    public static Sequence<BigInteger> ReadfromCSVFile(String FILE_NAME) throws FileNotFoundException {
    	Sequence<BigInteger> s = new Sequence<BigInteger>(); 
    	try{
            Scanner inputFile = new Scanner(new File(FILE_NAME));
            inputFile.useDelimiter(",");
            while(inputFile.hasNext()){
                String element = inputFile.next();
                Scanner elementScanner = new Scanner(element);
                LinkedList<BigInteger> v = new LinkedList<BigInteger>();
                while(elementScanner.hasNext()){
                    v.add(new BigInteger(elementScanner.next()));
                }
                elementScanner.close();
                s.insertElement(v);
            }
            inputFile.close();
        }
        catch(FileNotFoundException e) {
         System.err.println("File name cannot be found");
        }
        return s;
    }
}
