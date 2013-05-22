package panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.EinspaltenTableModel;
import main.Inhalt;
import daten.Abteilung;
import daten.Gruppe;
import daten.Person;

@SuppressWarnings("serial")
public class PlanGruppe extends JPanel {
	public final static String CARDNAME = "gruppe";

	private Inhalt inhalt;
	private Map<String, Set<String>> gruppen;
	private Abteilung meineAbteilung;
	private ArrayList<String> nichtGruppierteAngestellte;
	private EinspaltenTableModel<Gruppe> gruppenModel;
	private EinspaltenTableModel<Person> personenModel;
	private JComboBox<String> freiePersonen;
	private String gruppeAbteilungsleiter = Abteilung.getAbteilungIDFromString(
			Abteilung.CHEF).substring(0, 3)
			+ "01";

	public PlanGruppe(Inhalt i) {
		super(null);
		this.inhalt = i;
		this.gruppen = new TreeMap<String, Set<String>>();
		this.meineAbteilung = i.getDB().getAbteilungen()
				.get(Abteilung.getBezeichnung(i.getPerson().getAbteilung()));
		this.nichtGruppierteAngestellte = new ArrayList<String>();
		this.gruppenModel = new EinspaltenTableModel<Gruppe>("Gruppen:");
		initGruppen();

		JLabel ueberschrift = new JLabel("Abteilung: "
				+ meineAbteilung.getBez());
		this.add(ueberschrift);

		final JTable gruppenTable = new JTable(gruppenModel);
		gruppenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gruppenTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							personenModel.clear();
							if (gruppenTable.getSelectedRow() > -1) {
								Gruppe g = gruppenModel.getChange().get(
										gruppenTable.getSelectedRow());
								for (String s : gruppen.get(g.getID())) {
									Person p = inhalt.getDB().getPersonen()
											.get(s);
									personenModel.addChange(p);
								}
								if (g.getID().equals(gruppeAbteilungsleiter)) {
									System.out.println("mache nichts");
									return;
								}
							}
							if (!nichtGruppierteAngestellte.isEmpty()
									&& freiePersonen.getItemCount() == 0) {
								for (String s : nichtGruppierteAngestellte) {
									Person p = inhalt.getDB().getPersonen()
											.get(s);
									freiePersonen.addItem(p.getData());
								}
							}
						} else {
							;
						}
					}
				});
		gruppenTable
				.setPreferredScrollableViewportSize(new Dimension(200, 500));
		JScrollPane gruppenModelPane = new JScrollPane(gruppenTable);
		gruppenModelPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		gruppenModelPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(gruppenModelPane);

		JButton delGruppen = new JButton("Gruppe entfernen");
		delGruppen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Kann ich eine Gruppe entfernen?
				if (gruppenTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null,
							"Bitte selektieren Sie eine Gruppe.", "Auswahl",
							JOptionPane.CANCEL_OPTION);
					return;
				}
				if (gruppenModel.getChange().get(gruppenTable.getSelectedRow())
						.getID().equals("27101")) {
					JOptionPane
							.showMessageDialog(
									null,
									"Sie können diese Gruppe nicht löschen. Wenden Sie sich an einen Admin der IT wenn Sie es müssen",
									"Abteilungsleiter",
									JOptionPane.CANCEL_OPTION);
					return;
				} else {
					// Gruppe identifizieren.
					Gruppe g = gruppenModel.getChange().get(
							gruppenTable.getSelectedRow());
					Set<String> mitglieder = gruppen.get(g.getID());
					// Gruppe aus der Tabelle raus.
					gruppenModel.removeChange(g);
					// Gruppe lokal löschen
					for (String s : mitglieder) {
						nichtGruppierteAngestellte.add(s);
					}
					gruppen.remove(g.getID());
					// Gruppe in DB löschen
					inhalt.getDB().delGruppe(g);
				}
			}
		});
		this.add(delGruppen);

		final JTextField beschreibung = new JTextField("Gruppenbeschreibung",
				13);
		this.add(beschreibung);

		JButton hinzuGruppen = new JButton("Gruppe hinzufügen");
		hinzuGruppen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Kann ich eine Gruppe hinzufügen?
				if (beschreibung.getText().isEmpty()
						|| beschreibung.getText().equals("Gruppenbeschreibung")) {
					JOptionPane.showMessageDialog(null,
							"Geben Sie bitte eine sinnvolle Beschreibung an.",
							"Beschreibung", JOptionPane.CANCEL_OPTION);
					return;
				} else {
					// Gruppe identifizieren.
					Gruppe g = new Gruppe(meineAbteilung.getFreeID(),
							beschreibung.getText());
					beschreibung.setText("");
					// Gruppe in die Tabelle rein.
					gruppenModel.addChange(g);
					// Gruppe local vermerken
					gruppen.put(g.getID(), new TreeSet<String>());
					// Verschicken an die DB.
					inhalt.getDB().addGruppe(g);
				}
			}
		});
		this.add(hinzuGruppen);

		personenModel = new EinspaltenTableModel<Person>("Mitglieder:");
		final JTable personVonGruppeTable = new JTable(personenModel);
		personVonGruppeTable.setPreferredScrollableViewportSize(new Dimension(
				200, 500));
		personVonGruppeTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane personVonGruppeModelPane = new JScrollPane(
				personVonGruppeTable);
		personVonGruppeModelPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		personVonGruppeModelPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(personVonGruppeModelPane);

		JButton delPersonVonGruppe = new JButton("Mitglied entfernen");
		delPersonVonGruppe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Kann Mitglied entfernt werden?
				if (personVonGruppeTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null,
							"Makieren Sie bitte ein Mitglied der Gruppe.",
							"Auswahl", JOptionPane.CANCEL_OPTION);
					return;
				} else if (gruppenModel.getChange()
						.get(gruppenTable.getSelectedRow()).getID()
						.equals("27101")) {
					JOptionPane
							.showMessageDialog(
									null,
									"Sie können keine Abteilungsleiter löschen. Wenden Sie sich an einen Admin der IT wenn Sie es müssen.",
									"Abteilungsleiter",
									JOptionPane.CANCEL_OPTION);
					return;
				} else {
					// Mitglied identifizieren.
					Person p = personenModel.getChange().get(
							personVonGruppeTable.getSelectedRow());
					// Mitglied aus Tabelle raus, ins Dropdown
					personenModel.removeChange(p);
					freiePersonen.addItem(p.getData());
					// P ist jetzt in einer Gruppe.
					nichtGruppierteAngestellte.add(p.getID());
					Gruppe g = gruppenModel.getChange().get(
							gruppenTable.getSelectedRow());
					gruppen.get(g.getID()).remove(p.getID());
					// Verschicken an die DB.
					inhalt.getDB().delPerson(p);
					inhalt.getDB().addPerson(
							new Person(p.getNr(), p.getName(), p.getVorname(),
									p.getPosten(), p.getSoll(), p.getRest(), p
											.getAbteilung(), null));
				}
			}
		});
		this.add(delPersonVonGruppe);

		freiePersonen = new JComboBox<String>();
		this.add(freiePersonen);

		JButton hinzuPerson = new JButton("Mitglied hinzufügen");
		hinzuPerson.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Kann eine Person hinzugefügt werden?
				if (gruppenTable.getSelectedRow() == -1) {
					JOptionPane
							.showMessageDialog(
									null,
									"Wählen Sie bitte eine Gruppe, der Sie ein neues Mitglied hinzufügen wollen.",
									"Auswahl", JOptionPane.CANCEL_OPTION);
					return;
				}
				if (freiePersonen.getItemCount() == 0) {
					JOptionPane
							.showMessageDialog(
									null,
									"Es wurden bereits alle Mitarbeiter einer Gruppe zugewiesen.",
									"Zugeordnet", JOptionPane.CANCEL_OPTION);
					return;
				} else {
					Person p = null;
					// Person finden
					for (String s : nichtGruppierteAngestellte) {
						p = inhalt.getDB().getPersonen().get(s);
						if (p.getData().equals(
								(String) freiePersonen.getSelectedItem()))
							break;
					}
					// Person aus Dropdown heraus und in Tabelle daneben rein.
					personenModel.addChange(p);
					freiePersonen.removeItemAt(freiePersonen.getSelectedIndex());
					// Da p jetzt in einer Gruppe, übernehmen wir das
					nichtGruppierteAngestellte.remove(p.getID());
					Gruppe g = gruppenModel.getChange().get(
							gruppenTable.getSelectedRow());
					gruppen.get(g.getID()).add(p.getID());
					// und verschicken an die DB.
					inhalt.getDB().delPerson(p);
					inhalt.getDB().addPerson(
							new Person(p.getNr(), p.getName(), p.getVorname(),
									p.getPosten(), p.getSoll(), p.getRest(), p
											.getAbteilung(), g.getID()));
				}
			}
		});
		this.add(hinzuPerson);

		JButton back = new JButton("Zurück");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inhalt.createHauptmenu();
			}
		});
		this.add(back);

		ueberschrift.setBounds(250, 20, 300, 40);
		gruppenModelPane.setBounds(50, 60, 200, 500);
		beschreibung.setBounds(250, 60, 150, 26);
		hinzuGruppen.setBounds(250, 86, 150, 26);
		delGruppen.setBounds(250, 125, 150, 26);
		personVonGruppeModelPane.setBounds(400, 60, 200, 500);
		freiePersonen.setBounds(600, 60, 150, 26);
		hinzuPerson.setBounds(600, 86, 150, 26);
		delPersonVonGruppe.setBounds(600, 125, 150, 26);
		back.setBounds(600, 203, 150, 26);
	}

	private void initGruppen() {
		for (String s : this.meineAbteilung.getAngestellte()) {
			this.nichtGruppierteAngestellte.add(new String(s));
		}
		for (Entry<String, Gruppe> g : inhalt.getDB().getGruppen().entrySet()) {
			if (g.getKey().equals(gruppeAbteilungsleiter)
					&& Abteilung.CHEF.equals(meineAbteilung.getBez())) {
				System.out.println(Abteilung.getBezeichnung(g.getKey())
						+ " Nur Chef "
						+ Abteilung.getBezeichnung(meineAbteilung.getBezID()));
				Set<String> leiter = new TreeSet<String>();
				for (Entry<String, Abteilung> a : inhalt.getDB()
						.getAbteilungen().entrySet()) {
					if (a.getKey().equals(Abteilung.CHEF)) {
						continue;
					} else {
						for (String s : a.getValue().getChefs()) {
							leiter.add(s);
						}
					}
				}
				gruppen.put(g.getKey(), leiter);
				gruppenModel.addChange(g.getValue());
			} else if (Abteilung.getBezeichnung(g.getKey()).equals(
					Abteilung.getBezeichnung(meineAbteilung.getBezID()))) {
				gruppen.put(g.getKey(), new TreeSet<String>());
				gruppenModel.addChange(g.getValue());
			}
		}
		ArrayList<String> gruppierteAngestellte = new ArrayList<String>();
		for (String s : this.nichtGruppierteAngestellte) {
			String help = this.inhalt.getDB().getPersonen().get(s).getGruppe();
			if (help != null) {
				this.gruppen.get(help).add(s);
				gruppierteAngestellte.add(s);
			}
		}
		this.nichtGruppierteAngestellte.removeAll(gruppierteAngestellte);
		gruppierteAngestellte.clear();
		gruppierteAngestellte = null;
	}

	public ArrayList<String> getNichtGruppierteAngestellte() {
		return this.nichtGruppierteAngestellte;
	}

	public Abteilung getAbteilung() {
		return this.meineAbteilung;
	}

	public Map<String, Set<String>> getGruppen() {
		return this.gruppen;
	}

	public void open() {
		this.inhalt.getPanel().removeAll();
		this.inhalt.getPanel().add(this, BorderLayout.CENTER);
		this.inhalt.getPanel().validate();
		this.inhalt.getPanel().repaint();
	}

	public void back() {
		this.inhalt.createHauptmenu();
	}
}
