package daten;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Die Abteilung ist unsere zentrale Klasse, die alle wesentlichen Merkmale der
 * Abteilungen abbildet. Zu beachten ist, dass es immer nur einen Subchef geben
 * kann, aber mehrere gleichberechtigte Chefs. Die wichtigsten Chefs sind die
 * der Geschäftsführung. Dies muss beachtet werden, da die Geschäftsführung
 * ihrerseits eine vollständige Abteilung ist, aber allen Anderen gegenüber
 * besondere Rechte besitzt.
 * 
 * @author oettel
 * 
 */
public class Abteilung implements Comparable<Abteilung>, AbteilungI {
	private String bez;
	private ArrayList<String> chef;
	private String subChef;
	private ArrayList<String> angestellte;
	private String bezID;
	private ArrayList<String> freieID;
	private int letzteID;

	public Abteilung(String s) {
		this.bez = s;
		this.chef = new ArrayList<String>();
		this.subChef = null;
		this.angestellte = new ArrayList<String>();
		this.bezID = getAbteilungIDFromString(s);
		this.freieID = new ArrayList<String>();
		this.letzteID = 1;
	}

	/**
	 * Diese Funktion ordnet die verschiedenen Bereiche einer bestimmten
	 * vorhandenen Abteilung zu.
	 * 
	 * @param Bereichsnummer
	 * @return Abteilungsnummer
	 */
	public static String getBezeichnung(String nr) {
		switch (nr.substring(0, 3)) {
		case "210":
			return Abteilung.ROHBAU;
		case "211":
			return Abteilung.ROHBAU;
		case "212":
			return Abteilung.ROHBAU;
		case "240":
			return Abteilung.PRESSWERK;
		case "241":
			return Abteilung.PRESSWERK;
		case "242":
			return Abteilung.PRESSWERK;
		case "244":
			return Abteilung.PRESSWERK;
		case "247":
			return Abteilung.PRESSWERK;
		case "249":
			return Abteilung.PRESSWERK;
		case "245":
			return Abteilung.WERKZEUG;
		case "246":
			return Abteilung.WERKZEUG;
		case "248":
			return Abteilung.WERKZEUG;
		case "251":
			return Abteilung.KONSTRUKTION;
		case "252":
			return Abteilung.QUALI;
		case "253":
			return Abteilung.VORBEREITUNG;
		case "254":
			return Abteilung.ENTWICKLUNG;
		case "261":
			return Abteilung.EINKAUF;
		case "262":
			return Abteilung.LOGIS;
		case "271":
			return Abteilung.CHEF;
		case "272":
			return Abteilung.FINANZEN;
		case "273":
			return Abteilung.EDV;
		case "275":
			return Abteilung.PERSONAL;
		case "277":
			return Abteilung.PERSONAL;
		case "279":
			return Abteilung.ORGANISATION;
		default:
			// TODO loggen eines unbekannten Bereichs
			System.out.println("Ein unbekannter Bereich wurde gefunden: " + nr);
			return Abteilung.UNBEKANNT;
		}
	}

	public static String getAbteilungIDFromString(String s) {
		switch (s) {
		case Abteilung.ROHBAU:
			return "21000";
		case Abteilung.PRESSWERK:
			return "24000";
		case Abteilung.WERKZEUG:
			return "24500";
		case Abteilung.KONSTRUKTION:
			return "25100";
		case Abteilung.QUALI:
			return "25200";
		case Abteilung.VORBEREITUNG:
			return "25300";
		case Abteilung.ENTWICKLUNG:
			return "25400";
		case Abteilung.EINKAUF:
			return "26100";
		case Abteilung.LOGIS:
			return "26200";
		case Abteilung.CHEF:
			return "27100";
		case Abteilung.FINANZEN:
			return "27200";
		case Abteilung.EDV:
			return "27300";
		case Abteilung.PERSONAL:
			return "27500";
		case Abteilung.ORGANISATION:
			return "27900";
		default:
			// TODO loggen eines unbekannten Bereichs
			System.out.println("Ein unbekannter Bereich wurde genutzt: " + s);
			return "00000";
		}
	}
	
	/**
	 * Diese Funktion ordnet die verschiedenen Bereiche einer bestimmten
	 * vorhandenen Abteilung zu.
	 * 
	 * @param Bereichsnummer
	 * @return Abteilungsnummer
	 */
	public static String[] getArrayAbteilungen(String bez) {
		switch (bez) {			
		case Abteilung.ROHBAU:
			return new String[]{"210", "211", "212"};
		case Abteilung.PRESSWERK:
			return new String[]{"240","241","242","244","247","249"};
		case Abteilung.WERKZEUG:
			return new String[]{"245","246","248"};
		case Abteilung.KONSTRUKTION:
			return new String[]{"251"};
		case Abteilung.QUALI:
			return new String[]{"252"};
		case Abteilung.VORBEREITUNG:
			return new String[]{"253"};
		case Abteilung.ENTWICKLUNG:
			return new String[]{"254"};
		case Abteilung.EINKAUF:
			return new String[]{"261"};
		case Abteilung.LOGIS:
			return new String[]{"262"};
		case Abteilung.CHEF:
			return new String[]{"271"};
		case Abteilung.FINANZEN:
			return new String[]{"272"};
		case Abteilung.EDV:
			return new String[]{"273"};
		case Abteilung.PERSONAL:
			return new String[]{"275", "277"};
		case Abteilung.ORGANISATION:
			return new String[]{"279"};
		default:
			// TODO loggen eines unbekannten Bereichs
			System.out.println("Ein unbekannter Bereich wurde genutzt: " + bez);
			return new String[]{"000"};
		}
	}

	public String getBez() {
		return this.bez;
	}

	public ArrayList<String> getChefs() {
		return this.chef;
	}

	public String getSubChef() {
		return this.subChef;
	}

	public void setSubChef(String s) {
		this.subChef = s;
	}

	public ArrayList<String> getAngestellte() {
		return this.angestellte;
	}

	public String getBezID() {
		return this.bezID;
	}

	public ArrayList<String> getFreieID() {
		return this.freieID;
	}

	public int getLetzteID() {
		return this.letzteID;
	}

	public void setLetzteID(int i) {
		this.letzteID = i;
	}

	public void incrementLetzteID() {
		this.letzteID++;
	}

	public Set<String> getAlleDerAbteilung() {
		Set<String> result = new TreeSet<String>();
		if (!chef.isEmpty())
			result.addAll(chef);
		if (subChef != null)
			result.add(subChef);
		if (!angestellte.isEmpty())
			result.addAll(angestellte);
		return result;
	}

	/**
	 * @return erste freie ID für der Abteilung
	 */
	public String getFreeID() {
		String result;
		if (this.freieID.isEmpty()) {
			result = Integer.toHexString(letzteID);
			if (result.length() < 2)
				result = this.bezID.substring(0, 3) + "0" + result;
			else
				result = this.bezID.substring(0, 3) + result;
			this.letzteID++;
		} else {
			result = this.freieID.get(0);
			this.freieID.remove(0);
		}
		return result;
	}

	@Override
	public int compareTo(Abteilung o) {
		return this.getBez().compareTo(o.getBez());
	}

	@Override
	public String toString() {
		String result = "Abteilung " + bez + "\nMeine Chefs: ";
		for (String s : chef) {
			result = result + s + "; ";
		}
		result = result + "\nMein Stellvertreter: " + subChef
				+ "\nMeine Mitarbeiter: ";
		for (String s : angestellte) {
			result = result + s + "; ";
		}
		result = result + "\nMeine momentan freien ID's: ";
		for (String s : freieID) {
			result = result + s + "; ";
		}
		return result;
	}
}
