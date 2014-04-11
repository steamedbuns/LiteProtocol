package liteprotocol.test;

import liteprotocol.Broadcast;
import liteprotocol.BroadcastListener;
import liteprotocol.Communicator;

public class Main {

	public static void main(String[] args) {
		int id = 0;
		if(args.length > 0)
			id = Integer.parseInt(args[0]);
		else {
			System.out.println("Please enter an id.");
			System.exit(0);
		}
		
		final Communicator comm = new Communicator(id);
		
		comm.addBroadcastListener(new BroadcastListener() {

			public void broadcastRecived(Broadcast b) {
				System.out.println(b);
			}
			
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				comm.close();
			}
		});
	}
}
