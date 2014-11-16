import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;
/**
 * @author Haohan Zhu
 * This code is for sequence generator
 * The output includes two files: ServerSequence and ClientSequence
 * The parameters are sequence sizes, dimension and data range
 */
public class DataGenerator {
    
    public static void main(String args[]){
        
        int ClientSize = 60;
        int ServerSize = 40;
        int DIMENSION = 3;
        int RANGE = 100;

        try{
            FileWriter fstreamServer = new FileWriter("ServerSequence");
            FileWriter fstreamClient = new FileWriter("ClientSequence");
            BufferedWriter outServer = new BufferedWriter(fstreamServer);
            BufferedWriter outClient = new BufferedWriter(fstreamClient);
            Random r = new Random();
            for(int i = 0; i < ServerSize-1; i++){
                for (int j = 0; j < DIMENSION-1; j++){
                	outServer.write(r.nextInt(RANGE) + " ");
                }
                outServer.write(r.nextInt(RANGE) + ",");
            }
            outServer.write(""+r.nextInt(RANGE));
            outServer.close();
            for(int i = 0; i < ClientSize-1; i++){
                for (int j = 0; j < DIMENSION-1; j++){
                	outClient.write(r.nextInt(RANGE) + " ");
                }
                outClient.write(r.nextInt(RANGE) + ",");
            }
            outClient.write(""+r.nextInt(RANGE));
            outClient.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}
