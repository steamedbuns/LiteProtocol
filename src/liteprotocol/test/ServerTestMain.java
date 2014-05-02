package liteprotocol.test;

import java.util.Collection;

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

			@Override
			public void setGroup(int groupId) {
				server.setGroupId(groupId);
			}

			@Override
			public void setColor(LiteColor color) {
				this.color = color;
				System.out.println("Set Color: r = " + (this.color.getRed() & 0xFF) + " g = " + (this.color.getGreen() & 0xFF) + " b = " + (this.color.getBlue() & 0xFF));
			}

			@Override
			public void setToggles(Collection<Toggle> toggle, boolean group) {
								
			}

			@Override
			public void setEnabledToggles(boolean enabled) {
				
			}

			@Override
			public void requestForColor(Recipient r) {
				System.out.println("Sent Color: r = " + (this.color.getRed() & 0xFF) + " g = " + (this.color.getGreen() & 0xFF) + " b = " + (this.color.getBlue() & 0xFF));
				server.sendColor(r, this.color);				
			}

			@Override
			public void requestToggles(Recipient r, boolean group) {
				// TODO Auto-generated method stub
				
			}
			
		});
		server.startThreads();
	}

}
