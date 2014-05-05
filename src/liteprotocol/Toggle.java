package liteprotocol;

import java.nio.ByteBuffer;
import java.util.Date;


public class Toggle {
	private int id;
	private Date on;
	private Date off;
	private Frequency frequency;
	private LiteColor color;
	
	/**
	 * @param time
	 * @param frequency
	 * @param red
	 * @param green
	 * @param blue
	 * @param type
	 */
	public Toggle(int id, Date on, Date off, Frequency frequency, LiteColor color) {
		this.id = id;
		this.on = on;
		this.off = off;
		this.frequency = frequency;
		this.color = color;
	}
	
	public byte[] serialize() {
		return ByteBuffer.allocate(25)
				.putInt(id)
				.putLong(on.getTime())
				.putLong(off.getTime())
				.put(this.frequency.getValue())
				.put(color.serialize())
				.array();
	}
	
	public static Toggle derserialize(byte[] values) {
		if(values.length != 25)
			return null;
		ByteBuffer buffer = ByteBuffer.allocate(25);
		buffer.put(values);
		buffer.position(21);
		byte[] colors = new byte[4];
		buffer.get(colors);
		buffer.position(0);		
		return new Toggle(buffer.getInt(), new Date(buffer.getLong()), new Date(buffer.getLong()), Frequency.valueOf(buffer.get()), LiteColor.deserialize(colors));
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the on
	 */
	public Date getOn() {
		return on;
	}

	/**
	 * @return the off
	 */
	public Date getOff() {
		return off;
	}

	/**
	 * @return the frequency
	 */
	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * @return the color
	 */
	public LiteColor getColor() {
		return color;
	}
	
	public String toString() {
		return "[Toggle : Toggle ID = " + id + " On Date = " + on.toString() + " Off Date = " + off.toString() + " " + frequency.toString() + " " + color + "]"; 
	}
}
