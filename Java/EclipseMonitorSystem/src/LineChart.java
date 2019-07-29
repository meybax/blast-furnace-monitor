// LINECHART USING JFREECHART

import java.awt.Dimension;

/*	In Eclipse,
 * 		Go to Project -> Properties -> Java Build Path -> Libraries -> Classpath
 * 		Add external Jar jfreechart-1.0.19.jar from "Java/Libraries/jfreechart-1.0.19/lib"
 * 		Add external Jar jcommon-1.0.123.jar from "Java/Libraries/jfreechart-1.0.19/lib"
 */
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;

public class LineChart extends ApplicationFrame {
	private final XYSeries series;
	
	// creates a line chart
	public LineChart(String title, String label) {
		super(title);
		series = new XYSeries(label);
		final XYSeriesCollection dataset = new XYSeriesCollection(series);
		final JFreeChart xyLineChart = ChartFactory.createXYLineChart(
				title,
				"Time (ms)",
				label,
				dataset,
				PlotOrientation.VERTICAL,
				true , true , false);

		final ChartPanel chart = new ChartPanel(xyLineChart);
		chart.setPreferredSize(new Dimension(560 , 370));
		setContentPane(chart);

		pack();
		RefineryUtilities.positionFrameRandomly(this);
		setVisible(true);
	}
	
	// adds a value and time-stamp to the chart
	public void add(int time, double val) {
		series.add(time, val);
	}
	
	// resets the chart
	public void reset() {
		series.clear();
	}
}
