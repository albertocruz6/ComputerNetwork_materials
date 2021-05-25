

import java.net.DatagramSocket;
import java.net.SocketException;
public class Two_Layer_Protocol {

	public static void main(String[] args) throws SocketException, InterruptedException {
		DatagramSocket socket = new DatagramSocket();
		if(args.length <2 || args.length >3) {
			System.out.println("Invalid argumets");
		}
		int port = Integer.parseInt(args[1].trim());
		System.out.println(args[0]);
		if(args[0].toLowerCase().equals("send")) {
			String msg = args[2];
			TwoLayerSender s = new TwoLayerSender(socket,msg,port);
			Thread t = new Thread(s);
		    t.start();
			
		}else if(args[0].toLowerCase().equals("receive")) {
			Receiver r = new Receiver(port);
			Thread t = new Thread(r);
		    t.start();
		    t.join();
		}
		
		
	}
}




