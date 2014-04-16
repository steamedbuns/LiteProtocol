package liteprotocol;

import java.util.EventListener;

public interface MulticastListener extends EventListener {

	void broadcastRecived(Multicast b);
}
