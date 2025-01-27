/**
 * 
 */
package ijt.table.gui.action.plot;

import java.util.ArrayList;

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
import ijt.table.gui.frame.ChartFrame;

/**
 * @author dlegland
 *
 */
public class MultiLinePlot implements TableFrameAction
{

    @Override
    public void run(TableFrame parentFrame)
    {
        Table table = parentFrame.getTable();
        int nCols = table.columnCount();

        GenericDialog dlg = new GenericDialog("Multi Line Plot");
        String[] colNames = table.getColumnNames();
        dlg.addCheckbox("XData from column values", true);
        dlg.addChoice("Column for XData:", colNames, colNames[0]);
        int nCols2 = Math.min(nCols, 20);
        for (int i = 0; i < nCols2; i++)
        {
            dlg.addCheckbox(colNames[i], false);
        }

        dlg.showDialog();
        if (dlg.wasCanceled())
        {
            return;
        }
        
        boolean useColumnXData = dlg.getNextBoolean();
        int xDataColumnIndex = dlg.getNextChoiceIndex();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < nCols2; i++)
        {
            if (dlg.getNextBoolean())
            {
                indices.add(i);
            }
        }

        // generate data for x-axis
        double[] xData;
        String xAxisLabel;
        if (useColumnXData)
        {
            xData = table.getColumnValues(xDataColumnIndex);
            xAxisLabel = table.getColumnName(xDataColumnIndex);
        }
        else
        {
            xData = generateLinearVector(table.rowCount());
            xAxisLabel = "Row index";
        }
        
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
                .xAxisTitle(xAxisLabel)
                .yAxisTitle("")
                .build();
        
        // Additional chart style
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);

        // Initialize XY series
        for (int iCol : indices)
        {
            XYSeries series = chart.addSeries(colNames[iCol], xData, table.getColumnValues(iCol));
            series.setMarker(SeriesMarkers.NONE);
        }
        
        ChartFrame.create(chart, "MultiLine Plot", parentFrame);
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
