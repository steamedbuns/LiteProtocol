/**
 * 
 */
package liteprotocol;

/**
 * @author Carl
 *
 */
public class LiteColor {
	private LightBoolean on;
	private byte red;
	private byte green;
	private byte blue;
	
	/**
	 * @param on
	 * @param red
	 * @param green
	 * @param blue
	 */
	public LiteColor(LightBoolean on, byte red, byte green, byte blue) {
		this.on = on;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public byte[] serialize() {
		byte[] ret = new byte[4];
		ret[0] = on.serialize();
		ret[1] = red;
		ret[2] = green;
		ret[3] = blue;
		return ret;
	}
	
	public static LiteColor deserialize(byte[] value) {
		if(value.length != 4)
			return null;
		return new LiteColor(LightBoolean.deserialize(value[0]), value[1], value[2], value[3]);
	}

	/**
	 * @return the on
	 */
	public LightBoolean getOn() {
		return on;
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
	
	public String toString() {
		return "[Color : On = " + this.on.getValue() + " r = " + (this.red & 0xFF) + " g = " + (this.green & 0xFF) + " b = " + (this.blue & 0xFF) + "]";
	}
}
