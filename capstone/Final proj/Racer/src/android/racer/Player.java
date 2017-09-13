package android.racer;

import java.io.Serializable;

public class Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Car car;
	
	private static Player nullPlayer = new Player();
	public static final Player Null = nullPlayer;
	
	private Player() {
		name = "Click join to play!";
		car = null;
	}
	
	public Player(String name) {
		this.name = name;
		car = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}
	
	@Override
	public String toString() {
		return name + ( car==null ? "" : " - " + car);
	}

}
