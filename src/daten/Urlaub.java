package daten;

public class Urlaub implements Comparable<Urlaub>, DatenInterface<Urlaub>{
	public static final String TYPURLAUB = "U";
	public static final String TYPSTUNDEN = "G";
	public static final String TYPSONDER = "S";
	public static final String URLAUB = "Urlaub";
	public static final String STUNDEN = "Gleitzeit";
	public static final String SONDER = "Sonderurlaub";
	private Integer id;
	private String person;
	private String sqlTag;
	private Integer status;
	private String typ;
	private int jahr;

	/**
	 * @param i
	 *            eine eindeutige ID
	 * @param p
	 *            eine Personennummer
	 * @param t
	 *            ein Kalendertag im yyyy-MM-dd Format
	 * @param s
	 *            ein Status
	 */
	/**
	 * @param i eine ID
	 * @param p die Personalnummer
	 * @param t der Kalendertag in yyyy-MM-dd
	 * @param s ein Status (1-beantragt, 2-genehmigt, 3-abgelehnt)
	 * @param j das Beantragungsjahr
	 * @param typ der Urlaubs
	 */
	public Urlaub(Integer i, String p, String t, Integer s, int j, String typ) {
		this.id = i;
		this.person = p;
		this.status = s;
		this.sqlTag = t;
		this.typ = typ;
		this.jahr = j;
	}

	public String getPerson() {
		return person;
	}

	public String getSqlTag() {
		return sqlTag;
	}

	/**
	 * @return 1-Beantragt 2-Abgelehnt 3-Genehmigt
	 */
	public Integer getStatus() {
		return status;
	}
	
	public int getJahr(){
		return jahr;
	}
	
	public String getTyp(){
		return typ;
	}

	@Override
	public String getID() {
		return id.toString();
	}

	@Override
	public String getData() {
		return sqlTag;
	}

	@Override
	public int compareTo(Urlaub o) {
		return getData().compareTo(o.getData());
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    final Urlaub other = (Urlaub) obj;
	    if (getID().equals(other.getID()))
	        return true;
	    return false;
	}

}
