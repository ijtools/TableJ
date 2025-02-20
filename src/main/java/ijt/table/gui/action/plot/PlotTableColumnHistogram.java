/**
 * 
 */
package ijt.table.gui.action.plot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.knowm.xchart.Histogram;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import ij.gui.GenericDialog;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.frame.ChartFrame;


/**
 * @author dlegland
 *
 */
public class PlotTableColumnHistogram implements TableFrameAction
{
    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();

        GenericDialog gd = new GenericDialog("Histogram");
        // create a list of names corresponding to numeric columns
        String[] colNames = table.columns().stream()
                .filter(c -> c instanceof NumericColumn)
                .map(c -> c.getName())
                .toArray(String[]::new);
        if (colNames.length == 0)
        {
            throw new RuntimeException("Input table does not contain any numeric column");
        }
        
        gd.addChoice("Column:", colNames, colNames[0]);
        gd.addNumericField("Bin Number", 20, 0);
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        
        int colIndex = gd.getNextChoiceIndex();
        int binCount = (int) gd.getNextNumber();
        NumericColumn col = (NumericColumn) table.getColumn(table.findColumnIndex(colNames[colIndex]));

        double[] data = col.getValues();
        double[] range = valueRange(data);
        List<Double> valueList = Arrays.stream(data).boxed().collect(Collectors.toList());
        Histogram histogram = new Histogram(valueList, binCount, range[0], range[1]);
        
        String chartTitle = "Histogram";
        if (col.getName() != null && col.getName().length() > 0)
        {
            chartTitle += " of " + col.getName();
        }
        
        // Create Chart
        XYChart chart = new XYChartBuilder()
                .width(600)
                .height(500)
                .title(chartTitle)
                .xAxisTitle(col.getName())
                .yAxisTitle("Count")
                .build();
        
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisDecimalPattern("#0.00");
        
        XYSeries series = addHistogramSeries(chart, col.getName(), histogram);
        series.setMarker(SeriesMarkers.NONE);
        
        // Show it
        ChartFrame.create(chart, chartTitle, frame);
    }
    
    /**
     * Converts the histogram into a XYSeries suitable for display.
     * 
     * @param chart
     *            the chart to add the series to
     * @param name
     *            the name of the series, used for legend
     * @param histogram
     *            the histogram to convert
     * @return a XYSeries mimicking the histogram
     */
    private XYSeries addHistogramSeries(XYChart chart, String name, Histogram histogram)
    {
        double xmin = histogram.getMin();
        double xmax = histogram.getMax();
        int nBins = histogram.getNumBins();
        double binSize = (xmax - xmin) / nBins;
        
        List<Double> histoData = histogram.getyAxisData();
        
        // allocate data for new series
        double[] xData = new double[histoData.size() * 4];
        double[] yData = new double[histoData.size() * 4];
        for(int i = 0; i < histoData.size(); i++)
        {
            xData[4 * i]     = xmin + i * binSize;
            xData[4 * i + 1] = xmin + i * binSize;
            xData[4 * i + 2] = xmin + (i + 1) * binSize;
            xData[4 * i + 3] = xmin + (i + 1) * binSize;
            yData[4 * i] = 0;
            yData[4 * i + 1] = histoData.get(i);
            yData[4 * i + 2] = histoData.get(i);
            yData[4 * i + 3] = 0;
        }
        
        return chart.addSeries(name, xData, yData);
    }
    
    private static final double[] valueRange(double[] values)
    {
        double vmin = Double.POSITIVE_INFINITY;
        double vmax = Double.NEGATIVE_INFINITY;
        for (double v : values)
        {
            vmin = Math.min(v, vmin);
            vmax = Math.max(v, vmax);
        }
        return new double[] {vmin, vmax};
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }

}
