package liteprotocol.interfaces;

import java.util.Collection;

import liteprotocol.LiteColor;
import liteprotocol.Recipient;
import liteprotocol.Toggle;

public interface ServerListener {
	
	public void setGroup(int groupId);
	
	public void setColor(LiteColor color);
	
	public void setToggles(Collection<Toggle> toggle);
	
	public void setEnabledToggles(boolean enabled);
	
	public void requestForState(Recipient r);
	
	public void requestToggles(Recipient r);

}
