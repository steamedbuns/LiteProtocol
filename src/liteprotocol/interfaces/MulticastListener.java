package liteprotocol.interfaces;

import java.util.EventListener;

import liteprotocol.Multicast;

public interface MulticastListener extends EventListener {

	void multicastReceived(Multicast b);
}
