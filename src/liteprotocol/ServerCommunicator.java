package liteprotocol;

import java.util.Collection;

import liteprotocol.interfaces.Server;
import liteprotocol.interfaces.ServerListener;

public class ServerCommunicator implements Server {

	@Override
	public boolean sendState(Recipient r, LightState state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendToggles(Recipient r, Collection<Toggle> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addServerListener(ServerListener l) {
		// TODO Auto-generated method stub
		return false;
	}

}
