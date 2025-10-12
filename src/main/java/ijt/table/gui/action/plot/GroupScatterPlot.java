/**
 * 
 */
package ijt.table.gui.action.plot;

import java.awt.Color;
import java.util.ArrayList;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;

import ij.IJ;
import ij.gui.GenericDialog;
import ijt.table.CategoricalColumn;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;
import ijt.table.gui.frame.ChartFrame;

/**
 * Plot individuals with X and Y coordinates specified by two numeric columns,
 * and a color/marker specified by a categorical column.
 * 
 * @author dlegland
 *
 */
public class GroupScatterPlot implements FramePlugin
{
    @Override
    public void run(BaseFrame frame, String options)
    {
        if (!(frame instanceof TableFrame)) return;
        Table table = ((TableFrame) frame).getTable();
        if (table.columnCount() < 3)
        {
        	IJ.error("Requires a table with at least three colums", "Table error");
        	return;
        }

        String[] catColNames = getCategoricalColumnNames(table);
        if (catColNames.length == 0)
        {
        	IJ.error("Requires a table with at least one categorical column", "Table error");
        	return;
        }

        // Create dialog
        GenericDialog gd = new GenericDialog("Group Scatter Plot");
        String[] colNames = table.getColumnNames();
        gd.addChoice("X-Axis Column:", colNames, colNames[0]);
        gd.addChoice("Y-Axis Column:", colNames, colNames[1]);
        gd.addChoice("Grouping Column:", catColNames, catColNames[0]);
        gd.addNumericField("Marker_Size", 10, 0);
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        
        // parse user choices
        int xColIndex = gd.getNextChoiceIndex();
        int yColIndex = gd.getNextChoiceIndex();
        String groupColName = gd.getNextChoice();
        int markerSize = (int) gd.getNextNumber();

        
        NumericColumn colX = (NumericColumn) table.getColumn(xColIndex);
        NumericColumn colY = (NumericColumn) table.getColumn(yColIndex);
        CategoricalColumn groupCol = (CategoricalColumn) table.getColumn(table.findColumnIndex(groupColName));
        
        int nGroups = groupCol.levelNames().length;
        ArrayList<ArrayList<Double>> xData = new ArrayList<>(nGroups);
        ArrayList<ArrayList<Double>> yData = new ArrayList<>(nGroups);
        for (int i = 0; i < nGroups; i++)
        {
            xData.add(new ArrayList<Double>());
            yData.add(new ArrayList<Double>());
        }
        
        // iterate over rows
        for (int i = 0; i < table.rowCount(); i++)
        {
            int iGroup = groupCol.getLevelIndex(i);
            xData.get(iGroup).add(colX.getValue(i));
            yData.get(iGroup).add(colY.getValue(i));
        }

        // Default name for table
        String tableName = table.getName();
        if (tableName == null || tableName.length() == 0)
        {
            tableName = "Data";
        }
        
        // retrieve group colors
        Color[] groupColors = groupCol.levelColors();
        
        // Create the Chart
        XYChart chart = new XYChartBuilder()
                .width(600)
                .height(500)
                .xAxisTitle(table.getColumnName(xColIndex))
                .yAxisTitle(table.getColumnName(yColIndex))
                .theme(ChartTheme.Matlab)
                .build();
        
        // Additional chart style
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
        chart.getStyler().setMarkerSize(markerSize);
        
        String[] levelNames = groupCol.levelNames();
        for (int i = 0; i < nGroups; i++)
        {
            if (!xData.get(i).isEmpty())
            {
                double[] xarr = xData.get(i).stream().mapToDouble(Double::doubleValue).toArray();
                double[] yarr = yData.get(i).stream().mapToDouble(Double::doubleValue).toArray();
                XYSeries series = chart.addSeries(levelNames[i], xarr, yarr);
                series.setMarkerColor(groupColors[i]);
            }
        }

        ChartFrame.create(chart, "Group Scatter Plot", frame);
    }
    
    private String[] getCategoricalColumnNames(Table table)
    {
        int nCols = table.columnCount();
        
        int ncc = 0;
        for (int iCol = 0; iCol < nCols; iCol++)
        {
            if (table.getColumn(iCol) instanceof CategoricalColumn)
            {
                ncc++;
            }
        }
        
        String[] catColNames = new String[ncc];
        int icc = 0;
        for (int iCol = 0; iCol < nCols; iCol++)
        {
            if (table.getColumn(iCol) instanceof CategoricalColumn)
            {
                catColNames[icc++] = table.getColumnName(iCol);
            }
        }
        
        return catColNames;
    }
        
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
