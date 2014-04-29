package liteprotocol;

public class LightState {

	private int lightId;
	private int groupId;
	private LiteColor color;
	
	/**
	 * @param lightId
	 * @param groupId
	 * @param on
	 * @param red
	 * @param green
	 * @param blue
	 */
	protected LightState(int lightId, int groupId, LiteColor color) {
		this.lightId = lightId;
		this.groupId = groupId;
		this.color = color;
	}

	/**
	 * @return the lightId
	 */
	public int getLightId() {
		return lightId;
	}
	
	public int getGroupId() {
		return groupId;
	}

	/**
	 * @return the color
	 */
	public LiteColor getColor() {
		return color;
	}
}
