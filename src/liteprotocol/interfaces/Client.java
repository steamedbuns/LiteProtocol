package liteprotocol.interfaces;

import java.util.Collection;

import liteprotocol.GroupState;
import liteprotocol.LightState;
import liteprotocol.Toggle;


public interface Client {

	public Collection<Integer> getAllLightIds();
	
	public Collection<Integer> getAllGroupIds();
	
	public LightState getLightState(int id);
		
	public GroupState getGroup(int id);
	
	public Collection<Toggle> getGroupToggles(int id);
	
	public Collection<Toggle> getLightToggles(int id);
	
	public boolean setLightGroup(int lightId, int groupId);
	
	public boolean setLightColor(int lightId, byte on, byte red, byte greeen, byte blue);
	
	public boolean setGroupColor(int groupId, byte on, byte red, byte greeen, byte blue);
	
	public boolean setLightToggles(int lightId, Collection<Toggle> toggle);
	
	public boolean setGroupToggles(int groupId, Collection<Toggle> toggle);
	
	public boolean removeAllGroupToggles(int groupId, Collection<Integer> toggleIds);
	
	public boolean setLightEnableToggles(int lightId, boolean enabled);
	
	public boolean setGroupEnableToggles(int groupId, boolean enabled);
	
	public boolean setLightToggleEnabled(int lightId, int toggleId, boolean enabled);
	
	public boolean setGroupToggleEnabled(int groupId, int toggleId, boolean enabled);
}
