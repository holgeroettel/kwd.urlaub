package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class PlanPasswort extends JPanel implements KeyListener {
	public final static String CARDNAME = "password";

	private Inhalt inhalt;
	private JPasswordField feldPw = new JPasswordField(13);
	private JPasswordField feldPw2 = new JPasswordField(13);
	private boolean boolPw = true, boolPw2 = true;
	private PersNrVerifier inputVeri = new PersNrVerifier();

	public PlanPasswort(Inhalt i) {
		super(null);
		this.inhalt = i;
		JButton btnCheck = new JButton("Bestätigen");
		JButton btnBack = new JButton("Zurück");
		JLabel lblPw = new JLabel("Neuer PIN: ");
		JLabel lblPw2 = new JLabel("PIN wiederholen: ");

		feldPw.setInputVerifier(inputVeri);
		feldPw.addKeyListener(this);
		feldPw2.setInputVerifier(inputVeri);
		feldPw2.addKeyListener(this);

		btnCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commitPIN();
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
		this.add(btnBack);
		this.add(feldPw);
		this.add(lblPw2);
		this.add(feldPw2);
		this.add(btnCheck);
	}

	protected void commitPIN() {
		if (!verifyPw()) {
			JOptionPane.showMessageDialog(null,
					"Wiederholen Sie bitte den PIN.", "Ungleich",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			try {
				String url = "jdbc:mysql://147.54.81.70:3308/UrlaubDB";
				Connection con = DriverManager.getConnection(url, "user",
						"user");
				PreparedStatement prepStmt = con
						.prepareStatement("UPDATE kennwort SET kennwort.pass = ? WHERE kennwort.person='"
								+ inhalt.getPerson().getID() + "'");
				prepStmt.setString(1, String.valueOf(feldPw.getPassword()));
				prepStmt.execute();
				con.close();

				feldPw.setText("");
				feldPw.setBackground(Color.WHITE);
				boolPw = false;
				feldPw2.setText("");
				feldPw2.setBackground(Color.WHITE);
				boolPw2 = false;
				JOptionPane.showMessageDialog(null,
						"Ihr PIN wurde erfolgreich geändert.", "Erfolg",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
				return;
			}// end catch
		}
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
						String.valueOf(feldPw2.getPassword()))
				&& !String.valueOf(feldPw.getPassword()).isEmpty();
	}

	class PersNrVerifier extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			if (input == feldPw) {
				String s = String.valueOf(feldPw.getPassword());
				if (s.length() == 4 && s.matches("[0-9]*")) {
					feldPw.setBackground(Color.GREEN);
					boolPw = true;
					return true;
				} else {
					feldPw.setBackground(Color.PINK);
					boolPw = false;
					JOptionPane.showMessageDialog(null,
							"Geben Sie bitte eine PIN (vier Zahlen) an.",
							"PIN", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} else if (input == feldPw2) {
				String s = String.valueOf(feldPw2.getPassword());
				if (s.length() == 4 && s.matches("[0-9]*")) {
					feldPw2.setBackground(Color.GREEN);
					boolPw2 = true;
					return true;
				} else {
					feldPw2.setBackground(Color.PINK);
					boolPw2 = false;
					JOptionPane.showMessageDialog(null,
							"Geben Sie bitte nur vier Zahlen ein.", "PIN",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} else
				return false;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ((e.getSource() == feldPw2 || e.getSource() == feldPw)
				&& e.getKeyCode() == KeyEvent.VK_ENTER)
			commitPIN();
		if ((e.getSource() == feldPw2 || e.getSource() == feldPw)
				&& e.getKeyCode() == KeyEvent.VK_ESCAPE)
			inhalt.createHauptmenu();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
