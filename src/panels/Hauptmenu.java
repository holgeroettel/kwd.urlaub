package panels;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Inhalt;
import daten.Person;

@SuppressWarnings("serial")
public class Hauptmenu extends JPanel {
	public final static String CARDNAME = "haupt";

	private Inhalt inhalt;

	public Hauptmenu(Inhalt i) {
		super(null);
		inhalt = i;
		Person p = inhalt.getPerson();
		ArrayList<JButton> liste = new ArrayList<JButton>();

		GregorianCalendar heute = new GregorianCalendar();
		String l = inhalt.getDB().getLastLogin();
		GregorianCalendar lastLogin = new GregorianCalendar(Integer.parseInt(l
				.substring(0, 4)), (Integer.parseInt(l.substring(5, 7)) - 1),
				Integer.parseInt(l.substring(8)));
		if (inhalt.getDB().getAdmins().contains(p.getID())
				&& heute.get(GregorianCalendar.DAY_OF_YEAR) < lastLogin
						.get(GregorianCalendar.DAY_OF_YEAR)) {
			new PlanSchulferien(i);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		inhalt.getDB().setLastLogin(sdf.format(heute.getTime()));

		JLabel willkommen = new JLabel("Willkommen: " + p.getName() + ", "
				+ p.getVorname());
		this.add(willkommen);
		willkommen.setBounds(250, 210, 300, 26);

		JButton urlaubPlanen = new JButton("Urlaub planen");
		urlaubPlanen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inhalt.createPlan();
			}
		});
		liste.add(urlaubPlanen);

		if (p.getPosten() != 0) {
			JButton urlaubGen = new JButton("Urlaub genehmigen");
			urlaubGen.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					inhalt.createGenehmigung();
				}
			});
			liste.add(urlaubGen);

			JButton gruppen = new JButton("Gruppen der Abteilung");
			gruppen.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					inhalt.createGruppen();
				}
			});
			liste.add(gruppen);

			JButton auswertung = new JButton("Auswertung anzeigen");
			auswertung.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					inhalt.createAuswertung();
				}
			});
			if (p.getPosten() == 3)
				liste.add(auswertung);
		}

		if (i.getDB().getAdmins().contains(p.getNr())) {
			JButton personen = new JButton("Personal bearbeiten");
			personen.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					inhalt.createPersonal();
				}
			});
			liste.add(personen);
		}

		JButton pass = new JButton("PIN ändern");
		pass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inhalt.createPasswort();
			}
		});
		liste.add(pass);

		JButton hilfe = new JButton("Hilfe");
		hilfe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					URL url = new URL("http://147.54.81.70/hilfe.pdf");
					Desktop.getDesktop().browse(url.toURI());
				} catch (IOException | URISyntaxException e1) {
					System.out.println(e1.getMessage());
				}
			}
		});
		liste.add(hilfe);

		JButton beenden = new JButton("Beenden");
		beenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (inhalt.getDB().closeDB())
					System.exit(0);
				else
					System.exit(255);
			}
		});
		liste.add(beenden);

		int idx = 0, idx2 = 0;
		for (JButton b : liste) {
			b.setBounds((240 + 160 * idx), (249 + 26 * idx2), 160, 26);
			if (idx == 0)
				idx = 1;
			else {
				idx = 0;
				idx2++;
			}
			this.add(b);
		}
	}

	public void open() {
		this.inhalt.getPanel().removeAll();
		this.inhalt.getPanel().add(this, BorderLayout.CENTER);
		this.inhalt.getPanel().revalidate();
		this.inhalt.getPanel().repaint();
	}

}
