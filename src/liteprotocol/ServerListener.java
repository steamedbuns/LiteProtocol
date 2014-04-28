package liteprotocol;

public interface ServerListener {
	
	public boolean setGroup(int groupId);
	
	public boolean setColor(byte on, byte red, byte green, byte blue);
	
	public boolean addToggle(Toggle toggle);
	
	public boolean removeToggle(int toggleId);
	
	public boolean setEnableToggles(boolean enabled);
	
	public boolean setToggleEnabled(int toggleId, boolean enabled);
	
	public void requestForState(Recipient r);
	
	public void requestToggles(Recipient r);

}
