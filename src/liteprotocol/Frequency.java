package liteprotocol;

public enum Frequency {
	Daily((byte)0),
	Weekly((byte)1),
	Monthly((byte)2),
	Yearly((byte)3);

	private byte value;

	private Frequency(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public static Frequency valueOf(byte value) {
		switch(value) {
		default:
		case 0:
			return Frequency.Daily;
		case 1:
			return Frequency.Weekly;
		case 2:
			return Frequency.Monthly;
		case 3:
			return Frequency.Yearly;
		}
	}
	
	public String toString() {
		return "Frequency : Value = " + Byte.toString(this.value) + "]";
	}
}
