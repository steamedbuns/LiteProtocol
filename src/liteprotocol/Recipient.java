package liteprotocol;

import java.net.Socket;

public class Recipient {
	private Socket connection;
	private byte[] header;

	/**
	 * @param connection
	 * @param header
	 */
	protected Recipient(Socket connection, byte[] header) {
		this.connection = connection;
		this.header = header;
	}
	
	/**
	 * @return the connection
	 */
	protected Socket getConnection() {
		return connection;
	}

	/**
	 * @return the header
	 */
	protected byte[] getHeader() {
		return header;
	}
}
