package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import main.Inhalt;

@SuppressWarnings("serial")
public class PlanPasswort extends JPanel {
	public final static String CARDNAME = "password";
	
	private Inhalt inhalt;
	private JPasswordField feldPw = new JPasswordField(13);
	private JPasswordField feldPw2 = new JPasswordField(13);
	private boolean boolPw = false, boolPw2 = false;
	private PersNrVerifier inputVeri = new PersNrVerifier();

	public PlanPasswort(Inhalt i) {
		super(null);
		this.inhalt = i;
		JButton btnCheck = new JButton("Bestätigen");
		JButton btnBack = new JButton("Zurück");
		JLabel lblPw = new JLabel("Neues Kennwort: ");
		JLabel lblPw2 = new JLabel("Kennwort wiederholen: ");

		feldPw.setInputVerifier(inputVeri);
		feldPw2.setInputVerifier(inputVeri);

		btnCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!verifyPw()) {
					JOptionPane.showMessageDialog(null,
							"Geben sie bitte zwei gleiche Kennwörte ein.",
							"Ungleich", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					try {
						String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
						Connection con = DriverManager.getConnection(url,
								"user", "user");
						PreparedStatement prepStmt = con
								.prepareStatement("UPDATE kennwort SET kennwort.pass = ? WHERE kennwort.person='"
										+ inhalt.getPerson().getID() + "'");
						prepStmt.setString(1,
								String.valueOf(feldPw.getPassword()));
						prepStmt.execute();
						con.close();

						feldPw.setText("");
						feldPw.setBackground(Color.WHITE);
						boolPw = false;
						feldPw2.setText("");
						feldPw2.setBackground(Color.WHITE);
						boolPw2 = false;
						JOptionPane.showMessageDialog(null,
								"Ihr Kennwort wurde erfolgreich geändert.",
								"Erfolg", JOptionPane.INFORMATION_MESSAGE);
					} catch (SQLException e1) {
						System.out.println(e1.getMessage());
						return;
					}// end catch
				}
			}
		});
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inhalt.createHauptmenu();
			}
		});

		lblPw.setBounds(240, 249, 160, 26);
		feldPw.setBounds(400, 249, 160, 26);
		lblPw2.setBounds(240, 275, 160, 26);
		feldPw2.setBounds(400, 275, 160, 26);
		btnCheck.setBounds(400, 301, 160, 26);
		btnBack.setBounds(400, 327, 160, 26);

		this.add(lblPw);
		this.add(feldPw);
		this.add(lblPw2);
		this.add(feldPw2);
		this.add(btnCheck);
		this.add(btnBack);
	}

	public void open() {
		this.inhalt.getPanel().removeAll();
		this.inhalt.getPanel().add(this, BorderLayout.CENTER);
		this.inhalt.getPanel().validate();
		this.inhalt.getPanel().repaint();
	}

	public boolean verifyPw() {
		return boolPw
				&& boolPw2
				&& String.valueOf(feldPw.getPassword()).equals(
						String.valueOf(feldPw2.getPassword()));
	}

	class PersNrVerifier extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			if (input == feldPw) {
				String s = String.valueOf(feldPw.getPassword());
				if (s.length() > 5 && s.length() < 21
						&& s.matches("^[\\p{L}0-9]*$")) {
					feldPw.setBackground(Color.GREEN);
					boolPw = true;
					return true;
				} else {
					feldPw.setBackground(Color.PINK);
					boolPw = false;
					JOptionPane
							.showMessageDialog(
									null,
									"Geben Sie bitte eine Alphanumerischen String von mindestens sechs, maximal aber 20 Zeichen an.",
									"Kein Kennwort", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} else if (input == feldPw2) {
				String s = String.valueOf(feldPw2.getPassword());
				if (s.length() > 5 && s.length() < 21
						&& s.matches("^[\\p{L}0-9]*$")) {
					feldPw2.setBackground(Color.GREEN);
					boolPw2 = true;
					return true;
				} else {
					feldPw2.setBackground(Color.PINK);
					boolPw2 = false;
					JOptionPane
							.showMessageDialog(
									null,
									"Geben Sie bitte eine Alphanumerischen String von mindestens sechs, maximal aber 20 Zeichen an.",
									"Kein Kennwort", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} else
				return false;
		}
	}
}
