package liteprotocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import liteprotocol.interfaces.Client;

public class ClientCommunicator implements Client {
	private static final long timeout = 5000;
	private HashMap<Integer, Multicast> lightMap;
	private BroadcastListenThread packetListenThread;
	private SweepThread sweepThread;
	private Object mapSyncObject;

	public ClientCommunicator() {
		lightMap = new HashMap<Integer, Multicast>();
		mapSyncObject = new Object();
	}

	public Collection<Integer> getAllLightIds() {
		synchronized(this.mapSyncObject) {
			if(lightMap.size() == 0) {
				return null;
			}
			return lightMap.keySet();
		}
	}

	public Collection<Integer> getAllGroupIds() {
		synchronized(this.mapSyncObject) {
			if(lightMap.size() == 0) {
				return null;
			}
			HashSet<Integer> groupIds = new HashSet<Integer>();
			for(Multicast m : lightMap.values()) {
				if(m.getGroup() != 0)
					groupIds.add(Integer.valueOf(m.getGroup()));
			}
			return groupIds;
		}
	}

	public void startThreads() {
		if(packetListenThread == null) {
			packetListenThread = new BroadcastListenThread();
		}
		if(sweepThread == null) {
			sweepThread = new SweepThread();
		}
		packetListenThread.start();
		sweepThread.start();
	}

	public void stopThreads() {
		packetListenThread.stopThread();
		sweepThread.stopSweep();
		packetListenThread = null;
		sweepThread = null;
	}

	private void packetReceived(Multicast packet) {
		synchronized(this.mapSyncObject) {
			lightMap.put(Integer.valueOf(packet.getId()), packet);
		}
	}

	private void sweep() {
		synchronized(this.mapSyncObject) {
			for(Integer i : lightMap.keySet()) {
				if((System.currentTimeMillis() - lightMap.get(i).getTime()) > timeout)
					lightMap.remove(i);
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
				listener.joinGroup( InetAddress.getByName("224.0.0.1"));
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

	public LightState getLightState(int id) {
		Multicast broadcast = null;
		synchronized(this.mapSyncObject) {
			broadcast = this.lightMap.get(Integer.valueOf(id));
		}	
		if(broadcast == null)
			return null;
		try {
			byte[] header = {0x00, 0x01};
			byte[] reply = transmitRequest(broadcast, header, null);
			byte[] data = new byte[reply.length - 2];
			System.arraycopy(reply, 2, data, 0, data.length);
			return new LightState(broadcast.getId(), broadcast.getGroup(), LiteColor.deserialize(data));
		}
		catch (Exception e) {
			return null;
		}
	}

	public GroupState getGroup(int id) {
		if(id == 0)
			return null;
		Set<Integer> lights = new HashSet<Integer>();
		synchronized(this.mapSyncObject) {
			for(Multicast m : this.lightMap.values()) {
				if(m.getGroup() == id) {
					lights.add(m.getId());
				}
			}
		}
		try {
			if(lights.isEmpty())
				return null;
			LightState state = getLightState(lights.iterator().next().intValue());
			if(state == null)
				return null;
			return new GroupState(id, lights, state.getColor());
		} catch (Exception e) {		
			return null;
		}
	}

	public Collection<Toggle> getGroupToggles(int id) {
		Multicast destination = null;
		synchronized(this.mapSyncObject) {
			for(Multicast m : this.lightMap.values()) {
				if(m.getGroup() == id) {
					destination = m;
					break;
				}
			}
		}
		if(destination == null)
			return null;
		try {
			List<Toggle> toggleCollection = new LinkedList<Toggle>();
			byte[] header = {(byte)0x03, 0x00 };
			byte[] ret = this.transmitRequest(destination, header, null);
			if(ret == null || ret.length <= 2)
				return null;
			ByteArrayInputStream buffer = new ByteArrayInputStream(ret);
			byte[] toggleBuffer = new byte[25];
			int read = 0;
			if((read = buffer.read(toggleBuffer, 0, 2)) != 2)
				return null;
			do {
				if((read = buffer.read(toggleBuffer, 0, toggleBuffer.length)) == toggleBuffer.length)
					toggleCollection.add(Toggle.derserialize(toggleBuffer));				
			} while(read == toggleBuffer.length);
			return toggleCollection;
		} catch(Exception e) {
			return null;
		}
	}

	public Collection<Toggle> getLightToggles(int id) {
		Multicast destination = null;
		synchronized(this.mapSyncObject) {
			destination = this.lightMap.get(Integer.valueOf(id));
		}
		if(destination == null)
			return null;
		try {
			List<Toggle> toggleCollection = new LinkedList<Toggle>();
			byte[] header = {(byte)0x02, 0x00 };
			byte[] ret = this.transmitRequest(destination, header, null);
			if(ret == null || ret.length <= 2)
				return null;
			ByteArrayInputStream buffer = new ByteArrayInputStream(ret);
			byte[] toggleBuffer = new byte[25];
			int read = 0;
			if((read = buffer.read(toggleBuffer, 0, 2)) != 2)
				return null;
			do {
				if((read = buffer.read(toggleBuffer, 0, toggleBuffer.length)) == toggleBuffer.length)
					toggleCollection.add(Toggle.derserialize(toggleBuffer));				
			} while(read == toggleBuffer.length);
			return toggleCollection;
		} catch(Exception e) {
			return null;
		}
	}

	public boolean setLightGroup(int lightId, int groupId) {
		Multicast address = null;
		synchronized(this.mapSyncObject) {
			address = this.lightMap.get(Integer.valueOf(lightId));
		}
		try {
			if(address == null)
				return false;
			byte[] header = { (byte)0x80, 0x00 };
			byte[] data = ByteBuffer.allocate(4).putInt(groupId).array();
			byte[] ret = transmitRequest(address, header, data);
			if(ret.length == 2)
				return ret[0] == header[0] && ret[1] == header[1];
			else 
				return false;			
		} catch(Exception e) {
			return false;
		}
	}

	public boolean setLightColor(int lightId, LiteColor color) {
		Multicast address = null;
		synchronized(this.mapSyncObject) {
			address = this.lightMap.get(Integer.valueOf(lightId));
		}
		try {
			if(address == null)
				return false;
			byte[] header = { (byte)0x82, 0x00 };
			byte[] data = color.serialize();
			byte[] ret = transmitRequest(address, header, data);
			if(ret.length == 2)
				return ret[0] == header[0] && ret[1] == header[1];
			else 
				return false;	
		} catch (Exception e) {
			return false;
		}
	}

	public boolean setGroupColor(int groupId, LiteColor color) {
		LinkedList<Multicast> groupIds = new LinkedList<Multicast>();
		synchronized(this.mapSyncObject) {
			for(Multicast m : this.lightMap.values()) {
				if(m.getGroup() == groupId)
					groupIds.add(m);
			}
		}
		try {
			if(groupIds.size() <= 0)
				return false;
			byte[] header = { (byte)0x82, 0x00 };
			byte[] data = color.serialize();
			for(Multicast address : groupIds) {
				try {
					transmitRequest(address, header, data);
				} catch (Exception e) {

				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean setLightToggles(int lightId, Collection<Toggle> toggle) {
		Multicast address = null;
		synchronized(this.mapSyncObject) {
			address = this.lightMap.get(Integer.valueOf(lightId));
		}
		try {
			if(address == null)
				return false;
			byte[] header = {(byte)0x84, (byte)toggle.size()};
			byte[] data;
			ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
			Iterator<Toggle> it = toggle.iterator();
			while(it.hasNext()) {
				dataBuffer.write(it.next().serialize());
			}
			data = dataBuffer.toByteArray();
			byte[] ret = this.transmitRequest(address, header, data);
			if(ret.length != 2)
				return false;
			else
				return ret[0] == header[0] && ret[1] == header[1];
		} catch (Exception e) {
			return false;
		}		
	}

	public boolean setGroupToggles(int groupId, Collection<Toggle> toggle) {
		List<Multicast> groupAddresses = new LinkedList<Multicast>();
		synchronized(this.mapSyncObject) {
			for(Multicast m : this.lightMap.values()) {
				if(m.getGroup() == groupId)
					groupAddresses.add(m);
			}
		}
		try {
			if(groupAddresses.size() <= 0)
				return false;
			byte[] header = {(byte)0x85, (byte)toggle.size()};
			byte[] data;
			ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
			Iterator<Toggle> it = toggle.iterator();
			while(it.hasNext()) {
				dataBuffer.write(it.next().serialize());
			}
			data = dataBuffer.toByteArray();

			for(Multicast m : groupAddresses){
				try {
					this.transmitRequest(m, header, data);
				} catch (Exception e) {

				}
			}
			return true;
		}
		catch (Exception e){
			return false;
		}
	}

	public boolean removeAllGroupToggles(int groupId) {
		return setGroupToggles(groupId, new LinkedList<Toggle>());
	}

	public boolean setLightEnableToggles(int lightId, boolean enabled) {
		Multicast address = null;
		synchronized(this.mapSyncObject) {
			address = this.lightMap.get(Integer.valueOf(lightId));
		}
		try {
			if(address == null)
				return false;
			byte[] header = { (byte)0x86, 0x00 };
			byte[] data = new byte[1];
			data[0] = (new LightBoolean(enabled)).serialize();
			byte[] ret = transmitRequest(address, header, data);
			if(ret.length == 2)
				return ret[0] == header[0] && ret[1] == header[1];
			else 
				return false;	
		} catch (Exception e) {
			return false;
		}
	}

	public boolean setGroupEnableToggles(int groupId, boolean enabled) {
		LinkedList<Multicast> groupIds = new LinkedList<Multicast>();
		synchronized(this.mapSyncObject) {
			for(Multicast m : this.lightMap.values()) {
				if(m.getGroup() == groupId)
					groupIds.add(m);
			}
		}
		try {
			if(groupIds.size() <= 0)
				return false;
			byte[] header = { (byte)0x87, 0x00 };
			byte[] data = new byte[1];
			data[0] = (new LightBoolean(enabled)).serialize();
			for(Multicast address : groupIds) {
				try {
					transmitRequest(address, header, data);
				} catch (Exception e) {

				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private byte[] transmitRequest(Multicast destination, byte[] header, byte[] data) throws IOException {
		Socket socket = new Socket(destination.getAddress(), destination.getPort());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		DataInputStream in = new DataInputStream(socket.getInputStream());
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		outBuffer.write(header);
		if(data != null)
			outBuffer.write(data);
		out.write(outBuffer.toByteArray(), 0, outBuffer.size());

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] bufferedData = new byte[1024];
		int read = 0;
		do {
			read = in.read(bufferedData, 0, bufferedData.length);
			if(read > 0)
				buffer.write(bufferedData, 0, read);
		} while(read == bufferedData.length);

		in.close();
		out.close();
		socket.close();

		return buffer.toByteArray();
	}
}
