// LineChart from JFreeChart

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import java.awt.Dimension;

public class LineChart extends ApplicationFrame {
	private final XYSeries series;
	
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
	
	public void add(int time, double val) {
		series.add(time, val);
	}
	
	public void reset() {
		series.clear();
	}
}
