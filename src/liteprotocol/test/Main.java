package liteprotocol.test;

import liteprotocol.Broadcast;
import liteprotocol.BroadcastListener;
import liteprotocol.Communicator;

public class Main {

	public static void main(String[] args) {
		if(args.length > 0){
			final int id = Integer.parseInt(args[0]);

			final Communicator comm = new Communicator(id);
	
			comm.addBroadcastListener(new BroadcastListener() {
	
				public void broadcastRecived(Broadcast b) {
					if(b.getId() != id)
						System.out.println("Received: " + b);
				}

				@Override
				public void broadcastSent(Broadcast b) {
					// TODO Auto-generated method stub
					System.out.println("Sent: " + b);
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