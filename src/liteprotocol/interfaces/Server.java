package liteprotocol.interfaces;

import java.util.Collection;
import java.util.LinkedList;
import liteprotocol.LiteColor;
import liteprotocol.Recipient;
import liteprotocol.Toggle;

public abstract class Server {
	
	private Collection<ServerListener> listerners;
	private Object listenerSyncObject;
	
	protected Server() {
		this.listerners = new LinkedList<ServerListener>();
		this.listenerSyncObject = new Object();
	}
	
	public abstract void sendColor(Recipient r, LiteColor color);
	
	public abstract void sendToggles(Recipient r, Collection<Toggle> c);
	
	public abstract void sendReply(Recipient r);
	
	public abstract boolean setGroupId(int groupId);
	
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
	
	public void notifySetToggles(Collection<Toggle> toggle, boolean group) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.setToggles(toggle, group);
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
	
	public void notifyRequestForColor(Recipient r) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.requestForColor(r);
			}
		}
	}
	
	public void notifyRequestToggles(Recipient r, boolean group) {
		synchronized(this.listenerSyncObject) {
			for(ServerListener l : this.listerners){
				l.requestToggles(r, group);
			}
		}
	}
}
