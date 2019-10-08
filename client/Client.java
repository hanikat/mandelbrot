package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Utility.Request;
import client.RemoteServer;


/**
 * This class takes several parameters which defines the mandelbrot set to compute.
 * The work is distributed across one, or several, servers which are supplied as arguments during startup.
 * @author Marcus Hanikat, Hanikat@kth.se
 * @version 1.0 - 08/10/2019
 */
public class Client {
	
	public static void main(String[] args) {
	
		
		double min_c_re;
		double min_c_im;
		double max_c_re;
		double max_c_im;
		int max_n;
		int x;
		int y;
		int divisions;
		
		//Argument input length sanity check
		if (args.length < 9) {
			throw new java.lang.IllegalArgumentException("Too few arguments!");
		}
		

		//Sanity check of all numerical arguments, could be seperated to increase feedback to user
		try {
			min_c_re = Double.parseDouble(args[0]);
			min_c_im = Double.parseDouble(args[1]);
			max_c_re = Double.parseDouble(args[2]);
			max_c_im = Double.parseDouble(args[3]);
			max_n = Integer.parseInt(args[4]);
			x = Integer.parseInt(args[5]);
			y = Integer.parseInt(args[6]);
			divisions = Integer.parseInt(args[7]);
			
		} catch (NumberFormatException e) {
			throw new NumberFormatException("There was a problem parsing the supplied image parameters. Revise argumnets 1 through 8 and try again.");
		}
		
		
		//Sanity check and parsing of each passed server argument
		List<RemoteServer> servers = new ArrayList<>();
		for(int i = 8; i < args.length; i++) {
			
			//Check to see if a hostname/IP and port is supplied and separated by ":"
			if(args[i].contains(":")) {
				String[] strings = args[i].split(":");
				if(strings.length == 2 
				&& !strings[0].isEmpty()
				&& !strings[1].isEmpty()
				&& Integer.parseInt(strings[1]) > 0
				&& Integer.parseInt(strings[1]) <= 65535) {
					//Add server to list of available servers with a sentRequest count of 0
					servers.add(new RemoteServer(strings[0], Integer.parseInt(strings[1])));
				} else {
					throw new IllegalArgumentException("Argument " + Integer.toString(i) + " have an illegal format. Format should be: '<hostname>:<port>'");
				}
			} else {
				throw new IllegalArgumentException("Argument " + Integer.toString(i) + " have an illegal format. Format should be: '<hostname>:<port>'");
			}
		}

		//Create a new request with the supplied parameters
		Request req = new Request(min_c_re, min_c_im, max_c_re, max_c_im, max_n, x, y, 0, divisions);
		
		//Distribute the workload between the servers
		for(int j = 0; j < divisions; j++) {
			//Gets one server with lowest number of requests sent to
			RemoteServer lstReqSrv = Collections.min(servers, Comparator.comparingInt(s -> s.sentRequests));
			
			//Set the worker number for the server and reference to the created request
			lstReqSrv.worker = j;
			lstReqSrv.req = req;
			
			//Start the request by "forking" the execution with RemoteServer class
			Thread t1 = new Thread(lstReqSrv);
			t1.start();
			
			//Introduce a delay so that the threads don't overwrite the worker variable before a request is sent
			//Only necessary if more than one request is sent to the same server
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Sleep interrupted!");
			}
		}
		
		
	}
	
}

