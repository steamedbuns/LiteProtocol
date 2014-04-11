/**
 * 
 */
package liteprotocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * @author Carl
 *
 */
public class Broadcast {

	public static DatagramPacket createDatagramPacket(int id, int group, short port, InetAddress address) {
		byte[] packet = new byte[10];
		for(int i = 0; i < 12; i++) {
			if(i < 4)
				packet[i] = ByteBuffer.allocate(4).putInt(id).array()[i];
			else if(i >= 4 && i < 8)
				packet[i] = ByteBuffer.allocate(4).putInt(group).array()[i];
			else if(i >= 8 && i < 10)
				packet[i] = ByteBuffer.allocate(2).putShort(port).array()[i];
		}
		return new DatagramPacket(packet, packet.length, address, 10270);
	}
	
	public Broadcast(DatagramPacket packet) {
		
	}
}
