package liteprotocol;

import java.util.Collection;

public interface Server {
	
	public boolean sendState(Recipient r, LightState state);
	
	public boolean sendToggles(Recipient r, Collection<Toggle> c);
	
	public boolean addServerListener(ServerListener l);
}
