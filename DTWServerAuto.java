import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Haohan Zhu
 * Computer Science Department
 * Boston University
 * This file is for DTW privacy preserving calculations
 * This file is for Server use
 */

public class DTWServerAuto {
    
    public static void main(String[] args) throws Exception {
    	int port; // Port
    	String FILE_NAME = "ServerSequence"; // File name
        
        // Get command line argument
        if (args.length == 0) {
        	port = 1218;
        }
        else {
            port = Integer.parseInt(args[0]); // Get port
        }
        
        // Create a ServerSocket for a TCP server
        ServerSocket socket = new ServerSocket(port);
        InetAddress clientAddress = null;
        
        System.out.println("## Waiting for a new socket. ##");
        
        try{
        	// Create a socket to get incomming TCP packet.
        	Socket connectionSocket = socket.accept();
        	clientAddress = connectionSocket.getInetAddress();
        	System.out.println("## Connected by " + clientAddress.toString() + ". ## \n");
        	
        	OutputStream  out =  connectionSocket.getOutputStream(); //Output data stream
        	DataOutputStream dos = new DataOutputStream(out);
        	String outputString = "";
        	
        	InputStream in = connectionSocket.getInputStream(); //Input data stream
        	DataInputStream dis = new DataInputStream(in);
        	String inputString;
        	
        	boolean flag = true; // Flag for terminating sessions
        	Sequence<BigInteger> s = new Sequence<BigInteger>(); // Sequence from server side
        	Paillier paillier = new Paillier(); // Paillier Encrypt System
        	Scanner sc; //Read inputs
        	
        	long ServerPhase1Time = 0;
        	long ServerPhase2Time = 0;
        	long timerBefore = 0;
        	long timerAfter = 0;
        	long timerBegin = 0;
        	long timerMid = 0;
        	long timerEnd = 0;
        	long dataTransfer = 0;
        	
        	while(flag){
        		inputString  = receive(dis);
        		int number = 0;
        		if(inputString.equals("End Session."))
        			{number = 1;}
        		else if (inputString.equals("DTW Computation Request."))
        			{number = 2;}
        		else if (inputString.equals("Public Key Request."))
        			{number = 3;}
        		else if (inputString.equals("Public Key Received."))
        			{number = 4;}
        		else if (inputString.equals("Sequence Dimension and Length Request."))
        			{number = 5;}
        		else if (inputString.equals("Sequence Dimension and Length Reveiced."))
        			{number = 6;}
        		else if (inputString.equals("Encrypted Sequence Request."))
        			{number = 7;}
        		else if (inputString.equals("Encrypted Sequence Received."))
        			{number = 8;}
        		else if (inputString.equals("Minimum Value Request."))
        			{number = 9;}
        		else if (inputString.equals("Matrix Filled."))
        			{number = 10;}
        		else if (inputString.equals("Distance Verification."))
        			{number = 11;}
        		else
        			{number = 0;}

        		switch (number) {
        			case 1:
                            flag = false;
                            timerEnd = System.nanoTime();
                            System.out.println("## Information: Time for preprocessing at Server side is " + (timerMid-timerBegin)/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: Time for Phase 1 at Server side is " + ServerPhase1Time/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: Time for Phase 2 at Server side is " + ServerPhase2Time/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: " + dataTransfer*2/1000000.0 + " MB data tansferred ##"  );
                            System.out.println("## Information: Overall time is " + (timerEnd-timerBegin)/1000000000.0 + " seconds ##\n" );
                            break;
        			case 2:
                            timerBegin = System.nanoTime();
                            s = SequenceReader.ReadfromCSVFile(FILE_NAME);
                            outputString = "Sequence Generated.";
                            System.out.println("## Information: Sequence is extracted from local file: " + FILE_NAME + ". ##" );
                            break;
        			case 3:
                            outputString = "" + paillier.getn() + "," + paillier.getg() + "," + paillier.getBitLength();
                            System.out.println("## Information: Public key is sent to the client. ##" );
                            break;
        			case 4:
                            outputString = "Sequence Information Exchange.";
                            break;
        			case 5:
                            outputString = "" + s.getDimension() + "," + s.getLength();;
                            break;
        			case 6:
                            outputString = "Encrypted Sequence Information Exchange.";
                            timerMid = System.nanoTime();
                            break;
        			case 7:
                            timerBefore = System.nanoTime();
                            outputString = Enc(s, paillier);
                            timerAfter = System.nanoTime();
                            ServerPhase1Time += timerAfter - timerBefore;
                            dataTransfer += outputString.length();
                            System.out.println("## Information: Encrypted sequence information is sent to the client. ## \n" );
                            break;
        			case 8:
                            outputString = "Comparison Stage Begin.";
                            break;
        			case 9:
                            outputString = "Waiting for Inputs.";
                            send(dos, outputString);
                            inputString  = receive(dis);
                            timerBefore = System.nanoTime();
                            sc = new Scanner(inputString);
                            sc.useDelimiter(",");
                            LinkedList<String> input = new LinkedList<String>();
                            while(sc.hasNext()){
                                input.add(sc.next());
                            }
                            outputString = "" + min(input, paillier);
                            timerAfter = System.nanoTime();
                            ServerPhase2Time += timerAfter - timerBefore;
                            dataTransfer += outputString.length();
                            break;
        			case 10:
                            outputString = "Waiting for Distance.";
                            break;
        			case 11:
                            outputString = "Waiting for Inputs.";
                            send(dos, outputString);
                            inputString  = receive(dis);
                            sc = new Scanner(inputString);
                            outputString = paillier.Decryption(new BigInteger(sc.next())).toString();
                            System.out.println("$$ DTW Distance is: " + outputString + "$$\n");
                            break;
        			default:
                            outputString = "Error Verification.";
                            break;                         
        		}
        		if(flag) {
        			send(dos, outputString);
        		}
        	}
        	connectionSocket.close();
        }
        catch(SocketException e){
        	System.err.println("Socket error with " + clientAddress.toString() + ". Waiting for the next socket");
        }
    }
    
    //Send information
    private static void send(DataOutputStream dos, String outputString) throws IOException{
        byte[] message = outputString.getBytes();
        int len =  message.length;
        dos.writeInt(len);
        dos.write(message, 0, len);
    }
    
    //Receive information
    private static String receive(DataInputStream dis) throws IOException{
        int length = dis.readInt();
        byte[] data = new byte[length];
        dis.readFully(data, 0, length);
        String inputString = new String(data);
        return inputString;
    }
    
    //Compare a list of cipher texts and return a cipher text with minimal plain value (Can be changed to parallel computing)
    public static BigInteger min(LinkedList<String> input, Paillier p){
        PriorityQueue<BigInteger> plainText = new PriorityQueue<BigInteger>();
        for(int i = 0; i < input.size(); i++){
            plainText.add(p.Decryption(new BigInteger(input.get(i))));
        }
        return p.Encryption(plainText.peek());
    }
    
    //Encrypt the whole sequence (Can be changed to parallel computing)
    public static String Enc(Sequence<BigInteger> s, Paillier p){
        String strResult = "";
        BigInteger squareSum;
        for(int i = 0; i < s.getLength(); i++){
            squareSum = BigInteger.ZERO;
            for(int j = 0; j < s.getDimension(); j++){
                BigInteger temp = s.getElement(i).get(j);
                strResult += p.Encryption(temp) + " ";
                squareSum = squareSum.add(temp.multiply(temp));
            }
            strResult += p.Encryption(squareSum) + ",";
        } 
        return strResult;
    }

}
