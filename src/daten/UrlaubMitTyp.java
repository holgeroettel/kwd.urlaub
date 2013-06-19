package daten;

public class UrlaubMitTyp extends Urlaub {

	public UrlaubMitTyp(Integer i, String p, String t, Integer s, int j,
			String typ) {
		super(i, p, t, s, j, typ);
	}

	public String getData() {
		String text;
		switch (getTyp()) {
		case TYPURLAUB:
			text = "Urlaub: ";
			break;
		case TYPSTUNDEN:
			text = "Gleit: ";
			break;
		case TYPSONDER:
			text = "Sonder: ";
			break;
		default:
			text = "";
		}
		String[] help = getSqlTag().split("-");
		return text + help[2] + "." + help[1] + "." + help[0];
	}

}
