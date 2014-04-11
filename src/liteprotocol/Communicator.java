package liteprotocol;

import java.util.*;
import java.io.IOException;
import java.net.*;

public class Communicator {

	private int id;
	private List<BroadcastListener> broadcastListeners;
	private List<ControlListener> controlListeners;
	private Object broadcastSyncObject;
	private Object controlSyncObject;
	private BroadcastListenThread broadcastListenThread;
	
	public Communicator(int id) {
		this.id = id;
		this.broadcastListeners = new LinkedList<BroadcastListener>();
		this.controlListeners = new LinkedList<ControlListener>();
		this.broadcastSyncObject = new Object();
		this.controlSyncObject = new Object();
				
		broadcastListenThread = new BroadcastListenThread();
		broadcastListenThread.start();
	}
	
	public void close() {
		broadcastListenThread.stopListening();
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
				InetAddress sendAddress;
				InetAddress localHost = Inet4Address.getLocalHost();
				NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
				
				

			
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public void stopTransmitting() {
			this.broadcast = false;
		}
	}
	
	
}
