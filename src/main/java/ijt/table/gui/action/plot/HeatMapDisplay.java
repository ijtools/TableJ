/**
 * 
 */
package ijt.table.gui.action.plot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.HeatMapChart;
import org.knowm.xchart.HeatMapChartBuilder;
import org.knowm.xchart.style.HeatMapStyler;

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

        Color[] rangeColors = {
                new Color(  0,   0, 196), 
                new Color(  0, 127, 255), 
                new Color(127, 196, 255), 
                new Color(255, 255, 255), 
                new Color(255, 127, 127), 
                new Color(255,   0,   0), 
                new Color(196,   0,   0)
                };
        HeatMapStyler styler = chart.getStyler(); 
        styler.setRangeColors(rangeColors);
        styler.setMin(-1);
        styler.setMax(+1);
        styler.setPlotContentSize(1);
        styler.setShowValue(true);
        styler.setHeatMapValueDecimalPattern("#0.000;-#0.000");
        styler.setXAxisLabelRotation(45);
        chart.addSeries("Basic HeatMap", xData, yData, heatData);
        
        ChartFrame.create(chart, "HeatMap", parentFrame);
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
