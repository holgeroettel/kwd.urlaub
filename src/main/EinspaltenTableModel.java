package main;

import java.util.Vector;
import daten.DatenInterface;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Das KalenderModel ist das Abbild einer Tabelle, die wir uns anzeigen können.
 * Sie wurde jediglich ausgegliedert um den Kalender nicht zu groß werden zu
 * lassen. Außerdem ist es eine Einspaltige Tabelle, die in ihrem Verhalten
 * gleich mehrfach verwendet werden kann.
 * 
 * @author oettel
 * 
 */

@SuppressWarnings("unchecked")
public class EinspaltenTableModel<T> implements TableModel {
	private Vector<T> changes = new Vector<T>();
	private Vector<TableModelListener> listeners = new Vector<TableModelListener>();
	private String title;

	public EinspaltenTableModel(String s) {
		this.title = s;
	}

	public void addChange(T change) {
		int index = 0;
		for (T t : changes) {
			if (((DatenInterface<T>) t).compareTo(change) >= 0)
				break;
			index++;
		}
		changes.add(index, change);

		TableModelEvent e = new TableModelEvent(this, index, index,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

		// Nun das Event verschicken
		for (int i = 0, n = listeners.size(); i < n; i++) {
			(listeners.get(i)).tableChanged(e);
		}
	}

	public void removeChange(int index) {
		changes.remove(index);
		TableModelEvent e = new TableModelEvent(this, index, index,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);

		// Nun das Event verschicken
		for (int i = 0, n = listeners.size(); i < n; i++) {
			(listeners.get(i)).tableChanged(e);
		}
	}

	public void removeChange(T change) {
		int index = 0;
		for (T c : changes) {
			DatenInterface<T> help = (DatenInterface<T>) c;
			if (help.equals((DatenInterface<T>) change)) {
				break;
			}
			index++;
		}

		changes.remove(change);

		TableModelEvent e = new TableModelEvent(this, index, index,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);

		// Nun das Event verschicken
		for (int i = 0, n = listeners.size(); i < n; i++) {
			(listeners.get(i)).tableChanged(e);
		}

	}

	public void clear() {
		while (!changes.isEmpty())
			removeChange(0);
	}

	public Vector<T> getChange() {
		return changes;
	}

	@Override
	public int getRowCount() {
		return changes.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return title;
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public String getValueAt(int rowIndex, int columnIndex) {
		DatenInterface<T> u = (DatenInterface<T>) changes.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return u.getData();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

}
