package daten;

public interface DatenInterface<T> {
	public String getID();
	public String getData();
	public int compareTo(T t);
	public boolean equals(Object obj);
}
