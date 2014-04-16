/**
 * 
 */
package liteprotocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Carl/Qian
 *
 */
public class Broadcast {

	public final static int BROADCAST_PORT = 6789;
	
	private DatagramPacket data;
	private InetAddress address;
	private int id;
	private int group;
	private int port;
	private long timestamp;
	
	public static DatagramPacket createDatagramPacket(int id, int group, short port, InetAddress address) {
		byte[] packet = new byte[10];
		
		for(int i = 0; i < 12; i++) {
			if(i < 4)
				packet[i] = ByteBuffer.allocate(4).putInt(id).array()[i];
			else if(i >= 4 && i < 8)
				packet[i] = ByteBuffer.allocate(4).putInt(group).array()[i - 4];
			else if(i >= 8 && i < 10)
				packet[i] = ByteBuffer.allocate(2).putShort(port).array()[i - 8];
		}
		return new DatagramPacket(packet, packet.length, address, BROADCAST_PORT);
	}
	
	public Broadcast(DatagramPacket packet) {
		this.data = packet;
		byte[] data = packet.getData();
		if(data.length == 10) {
			this.address = packet.getAddress();
			this.id = getId(data);
			this.group = getGroup(data);
			this.port = getPort(data);
			this.timestamp = System.currentTimeMillis();
		}
	}
	
	private int getId(byte[] data) {
		byte[] buffer = new byte[4];
		for(int i = 0; i < 4; i++)
			buffer[i] = data[i];
		ByteBuffer value = ByteBuffer.wrap(buffer);
		return value.getInt();
	}
	
	private int getGroup(byte[] data) {
		byte[] buffer = new byte[4];
		for(int i = 0; i < 4; i++)
			buffer[i] = data[i + 4];
		ByteBuffer value = ByteBuffer.wrap(buffer);
		return value.getInt();
	}
	
	private int getPort(byte[] data) {
		byte[] buffer = new byte[4];
		buffer[0] = 0;
		buffer[1] = 0;
		for(int i = 2; i < 4; i++)
			buffer[i] = data[i + 6];
		ByteBuffer value = ByteBuffer.wrap(buffer);
		return value.getInt();
	}

	/**
	 * @return the address
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	public String toString() {
		SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
		Date d = new Date(timestamp);
		return "Time: " + date.format(d) + "\n[ID: " + id + ", Group: " + group + ", Port: " + this.port + ", IP: " + address.getHostAddress() + "]";
	}
	
	public DatagramPacket getData() {
		return this.data;
	}
	
	public boolean equals(Object o) {
		Broadcast other = (Broadcast) o;
		return this.id == other.id;
		
	}
	
	public long getTime() {
		return this.timestamp;
	}
}
