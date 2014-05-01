package liteprotocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;

import liteprotocol.interfaces.Server;

public class ServerCommunicator extends Server {
	private static final int Recieve_Port = 10356;
	private ControlReceiveThread ctlReceiveThread;
	private BroadcastMessageThread bcastThread;
	private int id;
	private int group;
	protected final ServerCommunicator server = this;

	public ServerCommunicator(int id, int group) {
		startThreads();
		this.id = id;
		this.group = group;
	}

	public void startThreads() {
		if(ctlReceiveThread == null) {
			ctlReceiveThread = new ControlReceiveThread();
			ctlReceiveThread.start();
		}
		if(bcastThread == null) {
			bcastThread = new BroadcastMessageThread();
			bcastThread.start();
		}
	}

	public void stopThreads() {
		ctlReceiveThread.stopRecieving();
		ctlReceiveThread = null;
		bcastThread.stopTransmitting();
		bcastThread = null;
	}

	private class BroadcastMessageThread extends Thread {
		private MulticastSocket broadcastSocket;
		private boolean broadcast = true;

		public void run() {
			try {
				InetAddress mcGroup = InetAddress.getByName("224.0.0.1");
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

	private class ControlReceiveThread extends Thread {
		private boolean recieve = true;
		private ServerSocket serverSocket;

		public void run() {
			try{
				serverSocket = new ServerSocket(Recieve_Port);
				while(recieve) {
					try {
						Socket connection = serverSocket.accept();
						byte[] header = new byte[2];
						byte[] data = null;
						byte[] byteBuffer = new byte[1024];
						int read = 0;
						DataInputStream in = new DataInputStream(connection.getInputStream());
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						do {
							read = in.read(byteBuffer, 0, byteBuffer.length);
							if(read > 0)
								buffer.write(byteBuffer, 0, read);
						} while(read != byteBuffer.length);
						data = buffer.toByteArray();
						if(data.length > 2) {
							header[0] = data[0];
							header[1] = data[1];
							if(data.length > 2) {
								byte[] temp = new byte[data.length - 2];
								System.arraycopy(data, 0, temp, 0, temp.length);
								data = temp;
							}
							else 
								data = null;
							Recipient r = new Recipient(connection, header);
							this.parse(r, data);
						}
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

		private void parse(Recipient r, byte[] data) {
			switch(r.getHeader()[0]) {
			case (byte)0x00: // getState
				server.notifyRequestForColor(r);
			break;
			case (byte)0x02: // get light toggles
				server.notifyRequestToggles(r, false);
			break;
			case (byte)0x03: // get group toggles
				server.notifyRequestToggles(r, true);
			case (byte)0x80: // set group
				if(data.length != 4)
					break;
			server.notifySetGroup(ByteBuffer.allocate(4).put(data).getInt());
			break;
			case (byte)0x82: // set color
				if(data.length != 4)
					break;
			server.notifySetColor(LiteColor.deserialize(data));
			break;
			case (byte)0x84: // set light toggles
				server.notifySetToggles(this.extractToggles(data), false);
			break;
			case (byte)0x85: // set group toggles
				server.notifySetToggles(this.extractToggles(data), true);
			break;
			case (byte)0x86: // set enabled toggles
				server.notifySetEnabledToggles(LightBoolean.deserialize(data[0]).getValue());
			break;
			default:
				break;
			}
			if(!r.getConnection().isClosed()){
				try {
					r.getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private Collection<Toggle> extractToggles(byte[] data) {
			Collection<Toggle> ret = new LinkedList<Toggle>();
			int read = 0;
			ByteArrayInputStream buffer = new ByteArrayInputStream(data);
			byte[] toggleBuffer = new byte[25];
			do {
				read = buffer.read(toggleBuffer, 0, toggleBuffer.length);
				if(read == toggleBuffer.length)
					ret.add(Toggle.derserialize(toggleBuffer));
			} while(read != toggleBuffer.length);
				return ret;
		}
	}

	@Override
	public void sendColor(Recipient r, LiteColor color) {
		try {
			Socket socket = r.getConnection();
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.write(r.getHeader());
			out.write(color.serialize());
			out.close();
		} catch (Exception e) {
			
		}
	}

	@Override
	public void sendToggles(Recipient r, Collection<Toggle> c) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			for(Toggle t : c) {
				buffer.write(t.serialize());
			}
			DataOutputStream out = new DataOutputStream(r.getConnection().getOutputStream());
			byte[] header = r.getHeader();
			header[1] = (byte)c.size();
			out.write(header);
			out.write(buffer.toByteArray());
			out.close();
		} catch (Exception e) {
			
		}
	}

	@Override
	public void sendReply(Recipient r) {
		try {
			DataOutputStream out = new DataOutputStream(r.getConnection().getOutputStream());
			out.write(r.getHeader());
			out.close();
		} catch(Exception e) {
			
		}
	}

	@Override
	public boolean setGroupId(int groupId) {
		this.group = groupId;
		return true;
	}
}
