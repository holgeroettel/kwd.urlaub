package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import main.Inhalt;
import daten.Gruppe;
import daten.Person;
import daten.Urlaub;

@SuppressWarnings("serial")
public class PlanUrlaubGenehmigen extends JPanel {
	public final static String CARDNAME = "genehmigen";

	private Inhalt inhalt;
	private ArrayList<Integer> listeHoehen = new ArrayList<Integer>();
	private ArrayList<String> listePersonen = new ArrayList<String>();
	private Map<String, Map<String, Urlaub>> mapPersonUrlaub = new TreeMap<String, Map<String, Urlaub>>();
	public int zeilenHoehe, zahlenBreite, currentXMonat;
	private SimpleDateFormat sdf = new SimpleDateFormat();

	public PlanUrlaubGenehmigen(Inhalt i) {
		super(null);
		this.inhalt = i;
		sdf.applyPattern("yyyy-MM-dd");
		zeilenHoehe = this.getFont().getSize() + (this.getFont().getSize() / 3);
		zahlenBreite = 17;
		JPanel firstColumn = initFirstColumn();
		initPersonUrlaubMap();
		JPanel content = initContent();
		JScrollPane scrollPane = new JScrollPane();
		JButton back = new JButton("Zurück");

		scrollPane.setViewportView(content);
		scrollPane.setRowHeaderView(firstColumn);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().setViewPosition(new Point(currentXMonat, 0));
		scrollPane.getHorizontalScrollBar().setUnitIncrement(45);
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);

		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inhalt.createHauptmenu();
			}
		});
		this.add(scrollPane);
		this.add(back);

		scrollPane.setBounds(0, 0, 1024, 698);
		back.setBounds(512, 703, 150, 26);
	}

	private void initPersonUrlaubMap() {
		for (String s : listePersonen) {
			mapPersonUrlaub.put(s, inhalt.initPersonenUrlaub(s));
		}
	}

	/**
	 * Baue mir ein Panel für die Jahre x und x+1 mit seinen Monaten und ordne
	 * den Personen des Panels links die Urlaubstage zu
	 * 
	 * @return
	 */
	private JPanel initContent() {
		JPanel result = new JPanel(null);
		// global
		String[] months = { "Januar", "Februar", "März", "April", "Mai",
				"Juni", "Juli", "August", "September", "Oktober", "November",
				"Dezember" };
		int year = inhalt.realYear;
		int currentX = 0;

		// Local
		GregorianCalendar cal = new GregorianCalendar(year-1, 0, 1);
		int month = -13;
		while (month < 15) {
			if (cal.get(GregorianCalendar.DAY_OF_MONTH) == 1)
				++month;

			if (month == 15)
				break;

			if ((month % 12) == 0
					&& cal.get(GregorianCalendar.DAY_OF_MONTH) == 1) {
				JLabel lblYear = new JLabel(" "
						+ String.valueOf(cal.get(GregorianCalendar.YEAR)));
				lblYear.setBounds(currentX, 0, 156, 26);
				lblYear.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
						Color.BLACK));
				lblYear.setOpaque(true);
				lblYear.setBackground(Color.WHITE);
				lblYear.setHorizontalAlignment(SwingConstants.LEFT);
				result.add(lblYear);
			}
			if (cal.get(GregorianCalendar.DAY_OF_MONTH) == 1) {
				int tage = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
				JLabel lblMonat = new JLabel(" "
						+ months[cal.get(GregorianCalendar.MONTH)]);
				lblMonat.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0,
						Color.BLACK));
				lblMonat.setBounds(currentX, listeHoehen.get(0) - 26, tage
						* zahlenBreite, 26);
				lblMonat.setOpaque(true);
				lblMonat.setBackground(Color.WHITE);
				lblMonat.setHorizontalAlignment(SwingConstants.LEFT);
				lblMonat.setVerticalAlignment(SwingConstants.BOTTOM);
				lblMonat.setLayout(new BorderLayout(0, 5));
				result.add(lblMonat);
				if (cal.get(GregorianCalendar.MONTH) == inhalt.realMonth
						&& cal.get(GregorianCalendar.YEAR) == inhalt.realYear) {
					currentXMonat = currentX;
				}
			}
			for (int idx = 0; idx < listeHoehen.size(); idx++) {
				DayButton button = new DayButton(cal, listePersonen.get(idx));
				button.setBounds(currentX, listeHoehen.get(idx), zahlenBreite,
						zeilenHoehe);
				result.add(button);
			}

			cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
			currentX += zahlenBreite;
		}

		result.setPreferredSize(new Dimension(currentX, 698));
		result.validate();
		return result;
	}

	/**
	 * Baue das linke Panel, mit sämtlichen Mitarbeitern der Abteilung XY
	 * 
	 * @return
	 */
	private JPanel initFirstColumn() {
		JPanel result = new JPanel(null);
		LabelProducer produce = new LabelProducer(13);
		ArrayList<Person> helpListePerson = new ArrayList<Person>();

		if (!inhalt.getPlanGruppe().getAbteilung().getChefs().isEmpty()) {
			JLabel chef = produce.produceHeading("Abteilungsleiter");
			result.add(chef);
			// Hole alle Personen die Chef sind
			for (String s : inhalt.getPlanGruppe().getAbteilung().getChefs()) {
				helpListePerson.add(inhalt.getDB().getPersonen().get(s));
			}
			// Alphabetisch sortieren
			Collections.sort(helpListePerson);
			// Label bauen und hinzufügen
			for (Person p : helpListePerson) {
				JLabel helpChef = produce.produceLine(p.getData());
				listePersonen.add(p.getID());
				result.add(helpChef);
			}
			helpListePerson.clear();
		}

		String subChefString = inhalt.getPlanGruppe().getAbteilung()
				.getSubChef();
		if (subChefString != null) {
			JLabel subChef = produce.produceHeading("Stellvertreter");
			result.add(subChef);
			// Hole Person p aus DB
			Person p = inhalt.getDB().getPersonen()
					.get(inhalt.getPlanGruppe().getAbteilung().getSubChef());
			JLabel helpSub = produce.produceLine(p.getData());
			listePersonen.add(p.getID());
			result.add(helpSub);
		}

		// Hole alle Gruppen
		ArrayList<Gruppe> helpListeGruppe = new ArrayList<Gruppe>();
		for (String s : inhalt.getPlanGruppe().getGruppen().keySet()) {
			Gruppe g = inhalt.getDB().getGruppen().get(s);
			helpListeGruppe.add(g);
		}
		// Sortieren
		Collections.sort(helpListeGruppe);

		for (Gruppe g : helpListeGruppe) {
			// hole die Gruppe aus DB für den Schlüssel
			JLabel headGruppe = produce.produceHeading(g.getData());
			result.add(headGruppe);

			for (String s : inhalt.getPlanGruppe().getGruppen().get(g.getID())) {
				Person p = inhalt.getDB().getPersonen().get(s);
				helpListePerson.add(p);
			}
			Collections.sort(helpListePerson);

			for (Person p : helpListePerson) {
				// hole die Person p die Mitglied der Gruppe
				JLabel memberGruppe = produce.produceLine(p.getData());
				listePersonen.add(p.getID());
				result.add(memberGruppe);
			}
			helpListePerson.clear();
		}

		if (!inhalt.getPlanGruppe().getNichtGruppierteAngestellte().isEmpty()) {
			JLabel rest = produce.produceHeading("Sonstige Mitarbeiter");
			result.add(rest);
			// alle restlichen Mitarbeiter
			for (String s : inhalt.getPlanGruppe()
					.getNichtGruppierteAngestellte()) {
				Person p = inhalt.getDB().getPersonen().get(s);
				helpListePerson.add(p);
			}
			// sortieren
			Collections.sort(helpListePerson);
			// dem Panel hinzufügen
			for (Person p : helpListePerson) {
				// hole die Person aus der DB
				JLabel nichtGruppierter = produce.produceLine(p.getData());
				listePersonen.add(p.getID());
				result.add(nichtGruppierter);
			}
		}

		helpListePerson.clear();
		helpListePerson = null;
		helpListeGruppe.clear();
		helpListeGruppe = null;

		result.setPreferredSize(new Dimension(150, 530));
		return result;
	}

	public void open() {
		this.inhalt.getPanel().removeAll();
		this.inhalt.getPanel().add(this, BorderLayout.CENTER);
		this.inhalt.getPanel().validate();
		this.inhalt.getPanel().repaint();
	}

	class LabelProducer {
		private int currentHeight;

		public LabelProducer(int beginn) {
			this.currentHeight = beginn;
		}

		public JLabel produceHeading(String titel) {
			JLabel result = new JLabel(titel);
			result.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
					Color.BLACK));
			currentHeight += 6;
			result.setBounds(0, currentHeight, 160, 26);
			result.setOpaque(true);
			result.setBackground(Color.WHITE);
			// Mittig vom unteren Rand 5px weg
			result.setHorizontalAlignment(SwingConstants.CENTER);
			result.setVerticalAlignment(SwingConstants.BOTTOM);
			result.setLayout(new BorderLayout(0, 5));
			currentHeight += 26;
			return result;
		}

		public JLabel produceLine(String titel) {
			listeHoehen.add(currentHeight);
			JLabel result = new JLabel(titel);
			result.setFont(result.getFont().deriveFont(Font.PLAIN));
			result.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1,
					Color.BLACK));
			result.setBounds(0, currentHeight, 160, zeilenHoehe);
			result.setOpaque(true);
			result.setBackground(Color.WHITE);
			result.setHorizontalAlignment(SwingConstants.CENTER);
			currentHeight += zeilenHoehe;
			return result;
		}
	}

	class DayButton extends JButton {
		private Urlaub urlaub;
		private boolean arbeitstag;

		public DayButton(GregorianCalendar calender, String person) {
			super();
			this.setMargin(new Insets(0, 0, 0, 0));
			this.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0,
					Color.BLACK));
			this.setHorizontalAlignment(SwingConstants.CENTER);
			String sqlTag = sdf.format(calender.getTime());
			urlaub = inhalt.containsUrlaubTag(sqlTag,
					mapPersonUrlaub.get(person));

			if (calender.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY
					|| calender.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
				this.arbeitstag = false;
				this.setEnabled(false);
				this.setText(sqlTag.substring(8));
			} else if (inhalt.feiertag(sqlTag)) {
				this.arbeitstag = false;
				this.setEnabled(false);
				this.setText(sqlTag.substring(8));
			} else if (urlaub == null) {
				this.arbeitstag = true;
				this.setEnabled(false);
				this.setText(sqlTag.substring(8));
			} else {
				this.arbeitstag = true;
				this.setEnabled(true);
				this.setText(urlaub.getTyp());
			}
			updateButtonColor();
			this.addActionListener(new DayButtonAction());
		}

		/**
		 * Wir färben die Hintergründe entsprechend des Wertes v.
		 * http://www.mediaevent.de/tutorial/farbcodes.html
		 * 
		 * @param v
		 */
		public void updateButtonColor() {
			if (!arbeitstag) {
				this.setBackground(new Color(238, 130, 238));
			} else if (this.urlaub == null) {
				this.setBackground(Color.WHITE);
			} else {
				switch (this.urlaub.getStatus().intValue()) {
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

		class DayButtonAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (inhalt.getPerson().getPosten().intValue() <= inhalt.getDB()
						.getPersonen().get(urlaub.getPerson()).getPosten()
						.intValue()) {
					return;
				}
				switch (urlaub.getStatus().intValue()) {
				case 1:
					inhalt.getDB().delUrlaub(urlaub);
					Urlaub u1 = new Urlaub(inhalt.getDB().getFreeID(),
							urlaub.getPerson(), urlaub.getSqlTag(),
							new Integer(2), urlaub.getJahr(), urlaub.getTyp());
					urlaub = u1;
					inhalt.getDB().addUrlaub(u1);
					Person p1 = inhalt.getDB().getPersonen()
							.get(u1.getPerson());
					if (p1.getPosten().intValue() != 3) {
						inhalt.getDB().dbSendChangeGenehmigt(p1.getID(),
								u1.getSqlTag());
					}
					break;
				case 2:
					inhalt.getDB().delUrlaub(urlaub);
					Urlaub u2 = new Urlaub(inhalt.getDB().getFreeID(),
							urlaub.getPerson(), urlaub.getSqlTag(),
							new Integer(3), urlaub.getJahr(), urlaub.getTyp());
					urlaub = u2;
					inhalt.getDB().addUrlaub(u2);
					Person p2 = inhalt.getDB().getPersonen()
							.get(u2.getPerson());
					if (p2.getPosten().intValue() != 3) {
						inhalt.getDB().dbSendChangeGenehmigt(p2.getID(),
								u2.getSqlTag());
					}
					break;
				case 3:
					inhalt.getDB().delUrlaub(urlaub);
					Urlaub u3 = new Urlaub(inhalt.getDB().getFreeID(),
							urlaub.getPerson(), urlaub.getSqlTag(),
							new Integer(1), urlaub.getJahr(), urlaub.getTyp());
					urlaub = u3;
					inhalt.getDB().addUrlaub(u3);
					Person p3 = inhalt.getDB().getPersonen()
							.get(u3.getPerson());
					if (p3.getPosten().intValue() != 3) {
						inhalt.getDB().dbSendChangeGenehmigt(p3.getID(),
								u3.getSqlTag());
					}
					break;
				}
				updateButtonColor();
			}
		}
	}
}
