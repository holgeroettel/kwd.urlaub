package main;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import panels.Hauptmenu;
import panels.Login;
import panels.PlanAuswertung;
import panels.PlanGruppe;
import panels.PlanPasswort;
import panels.PlanPersonen;
import panels.PlanUrlaub;
import panels.PlanUrlaubGenehmigen;
import daten.Datenbank;
import daten.Person;
import daten.Urlaub;

/**
 * Der Inhalt. Unsere zentrale Klasse. Diese hält die Schnittstelle zur
 * Datenbank und alle möglichen Fenster die wir einmal angezeigt bekommen
 * wollen. Weiterhin hält sie unsere Überschrift im JFrame.
 * 
 * @author oettel
 * 
 */
@SuppressWarnings("serial")
public class Inhalt extends JFrame {
	private JPanel inhalt;
	private CardLayout cl;
	private Datenbank db;
	public int realYear, realMonth, realDay, screenWidth;
	private Set<String> feiertage = new TreeSet<String>();
	private Person person;
	private Login login;
	private Hauptmenu hauptmenu;
	private PlanPasswort passwort;
	private PlanUrlaub planen;
	private PlanUrlaubGenehmigen genehmigung;
	private PlanGruppe gruppen;
	private PlanAuswertung auswertung = null;
	private PlanPersonen personal;

	public Inhalt() {
		super("Urlaubsplaner");
		this.setPreferredSize(new Dimension(1024, 768));
		Calendar cal = Calendar.getInstance(); // Create calendar
		realDay = cal.get(Calendar.DAY_OF_MONTH);
		realMonth = cal.get(Calendar.MONTH); // Get month
		realYear = cal.get(Calendar.YEAR); // Get year
		initFeiertage(realYear + "-" + realMonth + "-" + realDay);
		this.inhalt = (JPanel) this.getContentPane();
		this.inhalt.setLayout(new CardLayout());
		cl = (CardLayout) (inhalt.getLayout());
		screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		createLogin();
		this.pack();
		this.setVisible(true);
	}

	private void showCard(String name) {
		cl.show(inhalt, name);
	}

	private void createLogin() {
		this.login = new Login(this);
		inhalt.add(login, Login.CARDNAME);
		showCard(Login.CARDNAME);
	}

	public void createHauptmenu() {
		if (hauptmenu == null) {
			this.hauptmenu = new Hauptmenu(this);
			inhalt.add(hauptmenu, Hauptmenu.CARDNAME);
		}
		showCard(Hauptmenu.CARDNAME);
	}

	public void createGenehmigung() {
		if (gruppen == null) {
			this.gruppen = new PlanGruppe(this);
			inhalt.add(gruppen, PlanGruppe.CARDNAME);
		}
		if (genehmigung == null) {
			this.genehmigung = new PlanUrlaubGenehmigen(this);
			inhalt.add(genehmigung, PlanUrlaubGenehmigen.CARDNAME);
		}
		showCard(PlanUrlaubGenehmigen.CARDNAME);
	}

	public void createPasswort() {
		if (passwort == null) {
			this.passwort = new PlanPasswort(this);
			inhalt.add(passwort, PlanPasswort.CARDNAME);
		}
		showCard(PlanPasswort.CARDNAME);
	}

	public void createGruppen() {
		if (gruppen == null) {
			this.gruppen = new PlanGruppe(this);
			inhalt.add(gruppen, PlanGruppe.CARDNAME);
		}
		showCard(PlanGruppe.CARDNAME);
	}

	public void createAuswertung() {
		if (auswertung == null) {
			this.auswertung = new PlanAuswertung(this);
			inhalt.add(auswertung, PlanAuswertung.CARDNAME);
		}
		showCard(PlanAuswertung.CARDNAME);
	}

	public void createPersonal() {
		if (personal == null) {
			this.personal = new PlanPersonen(this);
			inhalt.add(personal, PlanPersonen.CARDNAME);
		}
		showCard(PlanPersonen.CARDNAME);
	}

	public void createPlan() {
		if (planen == null) {
			this.planen = new PlanUrlaub(this);
			inhalt.add(planen, PlanUrlaub.CARDNAME);
		}
		showCard(PlanUrlaub.CARDNAME);
	}

	public JPanel getPanel() {
		return this.inhalt;
	}

	public Datenbank getDB() {
		return this.db;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person p) {
		this.person = p;
		this.db = new Datenbank(realYear + "-" + realMonth + "-" + realDay, p);
	}

	public PlanGruppe getPlanGruppe() {
		return this.gruppen;
	}

	public Map<String, Urlaub> initPersonenUrlaub(String personID) {
		Map<String, Urlaub> pUrlaub = new TreeMap<String, Urlaub>();
		Statement stmt;
		ResultSet rs;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";

			Connection con = DriverManager.getConnection(url, "user", "user");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			rs = stmt.executeQuery("select * from urlaub where person like '"
					+ personID + "' order by person");
			while (rs.next()) {
				Urlaub u = new Urlaub(rs.getInt(1), rs.getString(2),
						rs.getString(3), new Integer(rs.getInt(4)),
						rs.getInt(5), rs.getString(6));
				pUrlaub.put(u.getSqlTag(), u);
			}// end while loop
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pUrlaub;
	}

	public Urlaub containsUrlaubTag(String sqlTag,
			Map<String, Urlaub> personenUrlaub) {
		if (personenUrlaub == null)
			return null;
		for (Entry<String, Urlaub> e : personenUrlaub.entrySet()) {
			if (e.getValue() == null) {
				continue;
			}
			if (e.getValue().getSqlTag().equals(sqlTag)) {
				return e.getValue();
			}
		}
		return null;
	}

	public boolean feiertag(String s) {
		if (s == null)
			return false;
		else
			return feiertage.contains(s);
	}

	private boolean initFeiertage(String s) {
		int X = new Integer(s.substring(0, 4));
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd");

		// Die festen Feiertage hinzufügen
		feiertage.add(sdf.format(new GregorianCalendar(X, 0, 1).getTime()));
		feiertage.add(sdf.format(new GregorianCalendar(X, 4, 1).getTime()));
		feiertage.add(sdf.format(new GregorianCalendar(X, 9, 3).getTime()));
		feiertage.add(sdf.format(new GregorianCalendar(X, 9, 31).getTime()));
		GregorianCalendar busUndBet = new GregorianCalendar(X, 10, 23);
		do {
			busUndBet.add(GregorianCalendar.DAY_OF_YEAR, -1);
		} while (busUndBet.get(GregorianCalendar.DAY_OF_WEEK) != 4);
		feiertage.add(sdf.format(busUndBet.getTime()));
		feiertage.add(sdf.format(new GregorianCalendar(X, 11, 25).getTime()));
		feiertage.add(sdf.format(new GregorianCalendar(X, 11, 26).getTime()));

		int K = X / 100;
		int M = 15 + (3 * K + 3) / 4 - (8 * K + 13) / 25;
		int S = 2 - (3 * K + 3) / 4;
		int A = X % 19;
		int D = (19 * A + M) % 30;
		int R = (D + A / 11) / 29;
		int OG = 21 + D - R;
		int SZ = 7 - (X + X / 4 + S) % 7;
		int OE = 7 - (OG - SZ) % 7;
		int OS = OG + OE;
		GregorianCalendar osterSonntag = new GregorianCalendar();
		osterSonntag.set(X, 2, 0);
		osterSonntag.add(GregorianCalendar.DAY_OF_YEAR, OS);

		GregorianCalendar karFreitag = (GregorianCalendar) osterSonntag.clone();
		karFreitag.add(GregorianCalendar.DAY_OF_YEAR, -2);
		feiertage.add(sdf.format(karFreitag.getTime()));

		GregorianCalendar osterMontag = (GregorianCalendar) osterSonntag
				.clone();
		osterMontag.add(GregorianCalendar.DAY_OF_YEAR, 1);
		feiertage.add(sdf.format(osterMontag.getTime()));

		GregorianCalendar himmelfahrt = (GregorianCalendar) osterSonntag
				.clone();
		himmelfahrt.add(GregorianCalendar.DAY_OF_YEAR, 39);
		feiertage.add(sdf.format(himmelfahrt.getTime()));

		GregorianCalendar pfingstMontag = (GregorianCalendar) osterSonntag
				.clone();
		pfingstMontag.add(GregorianCalendar.DAY_OF_YEAR, 50);
		feiertage.add(sdf.format(pfingstMontag.getTime()));

		return true;
	}

}
