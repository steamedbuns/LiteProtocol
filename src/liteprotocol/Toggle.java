package liteprotocol;

import java.util.Date;


public class Toggle {
	private int id;
	private Date on;
	private Date off;
	private byte frequency;
	private LiteColor color;
	
	/**
	 * @param time
	 * @param frequency
	 * @param red
	 * @param green
	 * @param blue
	 * @param type
	 */
	public Toggle(int id, Date on, Date off, byte frequency, LiteColor color) {
		this.id = id;
		this.on = on;
		this.off = off;
		this.frequency = frequency;
		this.color = color;
	}
	
	public byte[] serialize() {
		byte[] ret = new byte[25];
		ByteBuffer buffer = new ByteBuffer(25);
	}
	
	public Toggle derserialize(byte[] values) {
		if(values.length != 25)
			return null;
		
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the on
	 */
	public Date getOn() {
		return on;
	}

	/**
	 * @return the off
	 */
	public Date getOff() {
		return off;
	}

	/**
	 * @return the frequency
	 */
	public byte getFrequency() {
		return frequency;
	}

	/**
	 * @return the color
	 */
	public LiteColor getColor() {
		return color;
	}
	
}
