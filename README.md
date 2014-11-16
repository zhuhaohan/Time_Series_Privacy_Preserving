Time_Series_Privacy_Preserving
==============================

Code for Time Series Privacy Preserving:

[1] DataGenerator.java
	This file is used to generate two sequences. One for client, one for server. When generating, you can change sizes of sequences and dimensions of sequences. The sizes of sequences for client and server could be different.

[2] Sequence
	This file is for sequence class.

[3] SequenceReader.java
	This file is for reading sequence from the CSV file.

[4] Paillier.java
	This file is for Paillier encryption and decryption which is used by the server only. The code is written by Kun Liu. You can find the original file from http://www.csee.umbc.edu/~kunliu1/research/Paillier.html

[5] PaillierEncryptOnly.java
	This file is for Paillier encryption only which is used by the client only. The client can get the public key from the server and encrypt any data but cannot decrypt.

[6] DTWServerAuto.java
	This file is for the server to calculate Dynamic Time Warping. The file should take two arguments: port number (int) and file name (String). By default, port number is 1218 and file name is "ServerSequence".

[7] DTWClientAuto.java
	This file is for the client to calculate Dynamic Time Warping. The file should take four arguments: ip address (String), port number (int), file name (String) and random set size (int). By default, ip address is "localhost", port number is 1218, file name is "ClientSequence" and random set size is 100.

[8] DFDServerAuto.java
	This file is for the server to calculate Discrete Frechet Distance. The file should take two arguments: port number (int) and file name (String). By default, port number is 1218 and file name is "ServerSequence".

[9] DFDClientAuto.java
	This file is for the client to calculate Discrete Frechet Distance. The file should take four arguments: ip address (String), port number (int), file name (String) and random set size (int). By default, ip address is "localhost", port number is 1218, file name is "ClientSequence" and random set size is 100.