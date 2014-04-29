package liteprotocol;

import java.util.Collection;

public class GroupState {

	private int groupID;
	private Collection<Integer> lightIds;
	private boolean on;
	private byte red;
	private byte green;
	private byte blue;

	/**
	 * @param groupID
	 * @param lightIds
	 * @param on
	 * @param red
	 * @param green
	 * @param blue
	 */
	public GroupState(int groupID, Collection<Integer> lightIds, boolean on,
			byte red, byte green, byte blue) {
		this.groupID = groupID;
		this.lightIds = lightIds;
		this.on = on;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	/**
	 * @return the groupID
	 */
	public int getGroupID() {
		return groupID;
	}

	/**
	 * @return the lightIds
	 */
	public Collection<Integer> getLightIds() {
		return lightIds;
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
