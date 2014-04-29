package liteprotocol;

import java.util.Date;


public class Toggle {

	private Date time;
	private int frequency;
	private byte red;
	private byte green;
	private byte blue;
	private byte type;
	
	/**
	 * @param time
	 * @param frequency
	 * @param red
	 * @param green
	 * @param blue
	 * @param type
	 */
	public Toggle(Date time, int frequency, byte red, byte green, byte blue, byte type) {
		this.time = time;
		this.frequency = frequency;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.type = type;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @return the red
	 */
	public byte getRed() {
		return red;
	}

	/**
	 * @return the green
	 */
	public byte getGreen() {
		return green;
	}

	/**
	 * @return the blue
	 */
	public byte getBlue() {
		return blue;
	}

	/**
	 * @return the type
	 */
	public byte getType() {
		return type;
	}
}
