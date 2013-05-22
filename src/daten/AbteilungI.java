package daten;

/**
 * Das Interface der Abteilungen definiert uns unsere Bezeichnungen für die
 * Strings. Sollten sich Abteilungen ändern, so müssen diese hier nur
 * hinzugefügt werden (einmal als String und einmal ins Array).
 * 
 * @author oettel
 * 
 */
public interface AbteilungI {
	static final String ROHBAU = "Rohbau";
	static final String PRESSWERK = "Presswerk";
	static final String WERKZEUG = "Werkzeugbau";
	static final String KONSTRUKTION = "Konstruktion";
	static final String QUALI = "Qualität";
	static final String VORBEREITUNG = "Arbeitsvorbereitung";
	static final String ENTWICKLUNG = "Serienentwicklung";
	static final String EINKAUF = "Einkauf";
	static final String LOGIS = "Logistik";
	static final String CHEF = "Geschäftsführung/KVP";
	static final String FINANZEN = "Finanzbuchhaltung";
	static final String EDV = "EDV";
	static final String PERSONAL = "Personal";
	static final String ORGANISATION = "Organisation";
	static final String UNBEKANNT = "Unbekannt";
	static final String[] ABTEILUNGEN = { ROHBAU, PRESSWERK, WERKZEUG,
			KONSTRUKTION, QUALI, VORBEREITUNG, ENTWICKLUNG, EINKAUF, LOGIS,
			CHEF, FINANZEN, EDV, PERSONAL, ORGANISATION };
	static final String[] ABTEILUNGIDS = { "210", "211", "212", "240", "241",
			"242", "244", "245", "246", "247", "248", "249", "251", "252",
			"253", "254", "261", "262", "271", "272", "273", "275", "277",
			"279" };
}
