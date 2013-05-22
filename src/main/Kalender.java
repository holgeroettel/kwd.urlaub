package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Kalender implements ActionListener {
	private JFrame fenster;

	private JLabel lblMonth, lblYear;
	private JButton btnPrev, btnNext, btnOK, btnAuﬂen;
	private JTable tblCalendar;
	private JComboBox<String> cmbYear;
	private JFrame frmMain;
	private Container pane;
	private DefaultTableModel mtblCalendar; // Table model
	private JScrollPane stblCalendar; // The scrollpane
	private JPanel pnlCalendar;
	private int realYear, realMonth, realDay, currentYear, currentMonth;

	public Kalender(JButton btn) {
		this.btnAuﬂen = btn;
	}

	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	@Override
	public void actionPerformed(ActionEvent e) {
		// Prepare frame
		frmMain = new JFrame("Datum bestimmen"); // Create frame
		frmMain.setSize(330, 375); // Set size to 400x400 pixels
		frmMain.setLocationRelativeTo(fenster);
		pane = frmMain.getContentPane(); // Get content pane
		pane.setLayout(null); // Apply null layout

		// Create controls
		lblMonth = new JLabel("January");
		lblYear = new JLabel("Change year:");
		cmbYear = new JComboBox();
		btnPrev = new JButton("<<");
		btnNext = new JButton(">>");
		btnOK = new JButton("OK");
		mtblCalendar = new DefaultTableModel() {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};
		tblCalendar = new JTable(mtblCalendar);
		stblCalendar = new JScrollPane(tblCalendar);
		pnlCalendar = new JPanel(null);

		// Set border
		pnlCalendar.setBorder(BorderFactory.createTitledBorder("Calendar"));

		// Register action listeners
		btnPrev.addActionListener(new btnPrev_Action());
		btnNext.addActionListener(new btnNext_Action());
		btnOK.addActionListener(new datumWahlAction());
		cmbYear.addActionListener(new cmbYear_Action());

		// Add controls to pane
		pane.add(pnlCalendar);
		pnlCalendar.add(lblMonth);
		pnlCalendar.add(lblYear);
		pnlCalendar.add(cmbYear);
		pnlCalendar.add(btnPrev);
		pnlCalendar.add(btnNext);
		pnlCalendar.add(btnOK);
		pnlCalendar.add(stblCalendar);

		// Set bounds
		pnlCalendar.setBounds(0, 0, 320, 335);
		lblMonth.setBounds(160 - lblMonth.getPreferredSize().width / 2, 25,
				100, 25);
		lblYear.setBounds(10, 305, 80, 20);
		cmbYear.setBounds(100, 305, 80, 20);
		btnOK.setBounds(230, 305, 80, 20);
		btnPrev.setBounds(10, 25, 50, 25);
		btnNext.setBounds(260, 25, 50, 25);
		stblCalendar.setBounds(10, 50, 300, 250);

		// Make frame visible
		frmMain.setResizable(false);
		frmMain.setVisible(true);

		// Get real month/year
		GregorianCalendar cal = new GregorianCalendar(); // Create calendar
		realDay = cal.get(GregorianCalendar.DAY_OF_MONTH); // Get day
		realMonth = cal.get(GregorianCalendar.MONTH); // Get month
		realYear = cal.get(GregorianCalendar.YEAR); // Get year
		currentMonth = realMonth; // Match month and year
		currentYear = realYear;

		// Add headers
		String[] headers = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" }; // All
																				// headers
		for (int i = 0; i < 7; i++) {
			mtblCalendar.addColumn(headers[i]);
		}

		tblCalendar.getParent().setBackground(tblCalendar.getBackground()); // Set
																			// background

		// No resize/reorder
		tblCalendar.getTableHeader().setResizingAllowed(false);
		tblCalendar.getTableHeader().setReorderingAllowed(false);

		// Single cell selection
		tblCalendar.setColumnSelectionAllowed(true);
		tblCalendar.setRowSelectionAllowed(true);
		tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Set row/column count
		tblCalendar.setRowHeight(38);
		mtblCalendar.setColumnCount(7);
		mtblCalendar.setRowCount(6);

		// Populate table
		for (int i = realYear - 1; i <= realYear + 1; i++) {
			cmbYear.addItem(String.valueOf(i));
		}

		// Refresh calendar
		refreshCalendar(realMonth, realYear); // Refresh calendar
	}

	public void refreshCalendar(int month, int year) {
		// Variables
		String[] months = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
				"December" };
		int nod, som; // Number Of Days, Start Of Month

		// Allow/disallow buttons
		btnPrev.setEnabled(true);
		btnNext.setEnabled(true);
		if (month == 0 && year <= realYear) {
			btnPrev.setEnabled(false);
		} // Too early
		if (month == 0 && year >= realYear + 1) {
			btnNext.setEnabled(false);
		} // Too late
		lblMonth.setText(months[month]); // Refresh the month label (at the top)
		lblMonth.setBounds(160 - lblMonth.getPreferredSize().width / 2, 25,
				180, 25); // Re-align label with calendar
		cmbYear.setSelectedItem(String.valueOf(year)); // Select the correct
														// year in the combo box

		// Clear table
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				mtblCalendar.setValueAt(null, i, j);
			}
		}

		// Get first day of month and number of days
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		som = cal.get(GregorianCalendar.DAY_OF_WEEK);

		// Draw calendar
		for (int i = 1; i <= nod; i++) {
			int row = new Integer((i + som - 2) / 7);
			int column = (i + som - 2) % 7;
			mtblCalendar.setValueAt(i, row, column);
		}

		// Apply renderers
		tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0),
				new tblCalendarRenderer());
	}

	@SuppressWarnings("serial")
	class tblCalendarRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, selected,
					focused, row, column);
			if (column == 0 || column == 6) { // Week-end
				setBackground(new Color(255, 220, 220));
			} else { // Week
				setBackground(new Color(255, 255, 255));
			}
			if (value != null) {
				if (Integer.parseInt(value.toString()) == realDay
						&& currentMonth == realMonth && currentYear == realYear) { // Today
					setBackground(new Color(220, 220, 255));
				}
			}
			if (selected) {
				if (value != null) {
					setBackground(new Color(140, 140, 255));
					String monthString = "";
					String dayString = "";
					int month = currentMonth + 1;
					int day = Integer.parseInt(value.toString());
					if(month < 10)
						monthString += "0" + month;
					else
						monthString += month;
					if(day < 10)
						dayString += "0" + day;
					else
						dayString += day;
					btnAuﬂen.setText(currentYear + "-" + monthString
							+ "-" + dayString);
					btnOK.setEnabled(true);
				} else {
					btnOK.setEnabled(false);
				}
			}
			setBorder(null);
			setForeground(Color.black);
			return this;
		}
	}

	class btnPrev_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (currentMonth == 0) { // Back one year
				currentMonth = 11;
				currentYear -= 1;
			} else { // Back one month
				currentMonth -= 1;
			}
			refreshCalendar(currentMonth, currentYear);
		}
	}

	class btnNext_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (currentMonth == 11) { // Foward one year
				currentMonth = 0;
				currentYear += 1;
			} else { // Foward one month
				currentMonth += 1;
			}
			refreshCalendar(currentMonth, currentYear);
		}
	}

	class cmbYear_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (cmbYear.getSelectedItem() != null) {
				String b = cmbYear.getSelectedItem().toString();
				currentYear = Integer.parseInt(b);
				refreshCalendar(currentMonth, currentYear);
			}
		}
	}

	class datumWahlAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			frmMain.setVisible(false);
			frmMain.dispose();
		}
	}

}
