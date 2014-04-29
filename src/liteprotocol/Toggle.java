package liteprotocol;

import java.util.Date;


public class Toggle {
	private int id;
	private Date on;
	private Date off;
	private Frequency frequency;
	private LiteColor color;
	
	/**
	 * @param time
	 * @param frequency
	 * @param red
	 * @param green
	 * @param blue
	 * @param type
	 */
	public Toggle(int id, Date on, Date off, Frequency frequency, LiteColor color) {
		this.id = id;
		this.on = on;
		this.off = off;
		this.frequency = frequency;
		this.color = color;
	}
	
	public byte[] serialize() {
		byte[] ret = new byte[25];
		
		return null;
	}
	
	public static Toggle derserialize(byte[] values) {
		if(values.length != 25)
			return null;
		int id = 0;
		Date on = null;
		Date off = null;
		Frequency freq = null;
		LiteColor color = null;
		
		
		
		return new Toggle(id, on, off, freq, color);
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
	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * @return the color
	 */
	public LiteColor getColor() {
		return color;
	}
	
}
