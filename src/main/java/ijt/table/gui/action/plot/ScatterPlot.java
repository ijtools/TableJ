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
public class ScatterPlot implements TableFrameAction
{
    String[] markerTypes = new String[] {"Circle", "Cross", "Diamond", "Plus", "Square", "Triangle Up", "Triangle Down"};
    
    @Override
    public void run(TableFrame parentFrame)
    {
        Table table = parentFrame.getTable();

        GenericDialog gd = new GenericDialog("Line Plot");
        String[] colNames = table.getColumnNames();
        gd.addChoice("X-Axis Column:", colNames, colNames[0]);
        gd.addChoice("Y-Axis Column:", colNames, colNames[0]);
        gd.addChoice("Marker_Type", markerTypes, markerTypes[0]);
        gd.addNumericField("Marker_Size", 10, 0);
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        
        // parse user choices
        int xColIndex = gd.getNextChoiceIndex();
        int yColIndex = gd.getNextChoiceIndex();
        int markerTypeIndex = gd.getNextChoiceIndex();
        int markerSize = (int) gd.getNextNumber();

        
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
                .xAxisTitle(table.getColumnName(xColIndex))
                .yAxisTitle(table.getColumnName(yColIndex))
                .build();
        
        // Additional chart style
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
        chart.getStyler().setMarkerSize(markerSize);
        
        // Initialize XY series
        double[] xData = table.getColumnValues(xColIndex);
        double[] yData = table.getColumnValues(yColIndex);
        XYSeries series = chart.addSeries(tableName, xData, yData);
        switch (markerTypeIndex)
        {
            case 0: series.setMarker(SeriesMarkers.CIRCLE); break;
            case 1: series.setMarker(SeriesMarkers.CROSS); break;
            case 2: series.setMarker(SeriesMarkers.DIAMOND); break;
            case 3: series.setMarker(SeriesMarkers.PLUS); break;
            case 4: series.setMarker(SeriesMarkers.SQUARE); break;
            case 5: series.setMarker(SeriesMarkers.TRIANGLE_UP); break;
            case 6: series.setMarker(SeriesMarkers.TRIANGLE_DOWN); break;
        }

        // Create and set up the window.
        JFrame plotFrame = new JFrame();
        plotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // relocate with respect to parent frame
        Point pos0 = parentFrame.getJFrame().getLocation();
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
        
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }

}
