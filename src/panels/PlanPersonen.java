package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
import daten.AbteilungI;
import daten.Person;

@SuppressWarnings("serial")
public class PlanPersonen extends JPanel {
	public final static String CARDNAME = "personen";
	
	private Inhalt inhalt;
	private EinspaltenTableModel<Person> mitarbeiterModel;
	private String[] postenStrings = { "Mitarbeiter", "Stellvertreter",
			"Abteilungsleiter" , "Geschäftsführer"};
	private JTextField feldNr, feldName, feldVor, feldAbteilung, feldSoll,
			feldRest;
	private boolean verifiedNr, verifiedName, verifiedVor, verifiedSoll,
			verifiedRest, verifiedAbteilung;
	private JRadioButton radioAdmin;

	public PlanPersonen(Inhalt i) {
		super(null);
		this.inhalt = i;
		this.mitarbeiterModel = new EinspaltenTableModel<Person>("Mitarbeiter:");
		this.feldNr = new JTextField("000000", 13);
		this.feldName = new JTextField("Mustermann", 13);
		this.feldVor = new JTextField("Max", 13);
		this.feldSoll = new JTextField("30", 2);
		this.feldRest = new JTextField("30", 2);
		this.feldAbteilung = new JTextField("00000", 13);
		// this.feldFrist = new JTextField(inhalt.realYear + "-12-31");
		this.verifiedNr = false;
		this.verifiedName = false;
		this.verifiedVor = false;
		this.verifiedSoll = false;
		this.verifiedRest = false;
		this.verifiedAbteilung = false;
		this.radioAdmin = new JRadioButton("Admin?");
		JLabel lblUeberschrift = new JLabel("Personalplanung");
		JLabel lblID = new JLabel("Personalnummer: ");
		JLabel lblName = new JLabel("Name: ");
		JLabel lblVor = new JLabel("Vorname: ");
		JLabel lblSoll = new JLabel("UrlaubstageMax: ");
		JLabel lblRest = new JLabel("Resturlaub: ");
		JLabel lblAbteilung = new JLabel("Abteilung: ");
		JLabel lblPosten = new JLabel("Posten: ");
		final JComboBox<String> posten = new JComboBox<String>(postenStrings);
		final JTable mitarbeiterTable = new JTable(mitarbeiterModel);
		JScrollPane mitarbeiterTablePane = new JScrollPane(mitarbeiterTable);
		JButton btnHinzu = new JButton("Mitarbeiter hinzu");
		JButton btnDel = new JButton("Mitarbeiter entfernen");
		JButton btnBack = new JButton("Zurück");

		for (Entry<String, Person> p : inhalt.getDB().getPersonen().entrySet()) {
			mitarbeiterModel.addChange(p.getValue());
		}
		feldNr.setInputVerifier(new FeldVerifier());
		feldName.setInputVerifier(new FeldVerifier());
		feldVor.setInputVerifier(new FeldVerifier());
		feldAbteilung.setInputVerifier(new FeldVerifier());
		feldSoll.setInputVerifier(new FeldVerifier());
		feldRest.setInputVerifier(new FeldVerifier());

		mitarbeiterTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							if (mitarbeiterTable.getSelectedRow() > -1) {
								Person p = mitarbeiterModel.getChange().get(
										mitarbeiterTable.getSelectedRow());
								feldNr.setText(p.getID());
								feldName.setText(p.getName());
								feldVor.setText(p.getVorname());
								feldSoll.setText(p.getSoll().toString());
								feldRest.setText(p.getRest().toString());
								feldAbteilung.setText(p.getAbteilung());
								posten.setSelectedIndex(p.getPosten()
										.intValue());
								radioAdmin.setSelected(inhalt.getDB()
										.getAdmins().contains(p.getID()));
							}
						} else {
							return;
						}
					}
				});
		mitarbeiterTable.setPreferredScrollableViewportSize(new Dimension(200,
				500));
		mitarbeiterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mitarbeiterTablePane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mitarbeiterTablePane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		btnHinzu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Kann ich eine Person hinzufügen?
				if (!verifiedNr) {
					JOptionPane
							.showMessageDialog(
									null,
									"Die eingegebene Personalnummer konnte nicht verifiziert werden.",
									"Personallnummer?",
									JOptionPane.CANCEL_OPTION);
					return;
				}
				if (!verifiedName) {
					JOptionPane
							.showMessageDialog(
									null,
									"Der eingegebene Name konnte nicht verifiziert werden.",
									"Name?", JOptionPane.CANCEL_OPTION);
					return;
				}
				if (!verifiedVor) {
					JOptionPane
							.showMessageDialog(
									null,
									"Der eingegebene Vorname konnte nicht verifiziert werden.",
									"Vorname?", JOptionPane.CANCEL_OPTION);
					return;
				}
				if (!verifiedAbteilung) {
					JOptionPane
							.showMessageDialog(
									null,
									"Die eingegebene Abteilung konnte nicht verifiziert werden.",
									"Abteilung?", JOptionPane.CANCEL_OPTION);
					return;

				}
				if (!verifiedSoll) {
					JOptionPane
							.showMessageDialog(
									null,
									"Die Urlaubstage im Jahr wurden nicht verifiziert.",
									"Jahresurlaubstage",
									JOptionPane.CANCEL_OPTION);
					return;
				}
				if (!verifiedRest) {
					JOptionPane.showMessageDialog(null,
							"Wieviele Urlaubstag hat er/sie noch?", "Hat",
							JOptionPane.CANCEL_OPTION);
					return;
				} else {
					// Person identifizieren.
					Person p = new Person(feldNr.getText(), feldName.getText(),
							feldVor.getText(), new Integer(posten
									.getSelectedIndex()), Integer
									.parseInt(feldSoll.getText()), Integer
									.parseInt(feldRest.getText()), feldAbteilung
									.getText(), null);
					if (inhalt.getDB().getPersonen().containsKey(p.getID())) {
						JOptionPane
								.showMessageDialog(
										null,
										"Die Eingegebene Personalnummer existiert bereits.",
										"Dopplung", JOptionPane.CANCEL_OPTION);
						return;
					}
					feldNr.setText("");
					feldNr.setBackground(Color.WHITE);
					verifiedNr = false;
					feldName.setText("");
					feldName.setBackground(Color.WHITE);
					verifiedName = false;
					feldVor.setText("");
					feldVor.setBackground(Color.WHITE);
					verifiedVor = false;
					feldAbteilung.setText("");
					feldAbteilung.setBackground(Color.WHITE);
					verifiedAbteilung = false;
					feldSoll.setText("");
					feldSoll.setBackground(Color.WHITE);
					verifiedSoll = false;
					feldRest.setText("");
					feldRest.setBackground(Color.WHITE);
					verifiedRest = false;
					// Person in die Tabelle rein.
					mitarbeiterModel.addChange(p);
					// Verschicken an die DB.
					inhalt.getDB().addPerson(p);
					// Wenn Admin?
					if (radioAdmin.isSelected())
						inhalt.getDB().addAdmin(p.getID());
				}
			}
		});
		btnDel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Kann ich eine Person entfernen?
				if (mitarbeiterTable.getSelectedRow() == -1) {
					JOptionPane
							.showMessageDialog(
									null,
									"Wählen Sie bitte einen Mitarbeiter zum Löschen aus.",
									"Auswahl", JOptionPane.CANCEL_OPTION);
					return;
				} else {
					// Person identifizieren.
					Person p = mitarbeiterModel.getChange().get(
							mitarbeiterTable.getSelectedRow());
					boolean boolAdmin = inhalt.getDB().getAdmins()
							.contains(p.getID());
					// Ist Person der letzte Admin?
					if (inhalt.getDB().getAdmins().size() == 1 && boolAdmin) {
						JOptionPane
								.showMessageDialog(
										null,
										"Sie müssen erst einen neuen Admin erstellen, bevor Sie den Letzten löschen können.",
										"Letzter Admin",
										JOptionPane.CANCEL_OPTION);
						return;
					}
					// Person aus der Tabelle raus.
					mitarbeiterModel.removeChange(p);
					// Person in DB löschen
					inhalt.getDB().delPerson(p);
					// Wenn Admin?
					if (boolAdmin)
						inhalt.getDB().delAdmin(p.getID());
				}
			}
		});
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inhalt.createHauptmenu();
			}
		});

		this.add(mitarbeiterTablePane);
		this.add(lblUeberschrift);
		this.add(lblID);
		this.add(lblName);
		this.add(lblVor);
		this.add(lblSoll);
		this.add(lblRest);
		this.add(lblAbteilung);
		this.add(lblPosten);
		this.add(radioAdmin);
		this.add(btnHinzu);
		this.add(feldNr);
		this.add(feldName);
		this.add(feldVor);
		this.add(feldSoll);
		this.add(feldRest);
		this.add(feldAbteilung);
		this.add(posten);
		this.add(btnDel);
		this.add(btnBack);

		mitarbeiterTablePane.setBounds(100, 20, 200, 560);
		lblUeberschrift.setBounds(305, 20, 150, 26);
		lblID.setBounds(305, 72, 150, 26);
		feldNr.setBounds(455, 72, 160, 26);
		lblName.setBounds(305, 111, 150, 26);
		feldName.setBounds(455, 111, 160, 26);
		lblVor.setBounds(305, 150, 150, 26);
		feldVor.setBounds(455, 150, 160, 26);
		lblSoll.setBounds(305, 189, 150, 26);
		feldSoll.setBounds(455, 189, 160, 26);
		lblRest.setBounds(305, 228, 150, 26);
		feldRest.setBounds(455, 228, 160, 26);
		lblAbteilung.setBounds(305, 267, 150, 26);
		feldAbteilung.setBounds(455, 267, 160, 26);
		lblPosten.setBounds(305, 306, 150, 26);
		posten.setBounds(455, 306, 160, 26);
		radioAdmin.setBounds(455, 345, 160, 26);
		btnHinzu.setBounds(305, 384, 150, 26);
		btnDel.setBounds(455, 384, 160, 26);
		btnBack.setBounds(455, 423, 160, 26);

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

	private class FeldVerifier extends InputVerifier {
		public boolean verify(JComponent input) {
			if (input == feldNr) {
				String s = feldNr.getText();
				if (s.length() == 6 && s.matches("\\d*")
						&& Integer.parseInt(s.substring(0, 1)) != 0) {
					feldNr.setBackground(Color.GREEN);
					verifiedNr = true;
					return true;
				} else {
					feldNr.setBackground(Color.PINK);
					verifiedNr = false;
					JOptionPane
							.showMessageDialog(
									null,
									"Geben Sie bitte eine gültige Personalnummer an (sechs Zahlen, keine Führende 0).",
									"Falsche Personalnummer",
									JOptionPane.CANCEL_OPTION);
					return false;
				}
			} else if (input == feldName) {
				String s = feldName.getText();
				if (s.matches("[[\\p{Alpha}äöüÄÖÜ]+[\\x20\\x2D\\x5F\\p{Alpha}äöüÄÖÜ]*]+")) {
					feldName.setBackground(Color.GREEN);
					verifiedName = true;
					return true;
				} else {
					feldName.setBackground(Color.PINK);
					verifiedName = false;
					JOptionPane
							.showMessageDialog(
									null,
									"Geben Sie bitte eine gültigen Namen an (keine Zahl und mit Leerzeichen oder Bindestrich).",
									"Falsche Eingabe",
									JOptionPane.CANCEL_OPTION);
					return false;
				}
			} else if (input == feldVor) {
				String s = feldVor.getText();
				if (s.matches("[[\\p{Alpha}äöüÄÖÜ]+[\\x20\\x2D\\x5F\\p{Alpha}äöüÄÖÜ]*]+")) {
					feldVor.setBackground(Color.GREEN);
					verifiedVor = true;
					return true;
				} else {
					feldVor.setBackground(Color.PINK);
					verifiedVor = false;
					JOptionPane
							.showMessageDialog(
									null,
									"Geben Sie bitte eine gültigen Namen an (keine Zahl und mit Leerzeichen oder Bindestrich).",
									"Falsche Eingabe",
									JOptionPane.CANCEL_OPTION);
					return false;
				}
			} else if (input == feldSoll) {
				String s = feldSoll.getText();
				if (s.matches("\\d\\d")) {
					feldSoll.setBackground(Color.GREEN);
					verifiedSoll = true;
					return true;
				} else {
					feldSoll.setBackground(Color.PINK);
					verifiedSoll = false;
					JOptionPane.showMessageDialog(null,
							"Geben Sie bitte eine gültige Zahl an (XX).",
							"Falsche Eingabe", JOptionPane.CANCEL_OPTION);
					return false;
				}
			} else if (input == feldRest) {
				String s = feldRest.getText();
				if (s.matches("\\d\\d")||s.matches("\\d")) {
					feldRest.setBackground(Color.GREEN);
					verifiedRest = true;
					return true;
				} else {
					feldRest.setBackground(Color.PINK);
					verifiedRest = false;
					JOptionPane.showMessageDialog(null,
							"Geben Sie bitte eine gültige Zahl an (XX).",
							"Falsche Eingabe", JOptionPane.CANCEL_OPTION);
					return false;
				}
			} else if (input == feldAbteilung) {
				String s = feldAbteilung.getText();
				if (s.length() == 5
						&& s.matches("\\d*")
						&& !(Abteilung.getBezeichnung(s)
								.equals(Abteilung.UNBEKANNT))) {
					feldAbteilung.setBackground(Color.GREEN);
					verifiedAbteilung = true;
					return true;
				} else {
					feldAbteilung.setBackground(Color.PINK);
					verifiedAbteilung = false;
					String abteilungen = "";
					for (String a : AbteilungI.ABTEILUNGIDS) {
						abteilungen = abteilungen + a + "; ";
					}
					JOptionPane.showMessageDialog(null,
							"Geben Sie bitte eine mir bekannt Abteilung an ("
									+ abteilungen + ").",
							"Unbekannte Abteilung", JOptionPane.CANCEL_OPTION);
					return false;
				}
			} else
				return false;
		}
	}
}
