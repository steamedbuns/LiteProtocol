/**
 * 
 */
package liteprotocol;

/**
 * @author Carl
 *
 */
public class LightBoolean {
	private boolean value;
	
	public LightBoolean(boolean value) {
		this.value = value;
	}
	
	public byte serialize() {
		if(value)
			return (byte)255;
		else 
			return 0;
	}
	
	public static LightBoolean deserialize(byte value) {
		return new LightBoolean((value != (byte)0) ? true : false);
	}

	/**
	 * @return the value
	 */
	public boolean getValue() {
		return value;
	}
}
