package daten;

public class Person implements Comparable<Person>, DatenInterface<Person> {
	private String nr;
	private String name;
	private String vorname;
	private Integer posten, soll, rest;
	private String abteilung;
	private String gruppe;

	public Person(String nr, String name, String vorname, Integer posten, Integer soll, Integer rest,
			String abteilung, String gr) {
		this.nr = nr;
		this.name = name;
		this.vorname = vorname;
		this.posten = posten;
		this.soll = soll;
		this.rest = rest;
		this.abteilung = abteilung;
		this.gruppe = gr;
	}

	public String getNr() {
		return nr;
	}

	public String getName() {
		return name;
	}

	public String getVorname() {
		return vorname;
	}

	public Integer getPosten() {
		return posten;
	}
	
	public Integer getRest() {
		return rest;
	}
	
	public Integer getSoll() {
		return soll;
	}

	public String getAbteilung() {
		return abteilung;
	}

	public String getGruppe() {
		return gruppe;
	}

	public void setGruppe(String s) {
		gruppe = s;
	}

	@Override
	public String getID() {
		return getNr();
	}

	@Override
	public String getData() {
		return getName() + ", " + getVorname();
	}

	@Override
	public int compareTo(Person o) {
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
	    final Person other = (Person) obj;
	    if (getID().equals(other.getID()))
	        return true;
	    return false;
	}

}
