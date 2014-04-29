package liteprotocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
	private BroadcastMessageThread bcastThread;
	private int id;
	private int group;
	
	public ServerCommunicator(int id, int group) {
		this.publish = new ArrayList<ServerListener>();
		startThreads();
		this.id = id;
		this.group = group;
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
	
	public void startThreads() {
		if(ctlReceiveThread == null) {
			ctlReceiveThread = new ControlReceiveThread();
			ctlReceiveThread.start();
		}
		if(bcastThread == null) {
			bcastThread = new BroadcastMessageThread();
			bcastThread.start();
		}
	}
	
	public void stopThreads() {
		ctlReceiveThread.stopRecieving();
		ctlReceiveThread = null;
		bcastThread.stopTransmitting();
		bcastThread = null;
	}
	
	private class BroadcastMessageThread extends Thread {
		private MulticastSocket broadcastSocket;
		private boolean broadcast = true;
		
		public void run() {
			try {
				InetAddress mcGroup = InetAddress.getByName("224.0.0.1");
				broadcastSocket = new MulticastSocket(Multicast.BROADCAST_PORT);
				broadcastSocket.setTimeToLive(30);
				broadcastSocket.setBroadcast(true);
				while(broadcast) {
					try{
						broadcastSocket.send(Multicast.createDatagramPacket(id, group, (short) Recieve_Port, mcGroup));
						Thread.sleep(1000);
					} catch (IOException e) {
					} catch (InterruptedException e) {
					} 
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}
		
		public void stopTransmitting() {
			this.broadcast = false;
			broadcastSocket.close();
		}
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
