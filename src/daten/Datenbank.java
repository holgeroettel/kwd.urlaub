package daten;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Datenbank {
	private Map<String, Person> personen;
	private Map<String, Abteilung> abteilungen;
	private Map<String, Urlaub> urlaubstage;
	// private Map<String, Set<String>> changedGenehmigt;
	private Map<String, Gruppe> gruppen;
	private ArrayList<Integer> freieID;
	private Set<String> admins, ferien;// , changedBeantragt;
	private int letzteID;
	private String jahr;
	private String lastLogin;
	private Person person;

	public Datenbank(String heute, Person p) {
		this.personen = new TreeMap<String, Person>();
		this.abteilungen = new TreeMap<String, Abteilung>();
		this.urlaubstage = new TreeMap<String, Urlaub>();
		// this.changedGenehmigt = new TreeMap<String, Set<String>>();
		// this.changedBeantragt = new TreeSet<String>();
		this.gruppen = new TreeMap<String, Gruppe>();
		this.admins = new TreeSet<String>();
		this.ferien = new TreeSet<String>();
		this.freieID = new ArrayList<Integer>();
		this.letzteID = 1;
		this.jahr = new Integer(Calendar.getInstance().get(Calendar.YEAR))
				.toString();

		localAddPerson(p);
		if (!init(p)) {
			System.out.println("startDB schlägt fehl!");
		}
		this.person = p;
	}

	@SuppressWarnings("resource")
	private boolean init(Person p) {
		Statement stmt;
		ResultSet rs;
		try {
			// Für alle gilt, dass ich Ferien, freieIDs und lastLogin bekomme
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";

			Connection con = DriverManager.getConnection(url, "user", "user");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			rs = stmt.executeQuery("select * from ferien order by tag");
			while (rs.next()) {
				ferien.add(rs.getString(1));
			}// end while loop

			rs = stmt
					.executeQuery("select * from system where system.system = 'system' order by lastlogin");
			while (rs.next()) {
				this.lastLogin = rs.getString(2);
			}// end while loop

			rs = stmt.executeQuery("SELECT * from freieID ORDER BY id");
			while (rs.next()) {
				freieID.add(new Integer(rs.getInt(1)));
			}// end while loop
			stmt.executeUpdate("truncate table freieID");

			rs = stmt.executeQuery("SELECT MAX(id) from urlaub");
			rs.next();
			letzteID = rs.getInt(1);
			letzteID++;
			// end while loop

			// Für Admins gilt, dass sie alle Personen kennen müssen.
			rs = stmt.executeQuery("SELECT person from admins where person = '"
					+ p.getID() + "'");
			if (rs.next()) {
				localAddAdmin(rs.getString(1));
				rs = stmt
						.executeQuery("select * from personen order by person");
				while (rs.next()) {
					if (!localAddPerson(new Person(rs.getString(1),
							rs.getString(2), rs.getString(3), new Integer(
									rs.getInt(4)), new Integer(rs.getInt(5)),
							new Integer(rs.getInt(6)), rs.getString(7),
							rs.getString(8))))
						return false;
				}// end while loop
			}

			switch (p.getPosten()) {
			case 0:
				rs = stmt.executeQuery("SELECT * "
						+ "from urlaub where person = '" + p.getID()
						+ "' ORDER BY id");
				while (rs.next()) {
					if (!localAddUrlaub(new Urlaub(rs.getInt(1),
							rs.getString(2), rs.getString(3), new Integer(
									rs.getInt(4)), rs.getInt(5),
							rs.getString(6))))
						return false;
				}// end while loop
				return true;
			case 1:
			case 2:
				// Erst Abteilungen, dann Gruppen und Person, dann den Urlaub
				Abteilung neueAbteilung = new Abteilung(
						Abteilung.getBezeichnung(p.getAbteilung()));
				this.abteilungen.put(neueAbteilung.getBez(), neueAbteilung);

				// Personen holen, where Abteilung like 012%
				String whereString = "where";
				int idx = 0;
				for (String s : Abteilung.getArrayAbteilungen(neueAbteilung
						.getBez())) {
					if (idx > 0)
						whereString += " or";
					whereString += " abteilung like '" + s + "%'";
					idx++;
				}
				rs = stmt.executeQuery("select * from personen " + whereString
						+ " order by person");
				while (rs.next()) {
					if (!localAddPerson(new Person(rs.getString(1),
							rs.getString(2), rs.getString(3), new Integer(
									rs.getInt(4)), new Integer(rs.getInt(5)),
							new Integer(rs.getInt(6)), rs.getString(7),
							rs.getString(8))))
						return false;
				}// end while loop

				// Gruppen holen
				whereString = "where";
				idx = 0;
				for (String s : Abteilung.getArrayAbteilungen(neueAbteilung
						.getBez())) {
					if (idx > 0)
						whereString += " or";
					whereString += " gruppe like '" + s + "%'";
					idx++;
				}
				rs = stmt.executeQuery("select * from gruppen " + whereString
						+ " order by gruppe");
				while (rs.next()) {
					if (!localAddGruppe(new Gruppe(rs.getString(1),
							rs.getString(2))))
						return false;
				}// end while loop

				// Freie Gruppen Ids holen, die zu uns passen
				whereString = "where";
				idx = 0;
				for (String s : Abteilung.getArrayAbteilungen(neueAbteilung
						.getBez())) {
					if (idx > 0)
						whereString += " or";
					whereString += " id like '" + s + "%'";
					idx++;
				}
				rs = stmt.executeQuery("SELECT * "
						+ "from freieGruppenID ORDER BY id");
				while (rs.next()) {
					abteilungen.get(Abteilung.getBezeichnung(rs.getString(1)))
							.getFreieID().add(rs.getString(1));
				}// end while loop
				stmt.executeUpdate("delete from freieGruppenID " + whereString);

				// Urlaub für die Abteilung holen
				whereString = "where";
				idx = 0;
				for (String s : neueAbteilung.getAlleDerAbteilung()) {
					if (idx > 0)
						whereString += " or";
					whereString += " person = '" + s + "'";
					idx++;
				}
				rs = stmt.executeQuery("select * from urlaub " + whereString
						+ " order by id");
				while (rs.next()) {
					if (!localAddUrlaub(new Urlaub(rs.getInt(1),
							rs.getString(2), rs.getString(3), new Integer(
									rs.getInt(4)), rs.getInt(5),
							rs.getString(6))))
						return false;
				}// end while loop

				return true;
			case 3:
				initAbteilungen();
				return startDB();
			}

		} catch (SQLException e) {
			System.out.println("huba");
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	private void initAbteilungen() {
		for (String a : Abteilung.ABTEILUNGEN) {
			Abteilung neueAbteilung = new Abteilung(a);
			this.abteilungen.put(neueAbteilung.getBez(), neueAbteilung);
		}
	}

	private boolean startDB() {
		Statement stmt;
		ResultSet rs;
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";

			Connection con = DriverManager.getConnection(url, "user", "user");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			rs = stmt.executeQuery("select * from personen order by person");
			while (rs.next()) {
				if (!localAddPerson(new Person(rs.getString(1),
						rs.getString(2), rs.getString(3), new Integer(
								rs.getInt(4)), new Integer(rs.getInt(5)),
						new Integer(rs.getInt(6)), rs.getString(7),
						rs.getString(8))))
					return false;
			}// end while loop

			rs = stmt.executeQuery("SELECT * " + "from urlaub ORDER BY id");

			while (rs.next()) {
				if (!localAddUrlaub(new Urlaub(rs.getInt(1), rs.getString(2),
						rs.getString(3), new Integer(rs.getInt(4)),
						rs.getInt(5), rs.getString(6))))
					return false;
			}// end while loop

			rs = stmt
					.executeQuery("SELECT * " + "from gruppen ORDER BY gruppe");

			while (rs.next()) {
				if (!localAddGruppe(new Gruppe(rs.getString(1), rs.getString(2))))
					return false;
			}// end while loop

			rs = stmt.executeQuery("SELECT * "
					+ "from freieGruppenID ORDER BY id");
			while (rs.next()) {
				abteilungen.get(Abteilung.getBezeichnung(rs.getString(1)))
						.getFreieID().add(rs.getString(1));
			}// end while loop
			stmt.executeUpdate("truncate table freieGruppenID");

			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean addPerson(Person person) {
		if (localAddPerson(person))
			return dbAddPerson(person);
		return false;
	}

	private boolean localAddPerson(Person person) {
		getPersonen().put(person.getNr(), person);

		if (abteilungen.containsKey(Abteilung.getBezeichnung(person
				.getAbteilung()))) {
			Abteilung a = this.abteilungen.get(Abteilung.getBezeichnung(person
					.getAbteilung()));
			switch (person.getPosten()) {
			case 0:
				a.getAngestellte().add(person.getNr());
				return true;
			case 1:
				if (a.getSubChef() == null)
					a.setSubChef(person.getNr());
				else
					System.out.println("Wollte SubChef: " + person.getNr()
							+ " hinzufügen, aber es gibt schon einen: "
							+ a.getSubChef());
				return true;
			case 2:
				a.getChefs().add(person.getNr());
				return true;
			case 3:
				a.getChefs().add(person.getNr());
				return true;
			}
			return false;
		}
		return true;
	}

	private boolean dbAddPerson(Person person) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			if (person.getGruppe() == null)
				stmt.executeUpdate("INSERT INTO personen (person, name, vorname, posten, soll, ist, abteilung, gruppe) "
						+ "VALUES ('"
						+ person.getNr()
						+ "', '"
						+ person.getName()
						+ "', '"
						+ person.getVorname()
						+ "', '"
						+ person.getPosten().toString()
						+ "', '"
						+ person.getSoll().toString()
						+ "', '"
						+ person.getRest().toString()
						+ "', '"
						+ person.getAbteilung() + "', NULL)");
			else
				stmt.executeUpdate("INSERT INTO personen (person, name, vorname, posten, soll, ist, abteilung, gruppe) "
						+ "VALUES ('"
						+ person.getNr()
						+ "', '"
						+ person.getName()
						+ "', '"
						+ person.getVorname()
						+ "', '"
						+ person.getPosten().toString()
						+ "', '"
						+ person.getSoll().toString()
						+ "', '"
						+ person.getRest().toString()
						+ "', '"
						+ person.getAbteilung()
						+ "', '"
						+ person.getGruppe()
						+ "')");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return true;
	}

	public boolean delPerson(Person person) {
		if (localDelPerson(person))
			return dbDelPerson(person);
		return delAllFromPerson(person);
	}

	private boolean delAllFromPerson(Person person) {
		for (Entry<String, Urlaub> e : getUrlaubstage().entrySet()) {
			if (!e.getValue().getPerson().equals(person.getNr()))
				continue;
			if (!delUrlaub(e.getValue()))
				return false;
		}
		return true;
	}

	private boolean localDelPerson(Person person) {
		getPersonen().remove(person.getNr());
		if (abteilungen.containsKey(Abteilung.getBezeichnung(person
				.getAbteilung()))) {
			Abteilung a = this.abteilungen.get(Abteilung.getBezeichnung(person
					.getAbteilung()));
			switch (person.getPosten()) {
			case 0:
				a.getAngestellte().remove(person.getNr());
				return true;
			case 1:
				if (a.getSubChef() != null)
					a.setSubChef(null);
				else
					System.out.println("Wollte SubChef: " + person.getNr()
							+ " entfernen, aber es gibt keinen SubChef");
				return true;
			case 2:
				a.getChefs().remove(person.getNr());
				return true;
			}
			return false;
		}
		return true;
	}

	private boolean dbDelPerson(Person person) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			stmt.executeUpdate("DELETE FROM personen WHERE person = '"
					+ person.getNr() + "'");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return true;
	}

	public boolean addUrlaub(Urlaub urlaub) {
		if (localAddUrlaub(urlaub))
			return dbAddUrlaub(urlaub);
		return false;
	}

	private boolean localAddUrlaub(Urlaub urlaub) {
		if (!getUrlaubstage().containsKey(urlaub.getID())) {
			getUrlaubstage().put(urlaub.getID(), urlaub);
			return true;
		} else
			return false;
	}

	private boolean dbAddUrlaub(Urlaub urlaub) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			stmt.executeUpdate("INSERT INTO urlaub (id, person, tag, status, jahr, typ) "
					+ "VALUES ('"
					+ urlaub.getID()
					+ "', '"
					+ urlaub.getPerson()
					+ "', '"
					+ urlaub.getSqlTag()
					+ "', '"
					+ urlaub.getStatus().toString()
					+ "', "
					+ urlaub.getJahr()
					+ ", '" + urlaub.getTyp() + "')");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return true;
	}

	public boolean delUrlaub(Urlaub urlaub) {
		if (localDelUrlaub(urlaub))
			return dbDelUrlaub(urlaub);
		return false;
	}

	private boolean localDelUrlaub(Urlaub urlaub) {
		getUrlaubstage().remove(urlaub.getID());
		this.freieID.add(Integer.parseInt(urlaub.getID()));
		return true;
	}

	private boolean dbDelUrlaub(Urlaub urlaub) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			stmt.executeUpdate("DELETE FROM urlaub WHERE id = '"
					+ urlaub.getID() + "'");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return true;
	}

	public double dbUrlaubCountForMonthOfAbteilung(String sqlJahrMonat,
			String abteilung) {
		int result = 0;
		int idx = 0;
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();
			ResultSet rs = null;

			for (String s : abteilungen.get(abteilung).getAlleDerAbteilung()) {
				rs = stmt
						.executeQuery("select person, sum(case when urlaub.status=1"
								+ " then 1 when urlaub.status=2 then 1 else 0 end) as"
								+ " total from urlaub where urlaub.tag like '"
								+ sqlJahrMonat
								+ "%' and urlaub.person = '"
								+ s
								+ "' and urlaub.typ = '"
								+ Urlaub.TYPURLAUB
								+ "' group by urlaub.person");
				while (rs.next()) {
					result += rs.getInt(2);
				}// end while loop
				idx++;
			}
			con.close();
		} catch (SQLException e) {
			e.getMessage();
			return 0;
		}// end catch
		if (idx == 0)
			return 0;
		else
			return 1.0 * result / idx;
	}

	public int dbUrlaubSumSoll(int year) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery("select sum(personen.soll) from personen");
			while (rs.next()) {
				return rs.getInt(1);
			}// end while loop

			con.close();
		} catch (SQLException e) {
			e.getMessage();
			return 0;
		}// end catch
		return 0;
	}

	public int dbUrlaubCountForMonth(String yearMonth) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			ResultSet rs = stmt
					.executeQuery("select urlaub.id, count(urlaub.id) from urlaub where "
							+ "(urlaub.status = 1 or urlaub.status = 2) and urlaub.tag like '"
							+ yearMonth
							+ "%' and urlaub.typ = '"
							+ Urlaub.TYPURLAUB + "'");
			while (rs.next()) {
				return rs.getInt(2);
			}// end while loop
			con.close();
		} catch (SQLException e) {
			e.getMessage();
			return 0;
		}// end catch
		return 0;
	}

	public boolean addGruppe(Gruppe gruppe) {
		if (localAddGruppe(gruppe))
			return dbAddGruppe(gruppe);
		return false;
	}

	private boolean localAddGruppe(Gruppe gruppe) {
		if (!getGruppen().containsKey(gruppe.getID())) {
			getGruppen().put(gruppe.getID(), gruppe);
			if (!abteilungen.containsKey(Abteilung.getBezeichnung(gruppe
					.getID())))
				return false;
			Abteilung a = abteilungen.get(Abteilung.getBezeichnung(gruppe
					.getID()));
			int i = Integer.parseInt(gruppe.getID().substring(3), 16);
			if (i >= a.getLetzteID()) {
				a.setLetzteID(i);
				a.incrementLetzteID();
			}
			return true;
		} else
			return false;
	}

	private boolean dbAddGruppe(Gruppe gruppe) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			stmt.executeUpdate("INSERT INTO gruppen (gruppe, bez) "
					+ "VALUES ('" + gruppe.getID() + "', '" + gruppe.getBez()
					+ "')");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return true;
	}

	public boolean delGruppe(Gruppe gruppe) {
		if (localDelGruppe(gruppe))
			return dbDelGruppe(gruppe);
		return false;
	}

	private boolean localDelGruppe(Gruppe gruppe) {
		Gruppe help = getGruppen().get(gruppe.getID());
		if (help == null)
			return false;

		getGruppen().remove(help.getID());
		return localDelAllGruppe(gruppe);
	}

	private boolean localDelAllGruppe(Gruppe gruppe) {
		Abteilung a = abteilungen.get(Abteilung.getBezeichnung(gruppe.getID()));
		System.out.println("finde Abteilung: " + a.getBez());
		Person p = null;
		for (String s : a.getAngestellte()) {
			p = personen.get(s);
			System.out.println(p.getGruppe());
			if (p.getGruppe() == null)
				continue;
			if (p.getGruppe().equals(gruppe.getID())) {
				p.setGruppe(gruppe.getID());
			}
		}
		a.getFreieID().add(gruppe.getID());
		return true;
	}

	private boolean dbDelGruppe(Gruppe gruppe) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			stmt.executeUpdate("DELETE FROM gruppen WHERE gruppe = '"
					+ gruppe.getID() + "'");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return dbDelAllFromGruppe(gruppe);
	}

	private boolean dbDelAllFromGruppe(Gruppe gruppe) {
		try {
			PreparedStatement prepStmt = null;
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			prepStmt = con
					.prepareStatement("UPDATE personen SET gruppe = ? WHERE personen.gruppe = '"
							+ gruppe.getID() + "'");
			prepStmt.setNull(1, Types.CHAR);
			prepStmt.execute();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean addAdmin(String admin) {
		if (localAddAdmin(admin))
			return dbAddAdmin(admin);
		return false;
	}

	private boolean localAddAdmin(String admin) {
		getAdmins().add(admin);
		return true;
	}

	private boolean dbAddAdmin(String admin) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			stmt.executeUpdate("INSERT INTO admins (person) " + "VALUES ('"
					+ admin + "')");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return true;
	}

	public boolean delAdmin(String admin) {
		if (localDelAdmin(admin))
			return dbDelAdmin(admin);
		return false;
	}

	private boolean localDelAdmin(String admin) {
		getAdmins().remove(admin);
		return true;
	}

	private boolean dbDelAdmin(String admin) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			stmt.executeUpdate("DELETE FROM admins WHERE person = '" + admin
					+ "'");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return true;
	}

	public boolean addFerienTage(Set<String> sqlTage) {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();
			for (String s : sqlTage)
				stmt.executeUpdate("insert into ferien(tag) values ('" + s
						+ "')");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		ferien.clear();
		ferien = sqlTage;
		return true;
	}

	public boolean delFerienTage() {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();
			stmt.executeUpdate("delete from ferien where tag like '"
					+ (Calendar.getInstance().get(Calendar.YEAR) - 1) + "-%'");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		ferien.clear();
		return true;
	}

	public Map<String, Person> getPersonen() {
		return this.personen;
	}

	public Map<String, Abteilung> getAbteilungen() {
		return this.abteilungen;
	}

	public Map<String, Urlaub> getUrlaubstage() {
		return this.urlaubstage;
	}

	public Map<String, Gruppe> getGruppen() {
		return this.gruppen;
	}

	public Set<String> getAdmins() {
		return this.admins;
	}

	public Set<String> getFerien() {
		return this.ferien;
	}

	// public Set<String> getChangedBeantragt() {
	// return this.changedBeantragt;
	// }
	//
	// public Map<String, Set<String>> getChangedGenehmigt() {
	// return this.changedGenehmigt;
	// }

	public String getJahr() {
		return this.jahr;
	}

	public ArrayList<Integer> getFreieId() {
		return freieID;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String l) {
		this.lastLogin = l;
	}

	/**
	 * @return erste freie ID für den Urlaub
	 */
	public int getFreeID() {
		int result;
		if (this.freieID.isEmpty()) {
			result = this.letzteID;
			this.letzteID++;
		} else {
			result = this.freieID.get(0);
			this.freieID.remove(0);
		}
		return result;
	}

	public void returnFreeID(Integer i) {
		freieID.add(i);
	}

	public void dbSendChangeBeantragt(Set<String> set) {
		Connection con;
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();
			for (String s : set) {
				stmt.executeUpdate("insert into changesBeantragt(person, tag) values ('"
						+ person.getID() + "', '" + s + "')");
			}
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}// end catch
	}

	public void dbSendChangeGenehmigt(String personID, String tag) {
		Connection con;
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();
			stmt.executeUpdate("insert into changesGenehmigt(person, tag) values ('"
					+ personID + "', '" + tag + "')");

			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}// end catch
	}

	public boolean closeDB() {
		if (closeFreeID() && closeFreeGruppenID()) {
			// TODO sonstige close ups
			Connection con;
			try {
				PreparedStatement prepStmt;
				String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
				con = DriverManager.getConnection(url, "user", "user");
				if (admins.contains(person.getID())) {
					prepStmt = con
							.prepareStatement("UPDATE system SET lastlogin = ? WHERE system.system='system'");
					prepStmt.setString(1, lastLogin);
					prepStmt.execute();
				}
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}// end catch
			return true;
		} else
			return false;
	}

	private boolean closeFreeID() {
		if (freieID.isEmpty())
			return true;
		else {
			try {
				String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
				Connection con = DriverManager.getConnection(url, "user",
						"user");
				Statement stmt = con.createStatement();
				for (Integer i : freieID) {
					stmt.executeUpdate("INSERT INTO freieID (id) "
							+ "VALUES ('" + i.toString() + "')");
				}
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}// end catch
			return true;
		}
	}

	private boolean closeFreeGruppenID() {
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();
			for (Entry<String, Abteilung> a : abteilungen.entrySet()) {
				for (String i : a.getValue().getFreieID()) {
					stmt.executeUpdate("INSERT INTO freieGruppenID (id) "
							+ "VALUES ('" + i + "')");
				}
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}// end catch
		return true;
	}
}
