package liteprotocol.interfaces;

import java.util.Collection;
import java.util.LinkedList;
import liteprotocol.LiteColor;
import liteprotocol.Recipient;
import liteprotocol.Toggle;

public abstract class Server extends Thread {
	
	private Collection<ServerListener> listerners;
	private Object listenerSyncObject;
	
	protected Server() {
		this.listerners = new LinkedList<ServerListener>();
		this.listenerSyncObject = new Object();
	}
	
	public abstract boolean sendColor(Recipient r, LiteColor color);
	
	public abstract boolean sendToggles(Recipient r, Collection<Toggle> c);
	
	public abstract boolean sendReply(Recipient r);
	
	public boolean addServerListener(ServerListener l) {
		synchronized(this.listenerSyncObject) {
			return this.listerners.add(l);
		}
	}
	
	public boolean removeServerListener(Object l) {
		synchronized(this.listenerSyncObject) {
			return this.listerners.remove(l);
		}
	}
	
	public void notifySetGroup(int groupId) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.setGroup(groupId);
			}
		}
	}
	
	public void notifySetColor(LiteColor color) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.setColor(color);
			}
		}
	}
	
	public void notifySetToggles(Collection<Toggle> toggle) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.setToggles(toggle);
			}
		}
	}
	
	public void notifySetEnabledToggles(boolean enabled) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.setEnabledToggles(enabled);
			}
		}
	}
	
	public void notifyRequestForState(Recipient r) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.requestForState(r);
			}
		}
	}
	
	public void notifyRequestToggles(Recipient r) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.requestToggles(r);
			}
		}
	}
}
