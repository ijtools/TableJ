/**
 * 
 */
package ijt.table.gui.action.plot;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ij.gui.GenericDialog;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * @author dlegland
 *
 */
public class LinePlot implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();

        GenericDialog gd = new GenericDialog("Line Plot");
        String[] colNames = table.getColumnNames();
        gd.addChoice("Column:", colNames, colNames[0]);
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        
        int colIndex = gd.getNextChoiceIndex();

        // Initialize XY series
        XYSeries seriesXY = new XYSeries(colNames[colIndex]);
        for (int i = 0; i < table.rowCount(); i++)
        {
            seriesXY.add(i, table.getValue(i, colIndex));
        }

        // add series to a data set
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        xyDataset.addSeries(seriesXY);

        String tableName = table.getName();
        if (tableName == null)
        {
            tableName = "No Name";
        }
        
        // create line chart from data set
        JFreeChart chart = ChartFactory.createXYLineChart(tableName, "", "", xyDataset, PlotOrientation.VERTICAL, true,
                true, true);
        chart.fireChartChanged();

        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart, 500, 200, 500, 200, 500, 500, false, false, true, true, true,
                true);

//      this.getContentPane().add(  GuiUtil.createLineBoxPanel( chartPanel ) );
//      this.getContentPane().add(chartPanel);

        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        // add it to our application
        JFrame plotFrame = new JFrame();
        plotFrame .setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        plotFrame .setContentPane(chartPanel);
        plotFrame .pack();

        // show !
        plotFrame .setVisible(true);
//        return frame;

    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }

}
