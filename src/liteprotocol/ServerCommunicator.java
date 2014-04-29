package liteprotocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import liteprotocol.interfaces.Server;
import liteprotocol.interfaces.ServerListener;

public class ServerCommunicator extends Thread implements Server{
	private static final int Recieve_Port = 10356;
	private ArrayList<ServerListener> publish;
	private ControlReceiveThread ctlReceiveThread;
	
	public ServerCommunicator() {
		this.publish = new ArrayList<ServerListener>();
		startCtlThread();
	}
	
	@Override
	public boolean sendState(Recipient r, LightState state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendToggles(Recipient r, Collection<Toggle> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addServerListener(ServerListener l) {
		publish.add(l);
	}
	
	public void startCtlThread() {
		if(ctlReceiveThread == null) {
			ctlReceiveThread = new ControlReceiveThread();
			ctlReceiveThread.start();
		}
	}
	
	public void stopCtlThread() {
		ctlReceiveThread.stopRecieving();
		ctlReceiveThread = null;
	}
	
	private class ControlReceiveThread extends Thread {
		private boolean recieve = true;
		private ServerSocket serverSocket;
		
		public void run() {
			try{
				serverSocket = new ServerSocket(Recieve_Port);
				while(recieve) {
					try {
						Socket connection = serverSocket.accept();
						byte[] type = new byte[3];
						connection.getInputStream().read(type, 0, 3);
						connection.getInputStream().skip(1);
						String word = new String(type);
						if(word.equals("SET")) {
							
						}
						
					} catch(SocketTimeoutException e) {
						
					} catch (IOException e) {
						
					} 
				}
			} catch(IOException e) {
				
			}
		}
		
		public void stopRecieving() {
			this.recieve = false;
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.out.println("Error while trying to close Server socket.");
			}
		}
	}
}
