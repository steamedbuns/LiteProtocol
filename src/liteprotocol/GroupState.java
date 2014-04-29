package liteprotocol;

import java.util.Collection;

public class GroupState {

	private int groupID;
	private Collection<Integer> lightIds;
	private LiteColor color;

	/**
	 * @param groupID
	 * @param lightIds
	 * @param on
	 * @param red
	 * @param green
	 * @param blue
	 */
	public GroupState(int groupID, Collection<Integer> lightIds, LiteColor color) {
		this.groupID = groupID;
		this.lightIds = lightIds;
		this.color = color;
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
	public LiteColor getColor() {
		return color;
	}
}
