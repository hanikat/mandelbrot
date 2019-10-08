package host;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.imageio.ImageIO;

import Utility.Request;


/**
 * This class is used to handle the connections established to the socket in the parameter
 * The class is used to parse a single request received from the socket, calculate the mandelbrot set from the supplied parameters and send an response through the socket.
 * @author Marcus Hanikat, Hanikat@kth.se
 * @version 1.0 - 08/10/2019
 */
public class Worker implements Runnable {

	private Request request;
	private Socket sock;
	
	/**
	 * Creates a new worker which handles the connection related to socket
	 * @param Sock, the socket of an established connection
	 */
	public Worker (Socket Sock) {
		this.sock = Sock;
	}
	
	/**
	 * Function called when the request is received and a new thread is started to handle the connection
	 */
	@Override
	public void run() {
		//Thread startup
		
		
		//Try to read from the socket in the accepted connection
		InputStream sis;
		BufferedReader br;
		String read = "";
		try {
			sis = this.sock.getInputStream();
			br = new BufferedReader(new InputStreamReader(sis));
			read = br.readLine();
			
		} catch (IOException e1) {
			System.out.println("Error reading from socket: " + e1.getMessage());
		}
		
		//Split the string into the different supplied arguments
		String[] reqParam = read.replace(" ", "/").split("/");
		
		//Check to see if this request is sent to one or several servers
		//If the request is sent to several servers there will be two more arguments specifying which worker number this thread have and the total amount of working threads
		if(reqParam[2].equals("mandelbrot")) {
			
			if(reqParam.length < 11) {
				//Only one server processing the request, parse arguments into request class
				this.request = new Request(	Double.parseDouble(reqParam[3]), 
						Double.parseDouble(reqParam[4]),
						Double.parseDouble(reqParam[5]),
						Double.parseDouble(reqParam[6]),
						Integer.parseInt(reqParam[9]),
						Integer.parseInt(reqParam[7]),
						Integer.parseInt(reqParam[8]));
				
			} else {
				//Two or more servers processing the request, parse arguments into the request class
				this.request = new Request(Double.parseDouble(reqParam[3]), 
						Double.parseDouble(reqParam[4]),
						Double.parseDouble(reqParam[5]),
						Double.parseDouble(reqParam[6]),
						Integer.parseInt(reqParam[9]),
						Integer.parseInt(reqParam[7]),
						Integer.parseInt(reqParam[8]),
						Integer.parseInt(reqParam[10]),
						Integer.parseInt(reqParam[11]));
						
			}
			
			//Start building the image
			try {
				this.mandelbrot();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			System.out.println("Error parsing request from esatblished connection.");
		}
		
		
	}
	
	/**
	 * Computes the mandelbrot set for the parameters stored in the request variable
	 * @throws IOException if there is a problem writing to the socket supplied when the class is created
	 */
	private void mandelbrot() throws IOException {
		//Source of code: https://github.com/joni/fractals/blob/master/mandelbrot/MandelbrotColor.java (Don't reinvent the wheel ;) )
		
		BufferedImage image = this.request.image;
		int black = 0x0;
		
		//Define color range
		int[] colors = new int[this.request.n];
		for(int i = 0; i < this.request.n; i++) {
			colors[i] = Color.HSBtoRGB(i/256f, i/256f, i/256f);
			
		}
		int yStart = (this.request.y * this.request.workerNo)/this.request.split;
		int yStop = (this.request.y * (this.request.workerNo + 1))/this.request.split;
		
		
		//Compute the given part of the image
		for(int row = yStart; row < yStop; row++) {
			for(int col = 0; col < this.request.x; col++) {
				double x = 0, y = 0;
				int iterations = 0;
				while(x*x + y*y < 4 && iterations < this.request.n) {
					double c_re = (col - this.request.x/2) * 4.0/this.request.x;
					//Keep c_re in range between c_re_min and c_re_max
					Double.min(c_re, this.request.max_c_re);
					Double.max(c_re, this.request.min_c_re);
					
					double c_im = (row - this.request.y/2.0) * 4.0/this.request.x;
					//Keep c_re in range between c_im_min and c_im_max
					Double.min(c_im, this.request.max_c_im);
					Double.max(c_im, this.request.min_c_im);
					
					
					double x_new = x*x - y*y + c_re;
					y = 2*x*y + c_im;
					x = x_new;
					iterations++;
				}
				if(iterations < this.request.n) {
					image.setRGB(col, row, colors[iterations]);
				} else {
					image.setRGB(col, row, black);
				}
			}
		}
		
		//Write the image to the socket supplied to the current thread and then close it
		ImageIO.write(image.getSubimage(0, yStart, this.request.x, yStop - yStart), "png", this.sock.getOutputStream());
		sock.close();
	}
}