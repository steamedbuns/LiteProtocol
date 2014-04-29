package liteprotocol;

import java.util.Collection;

import liteprotocol.interfaces.Client;

public class ClientCommunicator implements Client {

	@Override
	public Collection<Integer> getAllLightIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Integer> getAllGroupIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LightState getLightState(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupState getGroup(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Toggle> getGroupToggles(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Toggle> getLightToggles(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setLightGroup(int lightId, int groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLightColor(int lightId, byte on, byte red, byte greeen,
			byte blue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGroupColor(int groupId, byte on, byte red, byte greeen,
			byte blue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addLightToggle(int lightId, Toggle toggle) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addGroupToggle(int groupId, Toggle toggle) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeLightToggle(int lightId, int toggleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeGroupToggle(int groupId, int toggleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLightEnableToggles(int lightId, boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGroupEnableToggles(int groupId, boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLightToggleEnabled(int lightId, int toggleId,
			boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGroupToggleEnabled(int groupId, int toggleId,
			boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}

}
