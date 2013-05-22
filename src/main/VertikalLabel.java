package main;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class VertikalLabel extends JPanel {
	private int height;
	private int width = 15;
	private String text;

	public VertikalLabel(String text) {
		super(null);
		this.height = this.getFont().getSize();
		this.text = text;
		printLabel();
	}

	private void printLabel() {
		char helpArray[] = text.toCharArray();
		for (int idx = 0; idx < helpArray.length; idx++) {
			JLabel help = new JLabel(String.valueOf(helpArray[idx]));
			help.setHorizontalAlignment(SwingConstants.CENTER);
			help.setBounds(0, idx * height, width, height);
			help.setOpaque(true);
			help.setBackground(Color.WHITE);
			this.add(help);
		}
		this.validate();
	}

	public void setBounds(int x, int y) {
		this.setBounds(x, y, width, text.length() * height);
	}
}
