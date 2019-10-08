import java.util.Random;

import client.Client;
import host.Server;

public class Start {

	public static void main(String[] args) {
		
		Random random = new Random();
		
		String[] s1Args = {Integer.toString(random.nextInt(40000) + 10000)};
		Runnable server1 = new Runnable() {
			public void run() {
				Server.main(s1Args);
			}
		};
		Thread server1t = new Thread(server1);
		server1t.start();
		
		String[] s2Args = {Integer.toString(random.nextInt(40000) + 10000)};
		Runnable server2 = new Runnable() {
			public void run() {
				Server.main(s2Args);
			}
		};
		Thread server2t = new Thread(server2);
		server2t.start();
		
		String[] s3Args = {Integer.toString(random.nextInt(40000) + 10000)};
		Runnable server3 = new Runnable() {
			public void run() {
				Server.main(s3Args);
			}
		};
		Thread server3t = new Thread(server3);
		server3t.start();
		
		String[] clientArgs = {"-1", "-1.5", "2", "1.5", "1024", "10000", "10000", "4", "localhost:" + s1Args[0], "localhost:" + s2Args[0], "localhost:" + s3Args[0]};
		
		Runnable client = new Runnable() {
			public void run() {
				Client.main(clientArgs);
			}
		};
		Thread clientt = new Thread(client);
		clientt.start();
		
	}

}
