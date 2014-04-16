package liteprotocol;

import java.util.*;
import java.io.IOException;
import java.net.*;

public class Communicator{
	
	private static final int Recieve_Port = 10356;
	private InetAddress mcGroup;
	private int id;
	private int group;
	private MulticastSocket listener;
	private MulticastSocket broadcastSocket;
	private ServerSocket serverSocket;
	private List<MulticastListener> broadcastListeners;
	private List<ControlListener> controlListeners;
	private Object broadcastSyncObject;
	private Object controlSyncObject;
	private BroadcastListenThread broadcastListenThread;
	private BroadcastMessageThread broadcastMessageThread;
	private ControlReciveThread controlReciveThread;
	
	public Communicator(int id) {
		this.id = id;
		this.group = 0;
		this.broadcastListeners = new LinkedList<MulticastListener>();
		this.controlListeners = new LinkedList<ControlListener>();
		this.broadcastSyncObject = new Object();
		this.controlSyncObject = new Object();
		try {
			mcGroup = InetAddress.getByName("224.0.0.1");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.broadcastListenThread = new BroadcastListenThread();
		this.broadcastListenThread.start();
		if(id >= 0) {		
			this.controlReciveThread = new ControlReciveThread();
			this.controlReciveThread.start();
			
			this.broadcastMessageThread = new BroadcastMessageThread();
			this.broadcastMessageThread.start();
		}
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
	
	public synchronized void addBroadcastListener(MulticastListener l) {
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
	
	private void notifyBroadcastRecived(Multicast b) {
		synchronized(this.broadcastSyncObject) {
			for(MulticastListener bl : this.broadcastListeners)
				bl.multicastReceived(b);
		}
	}
	
	private class BroadcastListenThread extends Thread {
		
		private boolean listen = true;
		
		public void run() {
			try {
				listener = new MulticastSocket(Multicast.BROADCAST_PORT);
				listener.joinGroup(mcGroup);
				byte reciveData[] = new byte[10];
				while(listen) {
					DatagramPacket packet = new DatagramPacket(reciveData, reciveData.length);
					listener.receive(packet);
					notifyBroadcastRecived(new Multicast(packet));
				}
			} catch (IOException e) {
				System.out.println("Socket closed.");
			}
		}
		
		public void stopListening() {
			this.listen = false;
			try {
				listener.leaveGroup(mcGroup);
			} catch (IOException e) {
				System.out.println("Could not leave group.");
			}
			listener.close();
		}
	}
	
	private class BroadcastMessageThread extends Thread {
		
		private boolean broadcast = true;
		
		public void run() {
			try {
				broadcastSocket = new MulticastSocket(Multicast.BROADCAST_PORT);
				broadcastSocket.joinGroup(mcGroup);
				while(broadcast) {
					try{
						broadcastSocket.send(Multicast.createDatagramPacket(id, group, (short) Recieve_Port, mcGroup));
						Thread.sleep(1000);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						System.out.println("Thread interupted.");
					} 
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}
		
		public void stopTransmitting() {
			this.broadcast = false;
			try {
				broadcastSocket.leaveGroup(mcGroup);
			} catch (IOException e) {
				System.out.println("Could not leave group.");
			}
			broadcastSocket.close();
		}
	}
	
	private class ControlReciveThread extends Thread {
		private boolean recieve = true;
		
		public void run() {
			try{
				serverSocket = new ServerSocket(Recieve_Port);
				serverSocket.setSoTimeout(30000);
				while(recieve) {
					try {
						Socket connection = serverSocket.accept();
						connection.close();
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
			}
		}
	}
}
