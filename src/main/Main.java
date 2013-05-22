package main;

/**
 * Unsere Startklasse. Die Reihenfolge lautet: 1. Login 2. Datenbank befeuern 3.
 * Hauptmenü aufbauen
 * 
 * @author oettel
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		new Inhalt();
	}

}
