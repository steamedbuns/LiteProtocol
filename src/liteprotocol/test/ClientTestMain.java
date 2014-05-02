package liteprotocol.test;

import java.util.ArrayList;
import java.util.Collection;

import liteprotocol.ClientCommunicator;
import liteprotocol.LightBoolean;
import liteprotocol.LightState;
import liteprotocol.LiteColor;

public class ClientTestMain {
	static ClientCommunicator client = new ClientCommunicator();
	public static void main(String[] args) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testLightState();
		client.stopThreads();
	}
	
	public static void testLightState() {
		System.out.println("Getting lightIds");
		Collection<Integer> lightIds = client.getAllLightIds();
		if(lightIds != null && lightIds.size() > 0) {
			for(Integer i : lightIds)
				System.out.println(i);
			
			
			ArrayList<LightState> lights = new ArrayList<LightState>();	
			System.out.println("Getting lightStates");
			for(Integer i : lightIds)
				lights.add(client.getLightState(i.intValue()));
			for(LightState l : lights)
				System.out.println("[Light Id=" + l.getLightId() + ", Group=" + l.getGroupId() + ", On=" + l.getColor().getOn().getValue() + 
						", Color=[Color r=" + (l.getColor().getRed() & 0xFF) + ", g=" + (l.getColor().getGreen() & 0xFF) + ", b=" + (l.getColor().getBlue() & 0xFF) + "]]" );
			
			System.out.println("Turning lights On");
			for(LightState l : lights) {
				System.out.println("Setting Color=[Color r=" + (l.getColor().getRed() & 0xFF) + ", g=" + (l.getColor().getGreen() & 0xFF) + ", b=" + (l.getColor().getBlue() & 0xFF) + "]]" );
				client.setLightColor(l.getLightId(), new LiteColor(new LightBoolean(true), l.getColor().getRed(), l.getColor().getGreen(), l.getColor().getBlue()));
			}
			
			lights = new ArrayList<LightState>();	
			System.out.println("Getting lightStates");
			for(Integer i : lightIds)
				lights.add(client.getLightState(i.intValue()));
			for(LightState l : lights)
				System.out.println("[Light Id=" + l.getLightId() + ", Group=" + l.getGroupId() + ", On=" + l.getColor().getOn().getValue() + 
						", Color=[Color r=" + (l.getColor().getRed() & 0xFF) + ", g=" + (l.getColor().getGreen() & 0xFF) + ", b=" + (l.getColor().getBlue() & 0xFF) + "]]" );
			
			System.out.println("Turning lights Off");
			for(LightState l : lights) {
				System.out.println("Setting Color=[Color r=" + (l.getColor().getRed() & 0xFF) + ", g=" + (l.getColor().getGreen() & 0xFF) + ", b=" + (l.getColor().getBlue() & 0xFF) + "]]" );
				client.setLightColor(l.getLightId(), new LiteColor(new LightBoolean(false), l.getColor().getRed(), l.getColor().getGreen(), l.getColor().getBlue()));
			}
			
			lights = new ArrayList<LightState>();	
			System.out.println("Getting lightStates");
			for(Integer i : lightIds)
				lights.add(client.getLightState(i.intValue()));
			for(LightState l : lights)
				System.out.println("[Light Id=" + l.getLightId() + ", Group=" + l.getGroupId() + ", On=" + l.getColor().getOn().getValue() + 
						", Color=[Color r=" + (l.getColor().getRed() & 0xFF) + ", g=" + (l.getColor().getGreen() & 0xFF) + ", b=" + (l.getColor().getBlue() & 0xFF) + "]]" );
		}
		else {
			System.out.println("No lightIds found");
		}
	}
}
