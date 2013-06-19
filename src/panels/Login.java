package panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.Inhalt;
import daten.Person;

@SuppressWarnings("serial")
public class Login extends JPanel implements KeyListener{
	public final static String CARDNAME = "login";
	
	Inhalt inhalt;
	Person person = null;
	JPasswordField pwFeld;
	JTextField nrFeld;

	public Login(Inhalt i) {
		super(null);
		this.inhalt = i;

		JLabel willkommen = new JLabel("Willkommen zur Urlaubsplanung");
		this.add(willkommen);
		willkommen.setBounds(250, 210, 300, 26);

		JLabel nrLabel = new JLabel("Personalnummer: ");
		this.add(nrLabel);
		nrLabel.setBounds(250, 249, 150, 26);

		nrFeld = new JTextField(6);
		nrFeld.addKeyListener(this);
		this.add(nrFeld);
		nrFeld.setBounds(400, 249, 150, 26);

		JLabel pwLabel = new JLabel("Ihr Kennwort: ");
		this.add(pwLabel);
		pwLabel.setBounds(250, 275, 150, 26);

		pwFeld = new JPasswordField(8);
		pwFeld.addKeyListener(this);
		this.add(pwFeld);
		pwFeld.setBounds(400, 275, 150, 26);

		JButton loginButton = new JButton("Bestätigen");
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				login(nrFeld.getText(), String.valueOf(pwFeld.getPassword()));
			}
		});
		this.add(loginButton);
		loginButton.setBounds(400, 301, 150, 26);

		JButton beenden = new JButton("Beenden");
		beenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		this.add(beenden);
		beenden.setBounds(400, 327, 150, 26);
	}

	public void login(String nr, String pw) {
		if (validate(nr, pw)) {
			this.inhalt.setPerson(person);
			this.inhalt.createHauptmenu();
		}
	}

	private boolean validate(String nr, String pw) {
		boolean result = false;
		try {
			String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
			Connection con = DriverManager.getConnection(url, "user", "user");
			Statement stmt = con.createStatement();

			ResultSet rs = stmt
					.executeQuery("select * from personen where (personen.person = '"
							+ nr + "')");
			String kennwort = null;
			while (rs.next()) {
				person = new Person(rs.getString(1), rs.getString(2),
						rs.getString(3), new Integer(rs.getInt(4)),
						new Integer(rs.getInt(5)), new Integer(rs.getInt(6)),
						rs.getString(7), rs.getString(8));
			}// end while loop

			if (person == null) {
				JOptionPane
						.showMessageDialog(
								null,
								"Der eingegebene Nummer ist mir nicht bekannt. Legen Sie sie bitte vorher als Admin an.",
								"Person?", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			rs = stmt
					.executeQuery("select person, pass from kennwort where (kennwort.person = '"
							+ nr + "')");
			while (rs.next()) {
				kennwort = rs.getString(2);
			}// end while loop

			if (kennwort == null || kennwort.equals("")) {
				JOptionPane
						.showMessageDialog(
								null,
								"Sie wurden erst angelegt, bitte lassen Sie sich ein Passwort geben.",
								"Fehlendes PW", JOptionPane.ERROR_MESSAGE);
			} else if (kennwort.equals(pw)) {
				result = true;
			} else {
				JOptionPane.showMessageDialog(null, "Das Kennwort ist falsch.",
						"Falsche Eingabe", JOptionPane.ERROR_MESSAGE);
				;
			}
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}// end catch
		return result;
	}

	@Override
	public void keyPressed(KeyEvent e) {	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == pwFeld
				&& e.getKeyCode() == KeyEvent.VK_ENTER)
			login(nrFeld.getText(), String.valueOf(pwFeld.getPassword()));
		if ((e.getSource() == pwFeld || e.getSource() == nrFeld)
				&& e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
	}

	@Override
	public void keyTyped(KeyEvent e) {		
	}

}
