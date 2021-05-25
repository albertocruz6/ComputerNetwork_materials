import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;


public class TwoLayerSender implements Runnable {
	private static final int BUFFER_SIZE = 1024;
	private int port ;
	private static final String HOSTNAME = "localhost";
	private static final int BASE_SEQUENCE_NUMBER = 42;
	private DatagramSocket socket;
	private final String msg;


	public TwoLayerSender(DatagramSocket s, String m, int p) {
		this.socket = s;
		this.msg = m;
		this.port = p;

	}

	private void send() throws IOException {


		this.socket.setSoTimeout( 1000 );

		//This is the packet number or packet name
		Integer sequenceNumber = BASE_SEQUENCE_NUMBER;


		for (int i = 0; i < this.msg.length(); i++) {

			//Boolean used to check if message is received
			boolean isTimedOut = true;
			//To add unreliability packages will be





			while( isTimedOut ){
				int lostPacket = new Random().nextInt(5);
				int duplicatePacket = new Random().nextInt(10);
				sequenceNumber++;



				// Create a byte array for sending and receiving data
				byte[] msg = this.msg.substring(i, i+1).getBytes(StandardCharsets.UTF_8);
				byte[] resMsg = new byte[ BUFFER_SIZE ];

				// Get the IP address of the server
				InetAddress IPAddress = InetAddress.getByName( HOSTNAME );

				System.out.println( "Sending Packet: " + sequenceNumber  );
				// Get byte data for message
				byte[] seqMsg = ByteBuffer.allocate(4).putInt( sequenceNumber ).array();

				ByteArrayOutputStream output = new ByteArrayOutputStream();
				output.write(seqMsg);
				output.write(msg);

				byte sendMsg[] = output.toByteArray();

				try{
					if(lostPacket == 2) {
						System.out.println("Package was lost");
						continue;
					}
					else if(duplicatePacket == 1) {

						this.unreliableDuplicate(sendMsg, resMsg, IPAddress);

					}else {
						// Send the UDP Packet to the server
						DatagramPacket packet = new DatagramPacket(sendMsg, sendMsg.length, IPAddress, this.port);
						socket.send( packet );

						// Receive the server's packet
						DatagramPacket received = new DatagramPacket(resMsg, resMsg.length);
						socket.receive( received );

						// Get the message from the server's packet
						int returnMessage = ByteBuffer.wrap( received.getData( ) ).getInt();

						//Acknowledgement
						System.out.println( "Message from the Reciever:" + returnMessage );


						// If acknowledgement received, stop loop
						isTimedOut = false;
					}
				} catch( SocketTimeoutException exception ){
					// If Acknowledgement is not sent then resends the package
					System.out.println( "Timeout: Packet " + sequenceNumber  );
					sequenceNumber--;
				}

			}
		}

		this.socket.close();


	}
	@Override
	public void run(){
		try {
			this.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void unreliableDuplicate(byte[] sendMsg,byte[] resMsg, InetAddress IPAddress ) throws IOException {
		System.out.println("Package was duplicated");
		DatagramPacket packet = new DatagramPacket(sendMsg, sendMsg.length, IPAddress, this.port);
		socket.send( packet );
		socket.send( packet );

		// Receive the server's packet
		DatagramPacket received = new DatagramPacket(resMsg, resMsg.length);
		socket.receive( received );
		socket.receive( received );

		// Get the message from the server's packet
		int returnMessage = ByteBuffer.wrap( received.getData( ) ).getInt();

		//Acknowledgement
		System.out.println( "Message from the Reciever:" + returnMessage );


	}

}
