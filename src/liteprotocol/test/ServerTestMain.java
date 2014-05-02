package liteprotocol.test;

import java.util.Collection;
import java.util.LinkedList;
import liteprotocol.LightBoolean;
import liteprotocol.LiteColor;
import liteprotocol.Recipient;
import liteprotocol.ServerCommunicator;
import liteprotocol.Toggle;
import liteprotocol.interfaces.ServerListener;

public class ServerTestMain {

	public static void main(String[] args) {
		final ServerCommunicator server = new ServerCommunicator(52, 0);
		server.addServerListener(new ServerListener() {
			private LiteColor color = new LiteColor(new LightBoolean(false), (byte)255, (byte)255, (byte)255);
			private Collection<Toggle> ltoggles = new LinkedList<Toggle>();
			private Collection<Toggle> gtoggles = new LinkedList<Toggle>();
			
			@Override
			public void setGroup(int groupId) {
				server.setGroupId(groupId);
				System.out.println("New group id : " + groupId);
			}

			@Override
			public void setColor(LiteColor color) {
				this.color = color;
				System.out.println("Received Color: On = " + this.color.getOn().getValue() + " r = " + (this.color.getRed() & 0xFF) + " g = " + (this.color.getGreen() & 0xFF) + " b = " + (this.color.getBlue() & 0xFF));
			}

			@Override
			public void setToggles(Collection<Toggle> toggle, boolean group) {
				if(group)
					this.gtoggles = toggle;
				else
					this.ltoggles = toggle;
			}

			@Override
			public void setEnabledToggles(boolean enabled) {
				
			}

			@Override
			public void requestForColor(Recipient r) {
				System.out.println("Sent Color: On = " + this.color.getOn().getValue() + " r = " + (this.color.getRed() & 0xFF) + " g = " + (this.color.getGreen() & 0xFF) + " b = " + (this.color.getBlue() & 0xFF));
				server.sendColor(r, this.color);				
			}

			@Override
			public void requestToggles(Recipient r, boolean group) {
				if(group)
					server.sendToggles(r, gtoggles);
				else
					server.sendToggles(r, ltoggles);
			}
			
		});
		server.startThreads();
		System.out.println("Server started");
	}

}
