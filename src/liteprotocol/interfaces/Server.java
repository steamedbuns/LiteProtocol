package liteprotocol.interfaces;

import java.util.Collection;

import liteprotocol.LightState;
import liteprotocol.Recipient;
import liteprotocol.Toggle;

public interface Server {
	
	public boolean sendState(Recipient r, LightState state);
	
	public boolean sendToggles(Recipient r, Collection<Toggle> c);
	
	public void addServerListener(ServerListener l);
}
