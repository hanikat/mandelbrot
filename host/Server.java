
package host;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;



/**
 * This class represents the server which recieves connections and creates a worker for each connection to distribute work.
 * @author Marcus Hanikat, Hanikat@kth.se
 * @version 1.0 - 08/10/2019
 */
public class Server{
	
	static ServerSocket sSock = null;
	
	
	public static void main(String[] args) {
		
		
		//Argument sanity check
		if (args.length < 1) {
			throw new java.lang.IllegalArgumentException("Too few arguments!");
		} else if (args.length > 1) {
			throw new java.lang.IllegalArgumentException("Too many arguments!");
		}
		
		int port = 0;
		
		try {
			port = Integer.parseInt(args[0]);
		} catch (java.lang.NumberFormatException e) {
			throw new java.lang.IllegalArgumentException("Invalid port supplied!");
		}
		handleConnections(port);
	}
	
	/**
	 * Method used to handle connection attempts to the server and distribute the workload to worker class.
	 */
	private static void handleConnections(int port) {
		
		//Create a ServerSocket to accept connections to
		try {
			sSock = new ServerSocket(port, 0, InetAddress.getByName(null));
		} catch (UnknownHostException e) {
			System.out.println("Unable to bind to localhost");
		} catch (IOException e) {
			System.out.println("IOException during socket creation: " + e.getMessage());
		}
		
		//Main program loop for accepting connections and then "forking" the workload to new threads
		while(true) {	
			Socket sock = null;
			try {
				sock = sSock.accept();
			} catch (IOException e) {
				System.out.println("IOException during socket.accept(): " + e.getMessage());
			}
			Thread t1 = new Thread(new Worker(sock));
			t1.start();	
		}	
	}
}
