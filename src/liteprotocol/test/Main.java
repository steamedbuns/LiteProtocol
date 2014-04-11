package liteprotocol.test;

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
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				comm.close();
			}
		});
	}
}
