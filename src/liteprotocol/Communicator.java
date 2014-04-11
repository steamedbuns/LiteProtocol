package liteprotocol;

import java.util.*;
import java.io.IOException;
import java.net.*;

public class Communicator {

	private boolean portSet;
	private int id;
	private int group;
	private int port;
	private List<BroadcastListener> broadcastListeners;
	private List<ControlListener> controlListeners;
	private Object broadcastSyncObject;
	private Object controlSyncObject;
	private BroadcastListenThread broadcastListenThread;
	private BroadcastMessageThread broadcastMessageThread;
	private ControlReciveThread controlReciveThread;
	
	public Communicator(int id) {
		this.portSet = false;
		this.id = id;
		this.group = 0;
		this.broadcastListeners = new LinkedList<BroadcastListener>();
		this.controlListeners = new LinkedList<ControlListener>();
		this.broadcastSyncObject = new Object();
		this.controlSyncObject = new Object();
		
		this.controlReciveThread = new ControlReciveThread();
		this.controlReciveThread.start();
		
		while(!portSet);
		
		this.broadcastListenThread = new BroadcastListenThread();
		this.broadcastListenThread.start();
		
		this.broadcastMessageThread = new BroadcastMessageThread();
		this.broadcastMessageThread.start();
	}
	
	public void close() {
		this.controlReciveThread.stopRecieving();
		this.broadcastListenThread.stopListening();
		this.broadcastMessageThread.stopTransmitting();
		

		try {
			this.controlReciveThread.join();
			this.broadcastListenThread.join();
			this.broadcastMessageThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void addBroadcastListener(BroadcastListener l) {
		synchronized(broadcastSyncObject) {
			this.broadcastListeners.add(l);
		}
	}
	
	public synchronized void removeBroadcastListener(Object l) {
		synchronized(broadcastSyncObject) {
			this.broadcastListeners.remove(l);
		}
	}
	
	public synchronized void addControlListener(ControlListener l) {
		synchronized(controlSyncObject) {
			this.controlListeners.add(l);
		}
	}
	
	public synchronized void removeControlListener(Object l) {
		synchronized(controlSyncObject) {
			this.controlListeners.remove(l);
		}
	}
	
	private void notifyBroadcastRecived(Broadcast b) {
		synchronized(this.broadcastSyncObject) {
			for(BroadcastListener bl : this.broadcastListeners)
				bl.broadcastRecived(b);
		}
	}
	
	private void setPort(int port) {
		this.port = port;
		this.portSet = true;
	}
	
	private class BroadcastListenThread extends Thread {
		
		private boolean listen = true;
		
		public void run() {
			try {
				byte reciveData[] = new byte[10];
				DatagramSocket listener = new DatagramSocket(10270);
				while(listen) {
					DatagramPacket packet = new DatagramPacket(reciveData, reciveData.length);
					listener.receive(packet);
					notifyBroadcastRecived(new Broadcast(packet));
				}
				listener.close();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void stopListening() {
			this.listen = false;
		}
	}
	
	private class BroadcastMessageThread extends Thread {
		
		private boolean broadcast = true;
		
		public void run() {
			try {
				InetAddress localHost = Inet4Address.getLocalHost();
				NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
				
				DatagramSocket broadcastSocket = new DatagramSocket();
				
				while(broadcast) {
					for(InterfaceAddress address : networkInterface.getInterfaceAddresses())
						try {
							broadcastSocket.send(Broadcast.createDatagramPacket(id, group, port, address.getBroadcast()));
						} catch (IOException e) {
							e.printStackTrace();
						}
				}

				broadcastSocket.close();
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
		}
		
		public void stopTransmitting() {
			this.broadcast = false;
		}
	}
	
	private class ControlReciveThread extends Thread {
		
		private boolean recieve = true;
		
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(0);
				setPort(serverSocket.getLocalPort());
				serverSocket.setSoTimeout(30000);
				while(recieve) {
					Socket connection = serverSocket.accept();
					connection.close();
				}
				
				serverSocket.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void stopRecieving() {
			this.recieve = false;
		}
		
	}
	
}
