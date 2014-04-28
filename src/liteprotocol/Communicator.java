package liteprotocol;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
	
	public void sendMessage(String message, InetAddress addr, int port) {
		ControlSendThread t = new ControlSendThread(message, addr, port);
		t.start();
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
			
		}
		System.out.println("Done");
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
				byte reciveData[] = new byte[10];
				while(listen) {
					DatagramPacket packet = new DatagramPacket(reciveData, reciveData.length);
					listener.receive(packet);
					notifyBroadcastRecived(new Multicast(packet));
				}
			} catch (IOException e) {
			}
		}
		
		public void stopListening() {
			this.listen = false;
			listener.close();
		}
	}
	
	private class BroadcastMessageThread extends Thread {
		
		private boolean broadcast = true;
		
		public void run() {
			try {
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
	
	private class ControlReciveThread extends Thread {
		private boolean recieve = true;
		
		public void run() {
			try{
				serverSocket = new ServerSocket(Recieve_Port);
				while(recieve) {
					try {
						Socket connection = serverSocket.accept();
						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						reader.close();
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
				System.out.println("Error while trying to close Server socket.");
			}
		}
	}
	
	private class ControlSendThread extends Thread {
		private String message;
		private InetAddress addr;
		private int port;
		private Socket out;
		public ControlSendThread(String message, InetAddress addr, int port) {
			this.addr = addr;
			this.port = port;
			this.message = message;
		}
		
		public void run() {
			try {
				out = new Socket(addr, port);
				out.setKeepAlive(false);
				PrintWriter o = new PrintWriter(out.getOutputStream());
				o.println(message);
				o.close();
				out.close();
			} catch (IOException e) {
				System.out.println("Something went wrong.");
			}
		}
	}
}
