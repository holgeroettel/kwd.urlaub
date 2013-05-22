package daten;

public class Gruppe implements Comparable<Gruppe>, DatenInterface<Gruppe> {
	private String id;
	private String bez;

	public Gruppe(String i, String b) {
		this.id = i;
		this.bez = b;
	}

	public String getBez() {
		return bez;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public int compareTo(Gruppe o) {
		return getData().compareTo(o.getData());
	}

	@Override
	public String getData() {
		return bez;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Gruppe other = (Gruppe) obj;
		if (getID().equals(other.getID()))
			return true;
		return false;
	}

}
