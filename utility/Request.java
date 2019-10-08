package Utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * This class is used to hold information about a single request of a complete or sub-set of mandelbrot set.
 * @author Marcus Hanikat, Hanikat@kth.se
 * @version 1.0 - 08/10/2019
 */
public class Request {
	
	public double min_c_re;
	public double min_c_im;
	public double max_c_re;
	public double max_c_im;
	public int n;
	public int x;
	public int y;
	public int split = 1;
	public int workerNo = 0;
	public BufferedImage image;
	private int count = 0;
	
	/**
	 * Create a request which holds the parameters for executing the mandelbrot algorithm
	 * @param min_re, minimum c_re value
	 * @param min_im, minimum c_im value
	 * @param max_re, maximum c_re value
	 * @param max_im, maximum c_im value
	 * @param N, maximum number of iterations to complete before setting pixels in the mandelbrot algorithm
	 * @param X, the width of the requested image
	 * @param Y, the height of the requested image
	 * @param WorkerNo, the number of the current worker
	 * @param Split, number of workers to split the workload between
	 */
	public Request (double min_re, double min_im, double max_re, double max_im, int N, int X, int Y, int WorkerNo, int Split) {
		this.min_c_re = min_re;
		this.min_c_im = min_im;
		this.max_c_re = max_re;
		this.max_c_im = max_im;
		this.n = N;
		this.x = X;
		this.y = Y;
		this.workerNo = WorkerNo;
		this.split = Split;
		this.image = new BufferedImage(this.x, this.y, BufferedImage.TYPE_INT_RGB);
	}
	
	/**
	 * Create a request which holds the parameters for executing the mandelbrot algorithm on a single worker
	 * @param min_re, minimum c_re value
	 * @param min_im, minimum c_im value
	 * @param max_re, maximum c_re value
	 * @param max_im, maximum c_im value
	 * @param N, maximum number of iterations to complete before setting pixels in the mandelbrot algorithm
	 * @param X, the width of the requested image
	 * @param Y, the height of the requested image
	 */
	public Request (double min_re, double min_im, double max_re, double max_im, int N, int X, int Y) {
		this.min_c_re = min_re;
		this.min_c_im = min_im;
		this.max_c_re = max_re;
		this.max_c_im = max_im;
		this.n = N;
		this.x = X;
		this.y = Y;
		this.image = new BufferedImage(this.x, this.y, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * This function is called when a request is completed to write the image to a png-file
	 * The function only prints the image if all sub-requests have been completed.
	 */
	public void print() {
		//Increment the counter and write the image to a file if all threads have reported in
		count++;
		if(count >= split) {
			try {
				ImageIO.write(this.image, "png", new File("mandelbrot-client.png"));
			} catch (IOException e) {
				System.out.println("There was a problem writing the image: " + e.getMessage());
			}
			
		}
		
	}
	
}



