# mandelbrot

A project designed to distirbute the computation of a mandelbrot set over a set of different servers. The computation will be done on different servers and the image will then be combined on a client. The communication between the client and servers is done through a TCP port. The image is a gray-scale color image with 256 different colors on a linear scale between black and white. The image can be divided in to a set number of parts which is evenly distributed across the servers. The combined image is then requested to be output from the client as a png-image through the Request class.

This project consists of three packages:

client:
This package contains the client used to distribute the workload between the different servers. The client creates a list of RequestServers from the supplied arguments, opens a TCP connection to these servers and then combines the response from all servers into a png-image

server:
This package contains the server which the client connects to. When each connection is recieved, the server creates a new thread to distribute the work to using the Worker class. The Worker class parses the recieved requests, computes the given part of the mandelbrot set and then sends this back to the client.

utility:
The utility package contains the Request class which is used to hold parameters for each request sent by the client and recieved by the server. It also contains the image which is assembled by the client and a function which writes the image to a png-file when all threads have recieved their parts of the image in the client class.


# notes
There are much that can be improved in this code. For example, the code could have been split into smaller methods within the classes, improved error handling, usage of HttpURLConnection class to send GET-requests instead of using socket and string parsing etc. 
