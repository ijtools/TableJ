/**
 * 
 */
package ijt.table.gui.action.plot;

import org.knowm.xchart.AnnotationText;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import ij.IJ;
import ij.gui.GenericDialog;
import ijt.table.Column;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.frame.ChartFrame;

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
        if (table.columnCount() < 2)
        {
        	IJ.error("Requires a table with at least two colums", "Table error");
        	return;
        }

        // retrieve table information
        String[] colNames = table.getColumnNames();
        String[] colNames2 = computeDisplayLabelsOptionStrings(table);
        String tableName = table.getName();
        if (tableName == null || tableName.length() == 0)
        {
            tableName = "Data";
        }
        
        // Create dialog to setup options
        GenericDialog gd = new GenericDialog("Line Plot");
        gd.addChoice("X-Axis Column:", colNames, colNames[0]);
        gd.addChoice("Y-Axis Column:", colNames, colNames[1]);
        gd.addChoice("Marker_Type", markerTypes, markerTypes[0]);
        gd.addNumericField("Marker_Size", 10, 0);
        gd.addChoice("Display Labels:", colNames2, colNames2[0]);
        
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
        int displayLabelIndex = gd.getNextChoiceIndex();

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
        
        // Optional display of labels
        if (displayLabelIndex > 0)
        {
        	String[] labels = computeLabels(table, displayLabelIndex);
			for (int i = 0; i < xData.length; i++)
			{
		        chart.addAnnotation(new AnnotationText(labels[i], xData[i], yData[i], false));
			}
        }
        
        ChartFrame.create(chart, "Scatter Plot", parentFrame);
    }
    
    private static final String[] computeDisplayLabelsOptionStrings(Table table)
    {
    	String[] colNames = table.getColumnNames();
        String[] colNames2;
    	if (table.hasRowNames())
        {
        	colNames2 = new String[colNames.length + 2];
        	colNames2[0] = "None";
        	colNames2[1] = "Row Names";
        	for (int i = 0; i < colNames.length; i++)
        	{
        		colNames2[i+2] = colNames[i];
        	}
        }
        else
        {
        	colNames2 = new String[colNames.length + 1];
        	colNames2[0] = "None";
        	for (int i = 0; i < colNames.length; i++)
        	{
        		colNames2[i+1] = colNames[i];
        	}
        }
    	return colNames2;
    }
    
    private static final String[] computeLabels(Table table, int displayLabelIndex)
    {
		if (table.hasRowNames() && displayLabelIndex == 1)
		{
			return table.getRowNames();
		}
		else
		{
			int colIndex = table.hasRowNames() ? displayLabelIndex - 2 : displayLabelIndex - 1;
			return createColumnLabels(table.getColumn(colIndex));
		}
    }
    
    private static final String[] createColumnLabels(Column column)
    {
		int n = column.size();
		String[] names = new String[n];
		for (int i = 0; i < n; i++)
		{
			names[i] = column.getString(i);
		}
		return names;
    }
        
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
