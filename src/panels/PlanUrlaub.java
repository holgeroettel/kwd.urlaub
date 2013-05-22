package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import main.EinspaltenTableModel;
import main.Inhalt;
import daten.Person;
import daten.Urlaub;
import daten.UrlaubMitTyp;

@SuppressWarnings("serial")
public class PlanUrlaub extends JPanel {
	public final static String CARDNAME = "urlaub";

	private Inhalt inhalt;
	private JLabel lblMonth, lblYear, lblAnzahl;
	private JButton toggle;
	private String typ;
	private Map<String, btnCalendar> tblCalendar;
	private int currentYear, currentMonth, rest;
	private Font myFont;
	private EinspaltenTableModel<Urlaub> tableModel;
	private EinspaltenTableModel<UrlaubMitTyp> tableModelAll;

	public PlanUrlaub(Inhalt i) {
		super(null);
		this.inhalt = i;
		tableModel = new EinspaltenTableModel<Urlaub>("Änderungen");
		tableModelAll = new EinspaltenTableModel<UrlaubMitTyp>("Eingetragen");
		// Create controls
		lblMonth = new JLabel("Januar");
		lblMonth.setHorizontalAlignment(SwingConstants.CENTER);
		lblYear = new JLabel("");
		lblYear.setHorizontalAlignment(SwingConstants.CENTER);
		lblAnzahl = new JLabel("");
		toggle = new JButton("Urlaub");
		typ = Urlaub.TYPURLAUB;
		for (Urlaub u : inhalt.initPersonenUrlaub(inhalt.getPerson().getID())
				.values()) {
			UrlaubMitTyp urlaub = new UrlaubMitTyp(new Integer(u.getID()),
					u.getPerson(), u.getSqlTag(), u.getStatus(), u.getJahr(),
					u.getTyp());
			tableModelAll.addChange(urlaub);
		}
		myFont = new Font("", Font.PLAIN, 24);
		tblCalendar = initButtons();

		JLabel uberschrift = new JLabel("Urlaubsplan "
				+ inhalt.getDB().getJahr());
		JLabel nameNameLabel = new JLabel("Name:");
		Person p = inhalt.getPerson();
		JLabel nameLabel = new JLabel(p.getName() + ", " + p.getVorname());
		JLabel nummerNummerLabel = new JLabel("Personalnummer:");
		JLabel nummerLabel = new JLabel(p.getNr());

		updateLblAnzahl();

		JTable tabelle = new JTable(tableModel);
		tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabelle.setPreferredScrollableViewportSize(new Dimension(156, 314));
		JScrollPane tabellenPane = new JScrollPane(tabelle);
		tabellenPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabellenPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JTable tabelle1 = new JTable(tableModelAll);
		tabelle1.getColumnModel().getColumn(0)
				.setCellRenderer(new myCellRenderer());
		tabelle1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabelle1.setPreferredScrollableViewportSize(new Dimension(156, 314));
		JScrollPane tabellenPane1 = new JScrollPane(tabelle1);
		tabellenPane1
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabellenPane1
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JLabel lblRest = new JLabel("Resturlaub:");
		JButton commit = new JButton("Bestätigen");
		JButton btnPrev = new JButton("<<");
		JButton btnNext = new JButton(">>");
		JButton beenden = new JButton("Zurück");

		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (toggle.getText().equals("Urlaub")) {
					toggle.setText("Stunden");
					typ = Urlaub.TYPSTUNDEN;
				} else if (toggle.getText().equals("Stunden")) {
					toggle.setText("Sonderurlaub");
					typ = Urlaub.TYPSONDER;
				} else {
					toggle.setText("Urlaub");
					typ = Urlaub.TYPURLAUB;
				}
			}
		});
		// Register action listeners
		btnPrev.addActionListener(new btnPrev_Action());
		btnNext.addActionListener(new btnNext_Action());
		commit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyChanges();
			}
		});
		beenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inhalt.createHauptmenu();
			}
		});

		// Add controls to pane
		this.add(lblMonth);
		this.add(lblYear);
		this.add(lblRest);
		this.add(lblAnzahl);
		this.add(toggle);
		this.add(btnPrev);
		this.add(btnNext);
		this.add(uberschrift);
		this.add(nameNameLabel);
		this.add(nameLabel);
		this.add(nummerNummerLabel);
		this.add(nummerLabel);
		this.add(tabellenPane);
		this.add(tabellenPane1);
		this.add(commit);
		this.add(beenden);
		for (Entry<String, btnCalendar> b : tblCalendar.entrySet()) {
			this.add(b.getValue());
		}

		// Set bounds
		uberschrift.setBounds(250, 15, 300, 26);
		nameNameLabel.setBounds(250, 41, 150, 26);
		nameLabel.setBounds(400, 41, 150, 26);
		nummerNummerLabel.setBounds(250, 67, 150, 26);
		nummerLabel.setBounds(400, 67, 150, 26);
		lblRest.setBounds(250, 93, 150, 26);
		lblAnzahl.setBounds(400, 93, 150, 26);
		toggle.setBounds(550, 93, 150, 26);
		btnPrev.setBounds(250, 119, 75, 26);
		lblMonth.setBounds(325, 119, 75, 26);
		lblYear.setBounds(400, 119, 75, 26);
		btnNext.setBounds(475, 119, 75, 26);
		tabellenPane1.setBounds(63, 177, 150, 314);
		tabellenPane.setBounds(587, 177, 150, 314);
		commit.setBounds(250, 489, 150, 26);
		beenden.setBounds(400, 489, 150, 26);

		for (Entry<String, btnCalendar> b : this.tblCalendar.entrySet()) {
			btnCalendar btnHelp = b.getValue();
			btnHelp.setBounds(218 + 52 * btnHelp.getColumn(),
					177 + 52 * btnHelp.getRow(), 52, 52);
		}

		String[] headers = { "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" };
		for (int head = 0; head <= 6; head++) {
			JLabel header = new JLabel(headers[head], SwingConstants.CENTER);
			header.setBounds(218 + 52 * head, 151, 52, 20);
			this.add(header);
		}

		// Get real month/year
		currentMonth = inhalt.realMonth; // Match month and year
		currentYear = inhalt.realYear;
		lblYear.setText(Integer.toString(currentYear));

		// Refresh calendar
		refreshCalendar(currentMonth, currentYear); // Refresh calendar
	}

	private void updateLblAnzahl() {
		int i = 0;
		for (Urlaub u : tableModelAll.getChange()) {
			if ((u.getStatus().intValue() == 1 || u.getStatus().intValue() == 2)
					&& u.getJahr() == inhalt.realYear
					&& u.getTyp().equals(Urlaub.TYPURLAUB))
				++i;
		}
		rest = (inhalt.getPerson().getSoll().intValue() - i + inhalt
				.getPerson().getRest().intValue());
		lblAnzahl.setText("" + rest);
	}

	private Map<String, btnCalendar> initButtons() {
		Map<String, btnCalendar> buttons = new TreeMap<String, btnCalendar>();
		for (Integer row = 0; row < 6; row++) {
			for (Integer column = 0; column < 7; column++) {
				btnCalendar neuerButton = new btnCalendar(row, column);
				neuerButton.setMargin(new Insets(2, 1, 2, 1));
				neuerButton.setFont(myFont);
				buttons.put(row.toString() + column.toString(), neuerButton);
			}
		}
		return buttons;
	}

	private void refreshCalendar(int month, int year) {
		// Variables
		String[] months = { "Januar", "Februar", "März", "April", "Mai",
				"Juni", "Juli", "August", "September", "Oktober", "November",
				"Dezember" };
		int lastOfMonth, startOfMonth; // Number Of Days, Start Of Month

		// Allow/disallow buttons
		lblMonth.setText(months[month]); // Refresh the month label (at the top)

		// Get first day of month and number of days
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		lastOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		startOfMonth = cal.get(Calendar.DAY_OF_WEEK);

		for (Entry<String, btnCalendar> b : tblCalendar.entrySet()) {
			b.getValue().updateButtonStatus(null);
		}

		// Draw calendar
		Integer row = 0;
		Integer column = ((startOfMonth + 6) % 7) - 1;
		if (column == -1)
			column = 6;

		for (Integer i = 1; i <= lastOfMonth; i++) {
			String tag, monat;
			if (i < 10)
				tag = "0" + i.toString();
			else
				tag = i.toString();
			if ((currentMonth + 1) < 10)
				monat = "0" + (currentMonth + 1);
			else
				monat = Integer.toString(currentMonth + 1);

			tblCalendar.get(row.toString() + column.toString())
					.updateButtonStatus(currentYear + "-" + monat + "-" + tag);
			column++;
			if (column == 7) {
				column = 0;
				row++;
			}
		}
	}

	class myCellRenderer extends DefaultTableCellRenderer {
		public myCellRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			switch (tableModelAll.getChange().get(row).getStatus().intValue()) {
			case 1:
				setBackground(new Color(255, 255, 100));
				break;
			case 2:
				setBackground(new Color(100, 255, 100));
				break;
			case 3:
				setBackground(new Color(255, 100, 100));
				break;
			}
			setText((String) value);
			return this;
		}
	}

	class btnPrev_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (currentMonth > inhalt.realMonth
					|| currentYear > inhalt.realYear) {
				if (currentMonth == 0) { // Back one year
					currentMonth = 11;
					currentYear -= 1;
					lblYear.setText(Integer.toString(currentYear));
				} else { // Back one month
					currentMonth -= 1;
				}
				refreshCalendar(currentMonth, currentYear);
			}
		}
	}

	class btnNext_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (currentMonth < 2 || currentYear < (inhalt.realYear + 1)) {
				if (currentMonth == 11) { // Foward one year
					currentMonth = 0;
					currentYear += 1;
					lblYear.setText(Integer.toString(currentYear));
				} else { // Foward one month
					currentMonth += 1;
				}
				refreshCalendar(currentMonth, currentYear);
			}
		}
	}

	class btnCalendar extends JButton {
		private Integer row, column, status;
		private String value;
		private boolean arbeitstag;

		public btnCalendar(Integer r, Integer c) {
			this.row = r;
			this.column = c;
			this.value = null;
			this.status = new Integer(0);
			if (c > 4)
				this.arbeitstag = false;
			else
				this.arbeitstag = true;

			this.setText("");
			updateButtonColor();
			this.setEnabled(false);
			this.addActionListener(new btnCalendar_Action());
		}

		public String getValue() {
			return this.value;
		}

		public Integer getStatus() {
			return this.status;
		}

		public Integer getRow() {
			return this.row;
		}

		public Integer getColumn() {
			return this.column;
		}

		public void updateButtonStatus(String s) {
			if (s == null && this.value == null)
				return;
			if (s == null) {
				this.value = null;
				this.setText("");
				this.setEnabled(false);
			} else {
				if (inhalt.feiertag(s)) {
					this.arbeitstag = false;
					this.setEnabled(false);
				} else if (this.column > 4) {
					this.arbeitstag = false;
					this.setEnabled(false);
				} else if (Integer.parseInt(s.substring(0, 4)) == inhalt.realYear
						&& Integer.parseInt(s.substring(5, 7)) == (inhalt.realMonth + 1)
						&& Integer.parseInt(s.substring(8)) < inhalt.realDay) {
					this.arbeitstag = true;
					this.setEnabled(false);
				} else {
					this.setForeground(Color.BLACK);
					if (Integer.parseInt(s.substring(0, 4)) == inhalt.realYear
							&& Integer.parseInt(s.substring(5, 7)) == (inhalt.realMonth + 1)
							&& Integer.parseInt(s.substring(8)) == inhalt.realDay) {
						this.setForeground(Color.BLUE);
					} else if (inhalt.getDB().getFerien().contains(s))
						this.setForeground(new Color(205, 102, 29));
					this.arbeitstag = true;
					this.setEnabled(true);
				}
				Urlaub u = containsTableTag(s);
				if (u == null) {
					this.status = 0;
					this.setText(s.substring(8));
				} else {
					this.status = u.getStatus();
					this.setText(u.getTyp() + s.substring(8));
				}
				this.value = s;
			}
			updateButtonColor();
		}

		/**
		 * Wir färben die Hintergründe entsprechend des Wertes v.
		 * http://www.mediaevent.de/tutorial/farbcodes.html
		 * 
		 * @param v
		 */
		public void updateButtonColor() {
			if (this.value == null) {
				this.setBackground(Color.LIGHT_GRAY);
			} else if (!arbeitstag) {
				this.setBackground(new Color(238, 130, 238));
			} else if (checkTableForValue(value)) {
				this.setBackground(Color.blue);
			} else {
				switch (status) {
				case 0:
					this.setBackground(new Color(255, 255, 255));
					break;
				case 1:
					this.setBackground(new Color(255, 255, 100));
					break;
				case 2:
					this.setBackground(new Color(100, 255, 100));
					break;
				case 3:
					this.setBackground(new Color(255, 100, 100));
					break;
				}
			}
		}

		private boolean checkTableForValue(String sqlTag) {
			for (Urlaub u : tableModel.getChange()) {
				if (u.getSqlTag().equals(value)) {
					return true;
				}
			}
			return false;
		}

		class btnCalendar_Action implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				Urlaub help = null;
				for (Urlaub u : tableModel.getChange()) {
					if (u.getSqlTag().equals(value)) {
						help = u;
						break;
					}
				}
				if (Integer.parseInt(value.substring(0, 4)) == inhalt.realYear
						&& Integer.parseInt(value.substring(5, 7)) == (inhalt.realMonth + 1)
						&& Integer.parseInt(value.substring(8)) == inhalt.realDay) {
				} else if (help == null) {
					tableModel.addChange(new Urlaub(inhalt.getDB().getFreeID(),
							inhalt.getPerson().getID(), value, new Integer(1),
							inhalt.realYear, typ));
				} else {
					if (!tableModelAll.getChange().contains(help))
						inhalt.getDB().returnFreeID(new Integer(help.getID()));
					tableModel.removeChange(help);
				}
				updateButtonColor();
			}
		}
	}

	private Urlaub containsTableTag(String s) {
		Urlaub u = null;
		for (Urlaub u1 : tableModelAll.getChange()) {
			if (u1.getSqlTag().equals(s)) {
				u = u1;
				break;
			}
		}
		return u;
	}

	private void applyChanges() {
		Urlaub u, help;
		String s;
		if (tableModel.getChange().isEmpty()) {
			JOptionPane
					.showMessageDialog(
							null,
							"Hiermit bestätigen Sie Ihre Änderungen. Nur leider haben Sie gerade keine gewählt.",
							"Änderung", JOptionPane.CANCEL_OPTION);
			return;
		}
		Set<String> changeBeantragt = new TreeSet<String>();
		while (!tableModel.getChange().isEmpty()) {
			u = tableModel.getChange().get(0);
			s = u.getSqlTag();
			tableModel.removeChange(u);
			help = containsTableTag(u.getSqlTag());
			if (help == null) {
				if (rest == 0 && typ.equals(Urlaub.TYPURLAUB))
					continue;
				if (inhalt.getPerson().getPosten().intValue() == 3) {
					u = new Urlaub(Integer.parseInt(u.getID()), u.getPerson(),
							u.getSqlTag(), 2, u.getJahr(), typ);
					inhalt.getDB().addUrlaub(u);
					tableModelAll.addChange(new UrlaubMitTyp(new Integer(u
							.getID()), u.getPerson(), u.getSqlTag(), u
							.getStatus(), u.getJahr(), u.getTyp()));
				} else {
					inhalt.getDB().addUrlaub(u);
					tableModelAll.addChange(new UrlaubMitTyp(new Integer(u
							.getID()), u.getPerson(), u.getSqlTag(), u
							.getStatus(), u.getJahr(), u.getTyp()));
					changeBeantragt.add(s);
				}
			} else {
				s = help.getSqlTag();
				inhalt.getDB().returnFreeID(new Integer(u.getID()));
				switch (help.getStatus()) {
				case 1:
					inhalt.getDB().delUrlaub(help);
					tableModelAll.removeChange(new UrlaubMitTyp(new Integer(
							help.getID()), help.getPerson(), help.getSqlTag(),
							help.getStatus(), help.getJahr(), help.getTyp()));
					changeBeantragt.add(s);
					break;
				case 2:
					inhalt.getDB().delUrlaub(help);
					tableModelAll.removeChange(new UrlaubMitTyp(new Integer(
							help.getID()), help.getPerson(), help.getSqlTag(),
							help.getStatus(), help.getJahr(), help.getTyp()));
					changeBeantragt.add(s);
					break;
				case 3:
					inhalt.getDB().delUrlaub(help);
					tableModelAll.removeChange(new UrlaubMitTyp(new Integer(
							help.getID()), help.getPerson(), help.getSqlTag(),
							help.getStatus(), help.getJahr(), help.getTyp()));
					Urlaub u2 = new Urlaub(inhalt.getDB().getFreeID(), inhalt
							.getPerson().getID(), help.getSqlTag(),
							new Integer(1), inhalt.realYear, typ);
					inhalt.getDB().addUrlaub(u2);
					tableModelAll.addChange(new UrlaubMitTyp(new Integer(u2
							.getID()), u2.getPerson(), u2.getSqlTag(), u2
							.getStatus(), u2.getJahr(), u2.getTyp()));
					changeBeantragt.add(s);
					break;
				}
			}
			updateLblAnzahl();
		}
		for (Entry<String, btnCalendar> e : tblCalendar.entrySet()) {
			e.getValue().updateButtonStatus(e.getValue().getValue());
		}
		inhalt.getDB().dbSendChangeBeantragt(changeBeantragt);
	}

	public void open() {
		this.inhalt.getPanel().removeAll();
		this.inhalt.getPanel().add(this, BorderLayout.CENTER);
		this.inhalt.getPanel().validate();
		this.inhalt.getPanel().repaint();
	}
}
