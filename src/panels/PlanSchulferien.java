package panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import main.Inhalt;
import main.Kalender;

public class PlanSchulferien {
	private Inhalt inhalt;
	private Set<String> tage;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public PlanSchulferien(Inhalt i) {
		this.inhalt = i;
		final JFrame frame = new JFrame("Willkommen im neuen Jahr Admin");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		JPanel panelOben = (JPanel) frame.getContentPane();
		panelOben.setLayout(new BorderLayout());

		JPanel panel = new JPanel(null);
		panel.setBounds(0, 0, 500, 326);
		JLabel headLine = new JLabel("Schulferien");
		headLine.setHorizontalAlignment(SwingConstants.RIGHT);
		headLine.setBounds(0, 13, 160, 26);
		JLabel headVon = new JLabel("Von");
		headVon.setHorizontalAlignment(SwingConstants.CENTER);
		headVon.setBounds(160, 13, 160, 26);
		JLabel headBis = new JLabel("Bis");
		headBis.setHorizontalAlignment(SwingConstants.CENTER);
		headBis.setBounds(320, 13, 160, 26);
		JLabel winter = new JLabel("Winter: ");
		winter.setHorizontalAlignment(SwingConstants.RIGHT);
		winter.setBounds(0, 52, 160, 26);
		JLabel ostern = new JLabel("Ostern: ");
		ostern.setHorizontalAlignment(SwingConstants.RIGHT);
		ostern.setBounds(0, 91, 160, 26);
		JLabel pfingsten = new JLabel("Pfingsten: ");
		pfingsten.setHorizontalAlignment(SwingConstants.RIGHT);
		pfingsten.setBounds(0, 130, 160, 26);
		JLabel sommer = new JLabel("Sommer: ");
		sommer.setHorizontalAlignment(SwingConstants.RIGHT);
		sommer.setBounds(0, 169, 160, 26);
		JLabel herbst = new JLabel("Herbst: ");
		herbst.setHorizontalAlignment(SwingConstants.RIGHT);
		herbst.setBounds(0, 208, 160, 26);
		JLabel weihnachten = new JLabel("Weihnachten: ");
		weihnachten.setHorizontalAlignment(SwingConstants.RIGHT);
		weihnachten.setBounds(0, 247, 160, 26);

		final JButton winterVon = new JButton("klick");
		winterVon.addActionListener(new Kalender(winterVon));
		winterVon.setBounds(160, 52, 160, 26);
		final JButton winterBis = new JButton("klick");
		winterBis.addActionListener(new Kalender(winterBis));
		winterBis.setBounds(320, 52, 160, 26);
		final JButton osternVon = new JButton("klick");
		osternVon.addActionListener(new Kalender(osternVon));
		osternVon.setBounds(160, 91, 160, 26);
		final JButton osternBis = new JButton("klick");
		osternBis.addActionListener(new Kalender(osternBis));
		osternBis.setBounds(320, 91, 160, 26);
		final JButton pfingstenVon = new JButton("klick");
		pfingstenVon.addActionListener(new Kalender(pfingstenVon));
		pfingstenVon.setBounds(160, 130, 160, 26);
		final JButton pfingstenBis = new JButton("klick");
		pfingstenBis.addActionListener(new Kalender(pfingstenBis));
		pfingstenBis.setBounds(320, 130, 160, 26);
		final JButton sommerVon = new JButton("klick");
		sommerVon.addActionListener(new Kalender(sommerVon));
		sommerVon.setBounds(160, 169, 160, 26);
		final JButton sommerBis = new JButton("klick");
		sommerBis.addActionListener(new Kalender(sommerBis));
		sommerBis.setBounds(320, 169, 160, 26);
		final JButton herbstVon = new JButton("klick");
		herbstVon.addActionListener(new Kalender(herbstVon));
		herbstVon.setBounds(160, 208, 160, 26);
		final JButton herbstBis = new JButton("klick");
		herbstBis.addActionListener(new Kalender(herbstBis));
		herbstBis.setBounds(320, 208, 160, 26);
		final JButton weihnachtenVon = new JButton("klick");
		weihnachtenVon.addActionListener(new Kalender(weihnachtenVon));
		weihnachtenVon.setBounds(160, 247, 160, 26);
		final JButton weihnachtenBis = new JButton("klick");
		weihnachtenBis.addActionListener(new Kalender(weihnachtenBis));
		weihnachtenBis.setBounds(320, 247, 160, 26);
		final JButton commit = new JButton("Einpflegen");
		commit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!valid()) {
					JOptionPane.showMessageDialog(null,
							"Sie haben eine leere oder ungültige Angabe.",
							"Alle Tage gewählt?", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					inhalt.getDB().delFerienTage();
					inhalt.getDB().addFerienTage(tage);
					frame.setVisible(false);
					frame.dispose();
				}
			}

			private boolean valid() {
				// Winter
				String vonString = winterVon.getText();
				GregorianCalendar vonCal = new GregorianCalendar(Integer
						.parseInt(vonString.substring(0, 4)), Integer
						.parseInt(vonString.substring(5, 7)) - 1, Integer
						.parseInt(vonString.substring(8)));
				String bisString = winterBis.getText();
				GregorianCalendar bisCal = new GregorianCalendar(Integer
						.parseInt(bisString.substring(0, 4)), Integer
						.parseInt(bisString.substring(5, 7)) - 1, Integer
						.parseInt(bisString.substring(8)));
				if (vonCal.getTimeInMillis() > bisCal.getTimeInMillis())
					return false;
				else {
					tage = new TreeSet<String>();
					addFeierTage(vonCal, bisCal);
				}
				// Ostern
				vonString = osternVon.getText();
				vonCal = new GregorianCalendar(Integer.parseInt(vonString
						.substring(0, 4)), Integer.parseInt(vonString
						.substring(5, 7)) - 1, Integer.parseInt(vonString
						.substring(8)));
				bisString = osternBis.getText();
				bisCal = new GregorianCalendar(Integer.parseInt(bisString
						.substring(0, 4)), Integer.parseInt(bisString
						.substring(5, 7)) - 1, Integer.parseInt(bisString
						.substring(8)));
				if (vonCal.getTimeInMillis() > bisCal.getTimeInMillis())
					return false;
				else {
					addFeierTage(vonCal, bisCal);
				}
				// Pfingsten
				vonString = pfingstenVon.getText();
				vonCal = new GregorianCalendar(Integer.parseInt(vonString
						.substring(0, 4)), Integer.parseInt(vonString
						.substring(5, 7)) - 1, Integer.parseInt(vonString
						.substring(8)));
				bisString = pfingstenBis.getText();
				bisCal = new GregorianCalendar(Integer.parseInt(bisString
						.substring(0, 4)), Integer.parseInt(bisString
						.substring(5, 7)) - 1, Integer.parseInt(bisString
						.substring(8)));
				if (vonCal.getTimeInMillis() > bisCal.getTimeInMillis())
					return false;
				else {
					addFeierTage(vonCal, bisCal);
				}
				// Sommer
				vonString = sommerVon.getText();
				vonCal = new GregorianCalendar(Integer.parseInt(vonString
						.substring(0, 4)), Integer.parseInt(vonString
						.substring(5, 7)) - 1, Integer.parseInt(vonString
						.substring(8)));
				bisString = sommerBis.getText();
				bisCal = new GregorianCalendar(Integer.parseInt(bisString
						.substring(0, 4)), Integer.parseInt(bisString
						.substring(5, 7)) - 1, Integer.parseInt(bisString
						.substring(8)));
				if (vonCal.getTimeInMillis() > bisCal.getTimeInMillis())
					return false;
				else {
					addFeierTage(vonCal, bisCal);
				}
				// Herbst
				vonString = herbstVon.getText();
				vonCal = new GregorianCalendar(Integer.parseInt(vonString
						.substring(0, 4)), Integer.parseInt(vonString
						.substring(5, 7)) - 1, Integer.parseInt(vonString
						.substring(8)));
				bisString = herbstBis.getText();
				bisCal = new GregorianCalendar(Integer.parseInt(bisString
						.substring(0, 4)), Integer.parseInt(bisString
						.substring(5, 7)) - 1, Integer.parseInt(bisString
						.substring(8)));
				if (vonCal.getTimeInMillis() > bisCal.getTimeInMillis())
					return false;
				else {
					addFeierTage(vonCal, bisCal);
				}
				// Weihnachten
				vonString = weihnachtenVon.getText();
				vonCal = new GregorianCalendar(Integer.parseInt(vonString
						.substring(0, 4)), Integer.parseInt(vonString
						.substring(5, 7)) - 1, Integer.parseInt(vonString
						.substring(8)));
				bisString = weihnachtenBis.getText();
				bisCal = new GregorianCalendar(Integer.parseInt(bisString
						.substring(0, 4)), Integer.parseInt(bisString
						.substring(5, 7)) - 1, Integer.parseInt(bisString
						.substring(8)));
				if (vonCal.getTimeInMillis() > bisCal.getTimeInMillis())
					return false;
				else {
					addFeierTage(vonCal, bisCal);
				}
				return true;
			}

			private void addFeierTage(GregorianCalendar von,
					GregorianCalendar bis) {
				while (von.getTimeInMillis() <= bis.getTimeInMillis()) {
					tage.add(sdf.format(von.getTime()));
					von.add(GregorianCalendar.DAY_OF_YEAR, 1);
				}
			}
		});
		commit.setBounds(240, 286, 160, 26);

		panel.add(headLine);
		panel.add(headVon);
		panel.add(headBis);
		panel.add(winter);
		panel.add(winterVon);
		panel.add(winterBis);
		panel.add(ostern);
		panel.add(osternVon);
		panel.add(osternBis);
		panel.add(pfingsten);
		panel.add(pfingstenVon);
		panel.add(pfingstenBis);
		panel.add(sommer);
		panel.add(sommerVon);
		panel.add(sommerBis);
		panel.add(herbst);
		panel.add(herbstVon);
		panel.add(herbstBis);
		panel.add(weihnachten);
		panel.add(weihnachtenVon);
		panel.add(weihnachtenBis);
		panel.add(commit);

		panelOben.add(panel, BorderLayout.CENTER);
		frame.validate();
		frame.setSize(new Dimension(500, 350));
		frame.setVisible(true);

		panel.validate();
		panel.repaint();
	}
}
