/**
 * 
 */
package ijt.table.gui.action.plot;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.HeatMapChart;
import org.knowm.xchart.HeatMapChartBuilder;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.frame.ChartFrame;

/**
 * Display the content of the current table as heat map.
 * 
 * Requires XChart version 3.6.3 or newer.
 * 
 * @author dlegland
 *
 */
public class HeatMapDisplay implements TableFrameAction
{

    @Override
    public void run(TableFrame parentFrame)
    {
        Table table = parentFrame.getTable();

        // Default name for table
        String tableName = table.getName();
        if (tableName == null || tableName.length() == 0)
        {
            tableName = "Data";
        }
        
        int nRows = table.rowCount();  
        int nCols = table.columnCount();
        List<String> xData = new ArrayList<String>();
        for (String str : table.getColumnNames())
        {
            xData.add(str);
        }
        
//        int[] yData = generateLinearVector(nRows);
//        int[] yData = generateLinearVector(nCols);
        List<String> yData = new ArrayList<String>();
        for (String str : table.getRowNames())
        {
            yData.add(str);
        }
        
        List<Number[]> heatData = new ArrayList<>(nRows *nCols);
        
        for (int i = 0; i < nRows; i++)
        {
            for (int j = 0; j < nCols; j++)
            {
                Number[] numbers = new Number[] {i, j, table.getValue(i, j)};
                heatData.add(numbers);
            }
        }
        
        // Create Chart
        HeatMapChart chart = new HeatMapChartBuilder()
                .width(1000)
                .height(600)
                .title(tableName)
                .build();

        chart.getStyler().setPlotContentSize(1);
        chart.getStyler().setShowValue(true);
        chart.addSeries("Basic HeatMap", xData, yData, heatData);
//        HeatMapSeries heatMapSeries = chart.addSeries("heatMap", xData, yData, heatData);
        
        ChartFrame.create(chart, "HeatMap", parentFrame);


//        // Create and set up the window.
//        JFrame plotFrame = new JFrame();
//        plotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // relocate with respect to parent frame
//        Point pos0 = parentFrame.getJFrame().getLocation();
//        plotFrame.setLocation(pos0.x + 30, pos0.y + 20);
//        
//        // Schedule a job for the event-dispatching thread:
//        // creating and showing this application's GUI.
//        try
//        {
//            javax.swing.SwingUtilities.invokeAndWait(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    // add a panel containing the chart 
//                    @SuppressWarnings({ "rawtypes", "unchecked" })
//                    XChartPanel chartPanel = new XChartPanel(chart);
//                    plotFrame.add(chartPanel);
//                    
//                    // Display the window.
//                    plotFrame.pack();
//                    plotFrame.setVisible(true);
//                }
//            });
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//        catch (InvocationTargetException e)
//        {
//            e.printStackTrace();
//        }
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
