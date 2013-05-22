package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import main.Inhalt;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import daten.AbteilungI;

@SuppressWarnings("serial")
public class PlanAuswertung extends JPanel implements ActionListener {
	public final static String CARDNAME = "auswertung";
	public final static String SINGLE = "Abteilungen";
	public final static String ALL = "Gesamt";
	public final static String ALLLAST = "Gesamt Vorjahr";

	private Inhalt inhalt;
	private String[] months = { "Januar", "Februar", "März", "April", "Mai",
			"Juni", "Juli", "August", "September", "Oktober", "November",
			"Dezember" };
	private Map<String, XYSeries> abteilungsWerte = new TreeMap<String, XYSeries>();
	private Map<String, XYSeries> jahresWerte = new TreeMap<String, XYSeries>();
	private JButton heading = new JButton(SINGLE);
	private XYSeriesCollection xyDataset = new XYSeriesCollection();
	private Map<String, JCheckBox> abteilungsBoxen = new TreeMap<String, JCheckBox>();

	public PlanAuswertung(Inhalt i) {
		super(null);
		this.inhalt = i;

		heading.setHorizontalAlignment(SwingConstants.CENTER);
		heading.setBounds(20, 13, 160, 26);
		heading.addActionListener(this);
		this.add(heading);

		int idx = 0;
		for (String s : AbteilungI.ABTEILUNGEN) {
			JCheckBox box = new JCheckBox(s);
			box.setSelected(true);
			box.addActionListener(this);
			box.setBounds(20, 52 + 26 * idx, 160, 26);
			this.add(box);
			abteilungsBoxen.put(s, box);
			idx++;
		}

		JButton back = new JButton("Zurück");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inhalt.createHauptmenu();
			}
		});
		back.setBounds(20, 65 + AbteilungI.ABTEILUNGEN.length * 26, 160, 26);
		this.add(back);

		JPanel bild = create();
		JScrollPane scrollBild = new JScrollPane(bild);
		scrollBild
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollBild
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollBild.setBorder(BorderFactory.createEmptyBorder());
		scrollBild.setBounds(180, 0, 844, 768);
		this.add(scrollBild);
	}

	public JPanel create() {
		String sqlJahr = inhalt.realYear + "-";
		for (String s : AbteilungI.ABTEILUNGEN) {
			XYSeries series = new XYSeries(s);

			for (int idx = 0; idx <= 11; idx++) {
				String sqlJahrMonat = sqlJahr;
				if (idx < 9)
					sqlJahrMonat += "0" + (idx + 1) + "-";
				else
					sqlJahrMonat += (idx + 1) + "-";
				double result = inhalt.getDB()
						.dbUrlaubCountForMonthOfAbteilung(sqlJahrMonat, s);
				series.add(idx, result);
			}
			abteilungsWerte.put(s, series);
			xyDataset.addSeries(series);
		}

		XYSeries serie = new XYSeries(ALL);
		int result = inhalt.getDB().dbUrlaubSumSoll(inhalt.realYear);
		int result1 = new Integer(result).intValue();
		for (int idx = 0; idx <= 11; idx++) {
			String sqlJahrMonat = sqlJahr;
			if (idx < 9)
				sqlJahrMonat += "0" + (idx + 1) + "-";
			else
				sqlJahrMonat += (idx + 1) + "-";
			int help = inhalt.getDB().dbUrlaubCountForMonth(sqlJahrMonat);
			result -= help;
			serie.add(idx, new Integer(result).intValue());
		}
		jahresWerte.put(ALL, serie);

		XYSeries serie1 = new XYSeries(ALLLAST);
		for (int idx = 0; idx <= 11; idx++) {
			String sqlJahrMonat = (inhalt.realYear - 1) + "-";
			if (idx < 9)
				sqlJahrMonat += "0" + (idx + 1) + "-";
			else
				sqlJahrMonat += (idx + 1) + "-";
			int help = inhalt.getDB().dbUrlaubCountForMonth(sqlJahrMonat);
			result1 -= help;
			serie1.add(idx, new Integer(result1).intValue());
		}
		jahresWerte.put(ALLLAST, serie1);

		SymbolAxis symbolAxis = new SymbolAxis("Monate", months);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
				false);
		renderer.setBaseItemLabelFont(new Font("Arial", Font.BOLD, 14));
		renderer.setBaseItemLabelsVisible(true);

		// XYItemLabelGenerator generator = new StandardXYItemLabelGenerator();
		// renderer.setBaseItemLabelGenerator(generator);

		XYPlot plot = new XYPlot(xyDataset, symbolAxis,
				new NumberAxis("Anzahl"), renderer);
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.DARK_GRAY);
		plot.setRangeGridlinePaint(Color.DARK_GRAY);
		plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		JFreeChart chart = new JFreeChart("Jahresurlaub", new Font("Arial", 2,
				16), plot, true);
		// panel.setMaximumSize(maximumSize);
		// panel.setMinimumSize(minimumSize);
		return new ChartPanel(chart, true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int idx = 0; idx < AbteilungI.ABTEILUNGEN.length; idx++) {
			if (e.getSource() == abteilungsBoxen
					.get(AbteilungI.ABTEILUNGEN[idx])) {
				JCheckBox help = (JCheckBox) e.getSource();
				if (help.isSelected())
					xyDataset.addSeries(abteilungsWerte
							.get(AbteilungI.ABTEILUNGEN[idx]));

				else
					xyDataset.removeSeries(xyDataset
							.getSeries(AbteilungI.ABTEILUNGEN[idx]));
				break;
			}
		}
		if (e.getSource() == heading) {
			if (heading.getText().equals(SINGLE)) {
				heading.setText(ALL);
				xyDataset.removeAllSeries();
				for (JCheckBox b : abteilungsBoxen.values()) {
					b.setEnabled(false);
				}
				for (XYSeries series : jahresWerte.values()) {
					xyDataset.addSeries(series);
				}
			} else {
				heading.setText(SINGLE);
				xyDataset.removeAllSeries();
				for (JCheckBox b : abteilungsBoxen.values()) {
					b.setEnabled(true);
				}
				for (XYSeries series : abteilungsWerte.values()) {
					xyDataset.addSeries(series);
				}
			}
		}
	}

	public void open() {
		this.inhalt.getPanel().removeAll();
		this.inhalt.getPanel().add(this, BorderLayout.CENTER);
		this.inhalt.getPanel().validate();
		this.inhalt.getPanel().repaint();
	}

}
