package liteprotocol.interfaces;

import java.util.Collection;
import java.util.Set;

import liteprotocol.GroupState;
import liteprotocol.LightState;
import liteprotocol.LiteColor;
import liteprotocol.Toggle;


public interface Client {

	public Set<Integer> getAllLightIds();
	
	public Set<Integer> getAllGroupIds();
	
	public LightState getLightState(int id);
		
	public GroupState getGroup(int id);
	
	public Collection<Toggle> getGroupToggles(int id);
	
	public Collection<Toggle> getLightToggles(int id);
	
	public boolean setLightGroup(int lightId, int groupId);
	
	public boolean setLightColor(int lightId, LiteColor color);
	
	public boolean setGroupColor(int groupId, LiteColor color);
	
	public boolean setLightToggles(int lightId, Collection<Toggle> toggle);
	
	public boolean setGroupToggles(int groupId, Collection<Toggle> toggle);
	
	public boolean removeAllGroupToggles(int groupId);
	
	public boolean setLightEnableToggles(int lightId, boolean enabled);
	
	public boolean setGroupEnableToggles(int groupId, boolean enabled);
}
