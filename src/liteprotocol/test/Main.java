package liteprotocol.test;

import liteprotocol.Multicast;
import liteprotocol.MulticastListener;
import liteprotocol.Communicator;

public class Main {

	public static void main(String[] args) {
		if(args.length > 0){
			final int id = Integer.parseInt(args[0]);

			final Communicator comm = new Communicator(id);
	
			comm.addBroadcastListener(new MulticastListener() {
	
				public void broadcastRecived(Multicast b) {
					if(b.getId() != id)
						System.out.println("Received: " + b);
				}
			});
	
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					comm.close();
				}
			});
		}
		
		else {
			System.out.println("Please enter an id.");
			System.exit(0);
		}
	}
}