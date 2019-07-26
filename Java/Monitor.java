import java.awt.event.*;

/*
   add to CLASSPATH:
      Libraries/jfreechart-1.0.19/lib/jfreechart-1.0.19.jar
      Libraries/jfreechart-1.0.19/lib/jcommon-1.0.23.jar
   for JFreeChart (http://www.jfree.org/jfreechart/)
*/
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Monitor {

   public static void main(String[] args) {
      new Monitor().run(args);
   }

   public void run(String[] args) {
      ControlPanel cp = new ControlPanel();
      
      // adds button events
   	cp.b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					cp.light1.changeColor();				
			}          
      });
      cp.b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					cp.light2.changeColor();				
			}          
      });
	   cp.b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					cp.remote.changeColor();				
			}          
      });
      
      LineChart_AWT chart = new LineChart_AWT("School Vs Years", "Number of Schools vs Years");
      chart.pack();
      RefineryUtilities.centerFrameOnScreen(chart);
      chart.setVisible(true);
      
      // MAIN LOOP
      while (true) {
      
      }
   }
   
   public class LineChart_AWT extends ApplicationFrame {
      public LineChart_AWT(String applicationTitle , String chartTitle) {
         super(applicationTitle);
         JFreeChart lineChart = ChartFactory.createLineChart(
            chartTitle,
            "Years","Number of Schools",
            createDataset(),
            PlotOrientation.VERTICAL,
            true,true,false);
            
         ChartPanel chartPanel = new ChartPanel(lineChart);
         chartPanel.setPreferredSize(new java.awt.Dimension(560 , 367));
         setContentPane(chartPanel);
      }
   
      private DefaultCategoryDataset createDataset( ) {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
         dataset.addValue( 15 , "schools" , "1970" );
         dataset.addValue( 30 , "schools" , "1980" );
         dataset.addValue( 60 , "schools" ,  "1990" );
         dataset.addValue( 120 , "schools" , "2000" );
         dataset.addValue( 240 , "schools" , "2010" );
         dataset.addValue( 300 , "schools" , "2014" );
         return dataset;
      }
   }
   
}