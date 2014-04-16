package liteprotocol;

import java.util.*;
import java.io.IOException;
import java.net.*;

public class Communicator {
	
	private static final int Recieve_Port = 10356;
	private int id;
	private int group;
	private DatagramSocket listener;
	private DatagramSocket broadcastSocket;
	private ServerSocket serverSocket;
	private List<BroadcastListener> broadcastListeners;
	private List<ControlListener> controlListeners;
	private Object broadcastSyncObject;
	private Object controlSyncObject;
	private BroadcastListenThread broadcastListenThread;
	private BroadcastMessageThread broadcastMessageThread;
	private ControlReciveThread controlReciveThread;
	
	public Communicator(int id) {
		this.id = id;
		this.group = 0;
		this.broadcastListeners = new LinkedList<BroadcastListener>();
		this.controlListeners = new LinkedList<ControlListener>();
		this.broadcastSyncObject = new Object();
		this.controlSyncObject = new Object();
		
		try {
			listener = new DatagramSocket(Broadcast.BROADCAST_PORT);
			broadcastSocket = new DatagramSocket();
			serverSocket = new ServerSocket(Recieve_Port);
			serverSocket.setSoTimeout(30000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(listener != null && broadcastSocket != null && serverSocket != null) {
			this.broadcastListenThread = new BroadcastListenThread();
			this.broadcastListenThread.start();
			
			if(id >= 0) {		
				this.controlReciveThread = new ControlReciveThread();
				this.controlReciveThread.start();
				
				this.broadcastMessageThread = new BroadcastMessageThread();
				this.broadcastMessageThread.start();
			}
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
				
				while(listen) {
					DatagramPacket packet = new DatagramPacket(reciveData, reciveData.length);
					listener.receive(packet);
					notifyBroadcastRecived(new Broadcast(packet));
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
				List<InterfaceAddress> addrList = new ArrayList<InterfaceAddress>();
				Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces();
				Iterator<NetworkInterface> iter = Collections.list(net).iterator();
				while(iter.hasNext()) {
					NetworkInterface next = iter.next();
					if(!next.isLoopback() && !next.isPointToPoint() && next.isUp() && !next.isVirtual() && !next.getDisplayName().contains("Virtual")) {
						Iterator<InterfaceAddress> it = next.getInterfaceAddresses().iterator();
						while(it.hasNext()) {
							addrList.add(it.next());
						}
					}
				}
				while(broadcast) {
					try{
						Iterator<InterfaceAddress> it = addrList.iterator();
						while(it.hasNext()) {
							InterfaceAddress addr = it.next();
							if(addr.getBroadcast() != null && addr.getBroadcast().getAddress()[0] != 0 
														   && addr.getBroadcast().getAddress()[1] != 0 
														   && addr.getBroadcast().getAddress()[2] != 0 
														   && addr.getBroadcast().getAddress()[3] != 0) {
								broadcastSocket.send(Broadcast.createDatagramPacket(id, group, (short)Recieve_Port, addr.getBroadcast()));
							}
						}
					} catch (NullPointerException e) {
						System.out.println("Something was null.");
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Thread.sleep(1000);
				}
			} catch (NullPointerException e) {
				System.out.println("Something was null.");
			} catch (SocketException e1) {
				System.out.println("Could not create socket.");
			} catch (InterruptedException e) {
				System.out.println("Thread interupted.");
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
			while(recieve) {
				try {
					Socket connection = serverSocket.accept();
					connection.close();
				} catch(SocketTimeoutException e) {
				} catch (IOException e) {
				} 
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
