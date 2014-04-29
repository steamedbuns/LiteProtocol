/**
 * 
 */
package liteprotocol;

/**
 * @author Carl
 *
 */
public class Boolean {
	private boolean value;
	
	public Boolean(boolean value) {
		this.value = value;
	}
	
	public byte serialize() {
		if(value)
			return (byte)255;
		else 
			return 0;
	}
	
	public static Boolean deserialize(byte value) {
		return new Boolean((value != (byte)0) ? true : false);
	}

	/**
	 * @return the value
	 */
	public boolean getValue() {
		return value;
	}
}
