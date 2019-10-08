package client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import Utility.Request;


/**
 * This class is used to represent the remote servers which mandelbrot-requests can be distributed to
 * @author Marcus Hanikat, Hanikat@kth.se
 * @version 1.0 - 08/10/2019
 */
public class RemoteServer extends Thread {
	
	private String hostname;
	private int port;
	public Request req;
	public int worker;
	public int sentRequests;
	
	/**
	 * Creates a new instance of RemoteServer with the supplied hostname and port
	 * @param h, the hostname of the RemoteServer
	 * @param p, the port of the RemoteServer
	 */
	public RemoteServer(String h, int p) {
		this.hostname = h;
		this.port = p;
		this.sentRequests = 0;
		this.worker = 0;
	}
	
	
	/**
	 * Method to execute as its own thread, used to open and wait for GET-requests to finish
	 */
	public void run() {
		this.sendRequest(req, worker);
	}
	
	
	/**
	 * Sends an request to the remote server using the parameters supplied in the Request instance supplied as argument
	 * @param Req, the Request which holds the parameters for computing the mandelbrot set
	 * @param Worker, the current threads worker number used to keep track of which part of the picture the server should compute
	 * @throws Exception
	 */
	public void sendRequest(Request Req, int Worker) {
		//Increment the sentRequest counter used for load balancing
		this.sentRequests++;
		
		//Prepare string to send as GET-request to the server
		String reqUrl = "GET /mandelbrot/" + 
				Double.toString(Req.min_c_re) + 
				"/" + Double.toString(Req.min_c_im) + 
				"/" + Double.toString(Req.max_c_re) + 
				"/" + Double.toString(Req.max_c_im) + 
				"/" + Integer.toString(Req.x) + 
				"/" + Integer.toString(Req.y) + 
				"/" + Integer.toString(Req.n) + "/" + Integer.toString(worker) + "/" + Integer.toString(Req.split) + "\r\n";
		
		//Setup and connect socket to server
		Socket sock = new Socket();
		InetAddress addr;
		try {
			addr = InetAddress.getByName(this.hostname);
			SocketAddress sadr = new InetSocketAddress(addr, this.port);
			sock.connect(sadr);
		} catch (UnknownHostException e) {
			System.out.println("The supplied hostname cannot be found: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("There was a problem establishing the connection to the remote host: " + e.getMessage());
		}
		
		//Create a PrintWriter and send the previously prepared string to the server
		PrintWriter out;
		try {
			out = new PrintWriter(sock.getOutputStream(), true);
			out.println(reqUrl);
		} catch (IOException e) {
			System.out.println("There was a problem writing output to the remote host: " + e.getMessage());
		}
		
		
		//Await response from the server and read this into a BufferedImage
		BufferedImage img = null;
		try {
			img = ImageIO.read(sock.getInputStream());
		} catch (IOException e) {
			System.out.println("There was a problem reading the response from the remote host: " + e.getMessage());
		}
		
		//Received the image, now write the corresponding part into the request to complete the image
		this.req.image.getGraphics().drawImage(img, 0, (this.req.image.getHeight()/this.req.split) * Worker, this.req.x, img.getHeight(), null);
		
		//Send notification to the request class that another worker has finished
		this.req.print();
		
		//Close socket and end thread execution
		try {
			sock.close();
		} catch (IOException e) {
			System.out.println("There was a problem closing the connection to the remote host: " + e.getMessage());
		}
	}
	
	
}