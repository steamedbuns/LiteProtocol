package liteprotocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import liteprotocol.interfaces.Client;

public class ClientCommunicator implements Client {
	private static final long timeout = 5000;
	private HashMap<Integer, Multicast> map;
	private BroadcastListenThread packetListenThread;
	private SweepThread sweepThread;
	private Object mapSyncObject;
	
	public ClientCommunicator() {
		map = new HashMap<Integer, Multicast>();
		startBroadcastListenThread();
		startSweepThread();
		mapSyncObject = new Object();
	}
	
	@Override
	public Collection<Integer> getAllLightIds() {
		HashSet<Integer> lightIds = new HashSet<Integer>();
		synchronized(this.mapSyncObject) {
			if(map.size() == 0) {
				return null;
			}
			Set<Entry<Integer, Multicast>> entry = map.entrySet();
			for(Entry<Integer,Multicast> e : entry) {
				lightIds.add(e.getKey());
			}
		}
		return lightIds;
	}

	@Override
	public Collection<Integer> getAllGroupIds() {
		HashSet<Integer> groupIds = new HashSet<Integer>();
		synchronized(this.mapSyncObject) {
			if(map.size() == 0) {
				return null;
			}
			Set<Entry<Integer, Multicast>> entry = map.entrySet();
			for(Entry<Integer,Multicast> e : entry) {
				groupIds.add(e.getValue().getGroup());
			}
		}
		return groupIds;
	}

	@Override
	public LightState getLightState(int id) {
		return null;
	}

	@Override
	public GroupState getGroup(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Toggle> getGroupToggles(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Toggle> getLightToggles(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setLightGroup(int lightId, int groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLightColor(int lightId, byte on, byte red, byte greeen,
			byte blue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGroupColor(int groupId, byte on, byte red, byte greeen,
			byte blue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addLightToggle(int lightId, Toggle toggle) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addGroupToggle(int groupId, Toggle toggle) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeLightToggle(int lightId, int toggleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeGroupToggle(int groupId, int toggleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLightEnableToggles(int lightId, boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGroupEnableToggles(int groupId, boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLightToggleEnabled(int lightId, int toggleId,
			boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGroupToggleEnabled(int groupId, int toggleId,
			boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void startBroadcastListenThread() {
		if(packetListenThread == null) {
			packetListenThread = new BroadcastListenThread();
			packetListenThread.start();
		}
	}
	
	public void startSweepThread() {
		if(sweepThread == null) {
			sweepThread = new SweepThread();
			sweepThread.start();
		}
	}
	
	public void stopBroadcastListenThread() {
		packetListenThread.stopThread();
		packetListenThread = null;
	}
	
	public void stopSweepThread() {
		sweepThread.stopSweep();
		sweepThread = null;
	}
	
	private void packetReceived(Multicast packet) {
		synchronized(this.mapSyncObject) {
			map.put(Integer.valueOf(packet.getId()), packet);
		}
	}
	
	private void sweep() {
		synchronized(this.mapSyncObject) {
			Set<Entry<Integer, Multicast>> entry = map.entrySet();
			for(Entry<Integer,Multicast> e : entry) {
				long currTime = System.currentTimeMillis();
				if((currTime - e.getValue().getTime()) > timeout) {
					map.remove(e.getKey());
				}
			}
		}
	}
	
	private class SweepThread extends Thread {
		private boolean sweep = true;
		
		public void run() {
			while(sweep) {
				sweep();
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
				}
			}
		}
		
		public void stopSweep() {
			this.sweep = false;
		}
	}
	
	private class BroadcastListenThread extends Thread {
		private MulticastSocket listener;
		private boolean listen = true;
		
		public void run() {
			try {
				listener = new MulticastSocket(Multicast.BROADCAST_PORT);
				byte reciveData[] = new byte[10];
				while(listen) {
					DatagramPacket packet = new DatagramPacket(reciveData, reciveData.length);
					listener.receive(packet);
					packetReceived(new Multicast(packet));
				}
			} catch (IOException e) {
			}
		}
		
		public void stopThread() {
			this.listen = false;
			listener.close();
		}
	}
}
