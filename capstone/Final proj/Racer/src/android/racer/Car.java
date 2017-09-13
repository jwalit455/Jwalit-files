package android.racer;

import java.io.Serializable;

public class Car implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private long UUID;
	
	public Car() {
		name = "Car";
		UUID = 0;
	}
	
	public Car(String UUID) {
		this.UUID = Long.parseLong(UUID);
		this.name = UUID; // default name
	}

	public Car(CharSequence text) {
		this(text.toString());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getUUID() {
		return UUID;
	}

	public void setUUID(long uUID) {
		UUID = uUID;
	}
	
	public String toString() {
		return name;
	}

}
