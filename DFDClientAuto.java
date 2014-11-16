import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Collections;

/**
 * Haohan Zhu
 * Computer Science Department
 * Boston University
 * This file is for DFD privacy preserving calculations
 * This file is for Client use
 */

public class DFDClientAuto {
       
    public static void main(String args[]) throws Exception {
    	String address; // IP address
    	int port; // Port
    	String FILE_NAME = "ClientSequence"; // File name
        int RND_SET = 100; // Random Set Size
        
        // Get command line argument
        if (args.length == 0) {
        	address = "localhost";
        	port = 1218;
        }
        else if (args.length == 1) {
        	address = args[0];
        	port = 1218;
        }
        else {
            address = args[0]; // Get IP address
            port = Integer.parseInt(args[1]); // Get port
        }
          
        InetAddress serverAddress;
          
        try{
        	// Create a socket to connect with a server.
        	Socket clientSocket = new Socket(address, port);
        	serverAddress = clientSocket.getInetAddress();
        	System.out.println("## Connected to " + serverAddress.toString() + ":" + clientSocket.getPort() + ". ## \n");
              
        	OutputStream  out =  clientSocket.getOutputStream(); //Output data stream
        	DataOutputStream dos = new DataOutputStream(out);
        	String outputString;
              
        	InputStream in = clientSocket.getInputStream(); //Input data stream
        	DataInputStream dis = new DataInputStream(in);
        	String inputString;
              
        	long timer1 = System.nanoTime();
        	boolean flag = true; // Flag for terminating sessions
        	Sequence<BigInteger> s = new Sequence<BigInteger>(); // Sequence from client side
        	s = SequenceReader.ReadfromCSVFile(FILE_NAME);
        	PaillierEncryptOnly paillier = new PaillierEncryptOnly(); // Paillier Encrypt System for encryption only
        	Scanner sc; //Read inputs
              
        	System.out.println("## Information: Sequence is extracted from file: " + FILE_NAME + ". ## \n");
              
        	Sequence<BigInteger> q = new Sequence<BigInteger>(); // Sequence from server side
        	int serverDimension=0; //Server sequence dimension
        	int serverSize=0; //Server sequence size
        	BigInteger result; // Final DTW distance          
              
        	LinkedList<BigInteger> qSum = new LinkedList<BigInteger>(); // Encryption of summations from server's sequence
              
        	//Matrix to Record Encryptions of Squares of Euclidean Distances
        	LinkedList<BigInteger>[] distanceMatrix = new LinkedList[s.getLength()];
        	for(int i = 0; i< distanceMatrix.length; i++){
        		distanceMatrix[i] = new LinkedList<BigInteger>();
        	}
              
        	//Matrix to Record intermediate values for dynamic programming
        	LinkedList<BigInteger>[] dynamicMatrix = new LinkedList[s.getLength()];
        	for(int i = 0; i< dynamicMatrix.length; i++){
        		dynamicMatrix[i] = new LinkedList<BigInteger>();
        	}
              
        	LinkedList<LinkedList<BigInteger>>[] randomSetMin = new LinkedList[s.getLength()];
        	for(int i = 0; i< randomSetMin.length; i++){
        		randomSetMin[i] = new LinkedList<LinkedList<BigInteger>>();
        	}

        	LinkedList<LinkedList<BigInteger>>[] randomSetMax = new LinkedList[s.getLength()];
        	for(int i = 0; i< randomSetMax.length; i++){
        		randomSetMax[i] = new LinkedList<LinkedList<BigInteger>>();
        	}
              
        	outputString = "DTW Computation Request."; //Initialize the session
        	send(dos, outputString);   
              
        	long timer2 = System.nanoTime(); 
        	System.out.println("## Information: Offline Preprocessing time is: " + (timer2-timer1)/1000000000.0 + " seconds. ##\n");
        	long ClientPhase1Time = 0;
        	long CommPhase1Time = 0;
        	long ClientPhase2Time = 0;
        	long CommPhase2Time = 0;
        	long ClientPhase3Time = 0;
        	long CommPhase3Time = 0;
        	long timerBefore = 0;
        	long timerAfter = 0;
        	long timerBegin = 0;
        	long timerMid = 0;
        	long timerEnd = 0;
        	long dataTransfer = 0;
              
        	while(flag){
        		inputString  = receive(dis);
        		int number = 0;
        		if(inputString.equals("Error Verification."))
                       {number = 1;}
        		else if (inputString.equals("Sequence Generated."))
                       {number = 2;}
        		else if (inputString.equals("Sequence Information Exchange."))
                       {number = 3;}
        		else if (inputString.equals("Encrypted Sequence Information Exchange."))
                       {number = 4;}
        		else if (inputString.equals("Comparison Stage Begin."))
                       {number = 5;}
        		else if (inputString.equals("Waiting for Distance."))
                       {number = 6;}
        		else
                       {number = 0;}

        		switch (number) {
        			case 1:
                            flag = false;
                            outputString = "End Session.";
                            break;
        			case 2:
                            timerBegin = System.nanoTime(); 
                            outputString = "Public Key Request.";
                            send(dos, outputString); 
                            inputString  = receive(dis);
                            sc = new Scanner(inputString);
                            sc.useDelimiter(",");
                            BigInteger n = new BigInteger(sc.next());
                            BigInteger g = new BigInteger(sc.next());
                            int bitLength = sc.nextInt();
                            paillier.setPaillierEncryptOnly(bitLength, g, n);
                            outputString = "Public Key Received.";
                            System.out.println("## Information: Public key is received from the server. ## \n" );
                            break;
        			case 3:
                            outputString = "Sequence Dimension and Length Request.";
                            send(dos, outputString); 
                            inputString  = receive(dis);
                            sc = new Scanner(inputString);
                            sc.useDelimiter(",");
                            serverDimension = sc.nextInt();
                            serverSize = sc.nextInt();
                            if(serverDimension != s.getDimension()){
                                outputString = "Error: Dimensions are not consistent.";
                            }
                            else{
                                outputString = "Sequence Dimension and Length Reveiced.";
                                distanceMatrix = initialMatrix(distanceMatrix, serverSize);
                                dynamicMatrix = initialMatrix(dynamicMatrix, serverSize);
                                randomSetMin = initialRandomSet(randomSetMin, serverSize, RND_SET);
                                randomSetMax = initialRandomSet(randomSetMax, serverSize, RND_SET);
                                System.out.println("## Information: (" + (s.getLength()+1) + "*" + (serverSize+1) + ") cipher matrix is generated ##");
                                System.out.println("## Information: (" + (s.getDimension()+1) + ")*" + (serverSize+1) + " cipher texts needed from server. ##\n");
                            }
                            timerMid = System.nanoTime();
                            break;
        			case 4:
                            outputString = "Encrypted Sequence Request.";
                            timerBefore = System.nanoTime();
                            send(dos, outputString); 
                            inputString  = receive(dis);
                            timerAfter = System.nanoTime();
                            CommPhase1Time += timerAfter - timerBefore;
                            timerBefore = System.nanoTime();
                            sc = new Scanner(inputString);
                            sc.useDelimiter(",");
                            boolean localFlag = true;
                            while(sc.hasNext()){
                                String element = sc.next();
                                Scanner elementVector = new Scanner(element);
                                LinkedList<BigInteger> v = new LinkedList<BigInteger>();
                                while(elementVector.hasNext()){
                                    v.add(new BigInteger(elementVector.next()));
                                }
                                elementVector.close();
                                qSum.add(v.getLast());
                                v.removeLast();
                                if(v.size()==serverDimension){
                                    q.insertElement(v);
                                }
                                else{
                                    outputString = "Error: Dimensions are not consistent.";
                                    localFlag = false;
                                    break;
                                }
                            }
                            if(localFlag){
                                if(q.getLength()!=serverSize){
                                    outputString = "Error: Sizes are not consistent.";
                                }
                                else{
                                    outputString = "Encrypted Sequence Received.";
                                    System.out.println("## Information: Start computing Euclidean distances. ##");
                                    distanceMatrix = fillDistanceMatrix(distanceMatrix, s, q, qSum, paillier);
                                    System.out.println("## Information: Finish computing Euclidean distances. ##\n");
                                    dynamicMatrix[0].set(0, distanceMatrix[0].get(0));
                                    for(int i = 1; i < dynamicMatrix.length; i++){
                                        BigInteger temp = paillier.HomomorphicAddition(dynamicMatrix[i-1].get(0), distanceMatrix[i].get(0));
                                        dynamicMatrix[i].set(0,temp);
                                    }
                                    for(int i = 1; i < dynamicMatrix[0].size(); i++){
                                        BigInteger temp = paillier.HomomorphicAddition(dynamicMatrix[0].get(i-1), distanceMatrix[0].get(i));
                                        dynamicMatrix[0].set(i,temp);
                                    }
                                    timerAfter = System.nanoTime();
                                    ClientPhase1Time += timerAfter - timerBefore;
                                }
                            }
                            break;
        			case 5:
                            System.out.println("## Information: Start filling matrix. ##");
                            for(int i = 1; i < dynamicMatrix.length; i++){
                                for(int j = 1; j < dynamicMatrix[i].size(); j++){
                                    outputString = "Minimum Value Request.";
                                    send(dos, outputString);
                                    receive(dis);
                                    timerBefore = System.nanoTime();
                                    BigInteger minRandom = paillier.Encryption(randomSetMin[i].get(j).peek().negate());
                                    outputString = candidatesGenerator(paillier, randomSetMin[i].get(j), dynamicMatrix[i-1].get(j), dynamicMatrix[i-1].get(j-1), dynamicMatrix[i].get(j-1)); 
                                    dataTransfer += outputString.length();
                                    timerAfter = System.nanoTime();
                                    ClientPhase2Time += timerAfter - timerBefore;
                                    timerBefore = System.nanoTime();
                                    send(dos, outputString);
                                    inputString  = receive(dis);
                                    timerAfter = System.nanoTime();
                                    CommPhase2Time += timerAfter - timerBefore; 
                                    sc = new Scanner(inputString);
                                    BigInteger min = new BigInteger(sc.next());
                                    timerBefore = System.nanoTime();
                                    BigInteger finalMin = paillier.HomomorphicAddition(min, minRandom);
                                    timerAfter = System.nanoTime();
                                    ClientPhase2Time += timerAfter - timerBefore;

                                    Collections.reverse(randomSetMax[i].get(j));

                                    timerBefore = System.nanoTime();
                                    BigInteger maxRandom = paillier.Encryption(randomSetMax[i].get(j).peek().negate());
                                    outputString = candidatesGenerator2(paillier, randomSetMax[i].get(j), finalMin, distanceMatrix[i].get(j)); 
                                    dataTransfer += outputString.length();
                                    timerAfter = System.nanoTime();
                                    ClientPhase3Time += timerAfter - timerBefore;
                                    timerBefore = System.nanoTime();
                                    send(dos, outputString);
                                    inputString  = receive(dis);
                                    timerAfter = System.nanoTime();
                                    CommPhase3Time += timerAfter - timerBefore; 
                                    sc = new Scanner(inputString);
                                    BigInteger max = new BigInteger(sc.next());
                                    timerBefore = System.nanoTime();
                                    BigInteger finalMax = paillier.HomomorphicAddition(max, maxRandom);
                                    dynamicMatrix[i].set(j, finalMax);
                                    timerAfter = System.nanoTime();
                                    ClientPhase3Time += timerAfter - timerBefore;
                                }
                            }
                            System.out.println("## Information: Finish filling matrix. ##\n");
                            outputString = "Matrix Filled.";
                            break;
        			case 6:
                            outputString = "Distance Verification.";
                            send(dos, outputString);
                            receive(dis);
                            outputString = ""+dynamicMatrix[s.getLength()-1].get(q.getLength()-1);
                            send(dos, outputString);
                            inputString = receive(dis);
                            sc = new Scanner(inputString);
                            result = new BigInteger(sc.next());
                            timerEnd = System.nanoTime();
                            System.out.println("## Information: Time for preprocessing at Client side is " + (timerMid-timerBegin)/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: Time for Phase 1 at Client side is " + ClientPhase1Time/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: Time for Phase 1 during Communication " + CommPhase1Time/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: Time for Phase 2 at Client side is " + ClientPhase2Time/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: Time for Phase 2 during Communication " + CommPhase2Time/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: Time for Phase 3 at Client side is " + ClientPhase3Time/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: Time for Phase 3 during Communication " + CommPhase3Time/1000000000.0 + " seconds ##" );
                            System.out.println("## Information: " + dataTransfer*2/1000000.0 + " MB data tansferred ##");
                            System.out.println("## Information: Overall time is " + (timerEnd-timerBegin)/1000000000.0 + " seconds ##\n" );
                            System.out.println("$$ DFD Distance is: " + result + " $$\n");
                            flag = false;
                            outputString = "End Session.";
                            break;
                        default:
                            outputString = "Error.";
                            break;                         
                    }
                    
                    send(dos, outputString);
              }
              // Close the socket
              clientSocket.close();
          }
          catch(ConnectException e){ // catch connection failed exception
              System.out.println("Connection failed. Socket error with " + address + ":" + port + ".");
          }
	}
    
    private static void send(DataOutputStream dos, String outputString) throws IOException{

        byte[] message = outputString.getBytes();
        int len =  message.length;
        dos.writeInt(len);
        dos.write(message, 0, len);
    }
    
    private static String receive(DataInputStream dis) throws IOException{
        int length = dis.readInt();
        byte[] data = new byte[length];
        dis.readFully(data, 0, length);
        String inputString = new String(data);
        return inputString;
    }
    
    private static LinkedList<BigInteger>[] initialMatrix(LinkedList<BigInteger>[] m, int rows){
        for(int i = 0; i< m.length; i++){
            for(int j = 0; j< rows; j++){
                m[i].add(BigInteger.ZERO);
            }
        }
        return m;
    }
    
    private static LinkedList<LinkedList<BigInteger>>[] initialRandomSet(LinkedList<LinkedList<BigInteger>>[] randomSet, int rows, int RND_SET){
        for(int i = 0; i< randomSet.length; i++){
            for(int j = 0; j< rows; j++){
                randomSet[i].add(randomSetGenerator(RND_SET, 1));
            }
        }
        return randomSet;
    }
    
    private static LinkedList<BigInteger>[] fillDistanceMatrix(LinkedList<BigInteger>[] m, Sequence<BigInteger> s, Sequence<BigInteger> q, LinkedList<BigInteger> qSum, PaillierEncryptOnly p){
        for(int i = 0; i < m.length; i++){
            for(int j = 0; j < m[i].size(); j++){
               m[i].set(j, computeEuclideanDis(q.getElement(j), qSum.get(j), s.getElement(i), p));
            }
        }
        return m;
    }
    
    private static BigInteger computeEuclideanDis(LinkedList<BigInteger> enS, BigInteger enSumS, LinkedList<BigInteger> plainQ, PaillierEncryptOnly p){
        BigInteger result;
        BigInteger sumQ = BigInteger.ZERO;
        BigInteger enSQ = BigInteger.ZERO;
        for(int i = 0; i < plainQ.size(); i++){
            BigInteger temp = plainQ.get(i).negate().multiply(new BigInteger("2"));
            enSQ = p.HomomorphicAddition(enSQ, p.HomomorphicMultiplication(enS.get(i), temp));
            sumQ = sumQ.add(plainQ.get(i).multiply(plainQ.get(i)));
        }
        result = p.HomomorphicAddition(enSumS, enSQ);
        result = p.HomomorphicAddition(result, p.Encryption(sumQ));
        return result;
    }
    
    private static LinkedList<BigInteger> randomSetGenerator(int size, int RandomNumberLength){
        PriorityQueue<BigInteger> randomQueue = new PriorityQueue<BigInteger>();
        LinkedList<BigInteger> randomSet = new LinkedList<BigInteger>();
        SecureRandom random = new SecureRandom();
        for(int i =0; i<size; i++){
            byte[] bytes = new byte[RandomNumberLength];
            random.nextBytes(bytes);
            BigInteger r = new BigInteger(bytes).abs();
            randomQueue.add(r);
        }
        for(int i =0; i<size; i++){
            randomSet.add(randomQueue.poll());
        }
        return randomSet;
    }
    
    private static String candidatesGenerator(PaillierEncryptOnly p, LinkedList<BigInteger> randomSet, BigInteger a, BigInteger b, BigInteger c){
        String candidates = "";
        BigInteger minRandom = randomSet.poll();
        candidates += p.HomomorphicAddition(a, p.Encryption(minRandom)) + ",";
        candidates += p.HomomorphicAddition(b, p.Encryption(minRandom)) + ",";
        candidates += p.HomomorphicAddition(c, p.Encryption(minRandom)) + ",";
        while(!randomSet.isEmpty()){
            BigInteger r = randomSet.poll();
            Random assign = new Random();
            Double assignD = assign.nextDouble();
            if(assignD < 0.333){
                candidates += p.HomomorphicAddition(a, p.Encryption(r)) + ",";
            }
            else if (assignD > 0.667){
                candidates += p.HomomorphicAddition(b, p.Encryption(r)) + ",";
            }
            else{
                candidates += p.HomomorphicAddition(c, p.Encryption(r)) + ",";
            }    
        }
        return candidates.substring(0, candidates.length()-1);
    }

    private static String candidatesGenerator2(PaillierEncryptOnly p, LinkedList<BigInteger> randomSet, BigInteger a, BigInteger b){
        String candidates = "";
        BigInteger minRandom = randomSet.poll();
        candidates += p.HomomorphicAddition(a, p.Encryption(minRandom)) + ",";
        candidates += p.HomomorphicAddition(b, p.Encryption(minRandom)) + ",";
        while(!randomSet.isEmpty()){
            BigInteger r = randomSet.poll();
            Random assign = new Random();
            Double assignD = assign.nextDouble();
            if(assignD < 5){
                candidates += p.HomomorphicAddition(a, p.Encryption(r)) + ",";
            }
            else{
                candidates += p.HomomorphicAddition(b, p.Encryption(r)) + ",";
            }    
        }
        return candidates.substring(0, candidates.length()-1);
    }

}
