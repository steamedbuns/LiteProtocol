package liteprotocol;

import java.util.*;
import java.io.IOException;
import java.net.*;

public class Communicator {

	private int id;
	private int group;
	private short port;
	private List<BroadcastListener> broadcastListeners;
	private List<ControlListener> controlListeners;
	private Object broadcastSyncObject;
	private Object controlSyncObject;
	private BroadcastListenThread broadcastListenThread;
	private BroadcastMessageThread broadcastMessageThread;
	
	public Communicator(int id) {
		this.id = id;
		this.group = 0;
		this.port = 0;
		this.broadcastListeners = new LinkedList<BroadcastListener>();
		this.controlListeners = new LinkedList<ControlListener>();
		this.broadcastSyncObject = new Object();
		this.controlSyncObject = new Object();
				
		this.broadcastListenThread = new BroadcastListenThread();
		this.broadcastListenThread.start();
		
		this.broadcastMessageThread = new BroadcastMessageThread();
		this.broadcastMessageThread.start();
	}
	
	public void close() {
		this.broadcastListenThread.stopListening();
		this.broadcastMessageThread.stopTransmitting();
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
				// TODO Auto-generated catch block
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
	
	
}
