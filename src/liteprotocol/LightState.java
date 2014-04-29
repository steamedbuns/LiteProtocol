package liteprotocol;

public class LightState {

	private int lightId;
	private int groupId;
	private boolean on;
	private byte red;
	private byte green;
	private byte blue;
	
	/**
	 * @param lightId
	 * @param groupId
	 * @param on
	 * @param red
	 * @param green
	 * @param blue
	 */
	protected LightState(int lightId, int groupId, boolean on, byte red,
			byte green, byte blue) {
		this.lightId = lightId;
		this.groupId = groupId;
		this.on = on;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/**
	 * @return the lightId
	 */
	public int getLightId() {
		return lightId;
	}

	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * @return the on
	 */
	public boolean isOn() {
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
}
