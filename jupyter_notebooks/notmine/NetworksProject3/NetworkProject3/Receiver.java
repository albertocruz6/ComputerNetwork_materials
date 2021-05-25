import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class Receiver implements Runnable {

	private static final int BUFFER_SIZE = 1024;
	private int port;

	public Receiver(int p) {
		this.port = p;
	}
	@SuppressWarnings("resource")
	private void receive() throws IOException {
		DatagramSocket serverSocket = new DatagramSocket( this.port );

		// Set up byte arrays for sending/receiving data
        byte[] receiveData = new byte[ BUFFER_SIZE ];
        byte[] dataForSend = new byte[ BUFFER_SIZE ];

        // Infinite loop to check for connections
        while(true){

        	// Get the received packet
        	DatagramPacket received = new DatagramPacket( receiveData, receiveData.length );
          	serverSocket.receive( received );

          	// Get the message from the packet
						byte[] recArr = received.getData();

          	String message = IntStream.range(4, recArr.length).mapToObj(i -> Byte.toString(recArr[i])).collect(Collectors.joining(" "));

						String[] temp = message.split(" ");
						char msg[] = new char[temp.length];
						for (int i = 0; i < temp.length; i++){
							msg[i] = (char)Integer.parseInt(temp[i]);
						}

						message = new String(msg).trim();

            Random random = new Random( );
            int chance = random.nextInt( 100 );
						int seqNum = ByteBuffer.wrap(Arrays.copyOfRange(recArr, 0, 4)).getInt();
						ByteBuffer reply;

            // 1 in 2 chance of responding to the message
            if( ((chance % 2) == 0) ){
              System.out.println("FROM CLIENT: " + message + "\nSEQ: " + seqNum);

              // Get packet's IP and port
              InetAddress IPAddress = received.getAddress();
              int port = received.getPort();

              // Convert message to uppercase
              dataForSend = ByteBuffer.allocate(4).putInt( seqNum ).array();

              // Send the packet data back to the client
              DatagramPacket packet = new DatagramPacket( dataForSend, dataForSend.length, IPAddress, port );
              serverSocket.send( packet );
            } else {
              System.out.println( "Oops, packet with sequence number "+ seqNum + " was dropped");
            }
       	}

	}

	@Override
	public void run() {
		try {
			this.receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
