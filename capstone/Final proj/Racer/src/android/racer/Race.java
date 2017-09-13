package android.racer;

import java.io.Serializable;

public class Race implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7699513696291043459L;
	private String name;
	private Player[] players;
	private String defaultOrient;
	
	public static final String ORIENT_LANDSCAPE = "Landscape";
	public static final String ORIENT_PORTRAIT = "Portrait";
	
	public Race(int numPlayers) {
		name = "Race1";
		defaultOrient = ORIENT_LANDSCAPE;
		players = new Player[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			players[i] = Player.Null;
		}
	}
	
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Player[] getPlayers() {
		return players;
	}

	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public String getDefaultOrient() {
		return defaultOrient;
	}

	public void setDefaultOrient(String defaultOrient) {
		this.defaultOrient = defaultOrient;
	}
}
