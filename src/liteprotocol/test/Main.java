package liteprotocol.test;

import liteprotocol.ServerCommunicator;

public class Main {

	public static void main(String[] args) {
		if(args.length > 0){
			final int id = Integer.parseInt(args[0]);

			final ServerCommunicator comm = new ServerCommunicator(id, 0);

	
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					comm.stopThreads();
				}
			});
		}
		
		else {
			System.out.println("Please enter an id.");
			System.exit(0);
		}
	}
}