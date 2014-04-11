package liteprotocol;

import java.util.EventListener;

public interface BroadcastListener extends EventListener {

	void broadcastRecived(Broadcast b);
}
