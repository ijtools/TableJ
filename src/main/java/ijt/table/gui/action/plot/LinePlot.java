/**
 * 
 */
package ijt.table.gui.action.plot;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

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
    public void run(TableFrame parentFrame)
    {
        Table table = parentFrame.getTable();

        GenericDialog gd = new GenericDialog("Line Plot");
        String[] colNames = table.getColumnNames();
        gd.addChoice("Column:", colNames, colNames[0]);
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        
        int colIndex = gd.getNextChoiceIndex();

        // Default name for table
        String tableName = table.getName();
        if (tableName == null || tableName.length() == 0)
        {
            tableName = "Data";
        }
        
        // Create the Chart
        XYChart chart = new XYChartBuilder()
                .width(600)
                .height(500)
                .title(tableName)
                .xAxisTitle("Row index")
                .yAxisTitle("")
                .build();
        
        // Additional chart style
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);

        // Initialize XY series
        double[] xData = generateLinearVector(table.rowCount());
        XYSeries series = chart.addSeries(colNames[colIndex], xData, table.getColumnValues(colIndex));
        series.setMarker(SeriesMarkers.NONE);

        // Create and set up the window.
        JFrame plotFrame = new JFrame();
        plotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // relocate with respect to parent frame
        Point pos0 = parentFrame.getLocation();
        plotFrame.setLocation(pos0.x + 30, pos0.y + 20);
        
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        try
        {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable()
            {
                @Override
                public void run()
                {
                    // add a panel containing the chart 
                    @SuppressWarnings({ "rawtypes", "unchecked" })
                    XChartPanel chartPanel = new XChartPanel(chart);
                    plotFrame.add(chartPanel);
                    
                    // Display the window.
                    plotFrame.pack();
                    plotFrame.setVisible(true);
                }
            });
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Generate a linear vectors containing values starting from 1, 2... to
     * nValues.
     * 
     * @param nValues
     *            the number of values
     * @return a linear vector of nRows values
     */
    private double[] generateLinearVector(int nValues)
    {
        double[] values = new double[nValues];
        for (int i = 0; i < nValues; i++)
        {
            values[i] = i+1;
        }
        return values;
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }

}
