/**
 * 
 */
package ijt.table.gui.action.plot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knowm.xchart.Histogram;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;
import ijt.table.gui.action.file.OpenDemoTable;
import ijt.table.gui.frame.ChartFrame;


/**
 * Displays a dialog to choose a numeric column from a table, as well as value
 * range and bin number, and displays the histogram of values when user
 * validates input.
 * 
 * @author dlegland
 *
 */
public class PlotTableColumnHistogram implements FramePlugin
{
    @Override
    public void run(BaseFrame frame, String options)
    {
        if (!(frame instanceof TableFrame)) return;
        Table table = ((TableFrame) frame).getTable();
        
        new HistogramDialog(frame, "Column Values Histogram", table);
    }
    
    private static final XYChart createChart(NumericColumn col, double[] valueRange, int binCount)
    {
        List<Double> valueList = Arrays.stream(col.getValues()).boxed().collect(Collectors.toList());
        Histogram histogram = new Histogram(valueList, binCount, valueRange[0], valueRange[1]);
        
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
        
        return chart;
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
    private static final XYSeries addHistogramSeries(XYChart chart, String name, Histogram histogram)
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
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }

    /**
     * Inner class that displays a custom dialog, and updates chart display when
     * necessary.
     */
    private static class HistogramDialog
    {
        BaseFrame refFrame;
        Table table;
        
        /**
         * The array of indices of the numeric columns.
         */
        int[] numColInds;
        NumericColumn currentColumn = null;
        
        // options widgets
        JComboBox<String> columnSelection;
        JTextField minValueTextField;
        JTextField maxValueTextField;
        JTextField binNumberTextField;
        
        // widgets
        JDialog dlg;
        JButton okButton;
        JButton closeButton;
        
        ChartFrame chartFrame = null;
        
        public HistogramDialog(BaseFrame refFrame, String dlgTitle, Table refTable)
        {
            this.dlg = new JDialog(refFrame.getJFrame(), dlgTitle, false);
            this.refFrame = refFrame;
            this.table = refTable;
            
            // initialize inner data
            this.numColInds = computeIndexOfNumericColumns(table);
            this.currentColumn = (NumericColumn) table.getColumn(numColInds[0]);
            
            // setup GUI
            createWidgets();
            setupLayout();
            dlg.setMinimumSize(new Dimension(200, 200));
            dlg.setLocationRelativeTo(refFrame.getJFrame());
            
            this.dlg.setVisible(true);
        }
        
        private void createWidgets()
        {
            String[] numColNames = new String[numColInds.length];
            for (int i = 0; i < numColInds.length; i++)
            {
                numColNames[i] = table.getColumnName(i);
            }
            
            columnSelection = new JComboBox<String>(numColNames);
            columnSelection.addActionListener(evt -> {
                int selIndex = columnSelection.getSelectedIndex();
                if (selIndex < 0) return;
                int colIndex = numColInds[selIndex];
                currentColumn = (NumericColumn) table.getColumn(colIndex);
                
                setMinMaxValueTextFields();
            });
            
            minValueTextField = new JTextField();
            maxValueTextField = new JTextField();
            setMinMaxValueTextFields();
            binNumberTextField = new JTextField("20");
            
            // create control buttons
            okButton = new JButton("Display");
            okButton.addActionListener(evt -> {
                okButton.setText("Update");
                
                // parse fields
                double vmin = Double.parseDouble(minValueTextField.getText());
                double vmax = Double.parseDouble(maxValueTextField.getText());
                int nBins = (int)Double.parseDouble(binNumberTextField.getText());
                
                // recompute new chart
                XYChart chart = createChart(currentColumn, new double[] {vmin, vmax}, nBins);
                
                // display a new frame, or update existing frame
                if (chartFrame == null)
                {
                    chartFrame = ChartFrame.create(chart, "Histogram of " + currentColumn.getName(), refFrame);
                }
                else
                {
                    chartFrame.setChart(chart);
                    chartFrame.setTitle("Histogram of " + currentColumn.getName());
                    
                    chartFrame.getJFrame().invalidate();
                    chartFrame.getJFrame().validate();
                    chartFrame.getJFrame().repaint();
                }
            });
            closeButton = new JButton("Close");
            closeButton.addActionListener(evt -> setVisible(false));
        }
        
        private void setMinMaxValueTextFields()
        {
            double[] range = currentColumn.valueRange();
            minValueTextField.setText(String.format(Locale.ENGLISH, "%5.2f", range[0]));
            maxValueTextField.setText(String.format(Locale.ENGLISH, "%5.2f", range[1]));
        }
        
        private int[] computeIndexOfNumericColumns(Table table)
        {
            ArrayList<Integer> indexList = new ArrayList<Integer>();
            for (int i = 0; i < table.columnCount(); i++)
            {
                if (table.getColumn(i) instanceof NumericColumn)
                {
                    indexList.add(i);
                }
            }
            
            int[] inds = new int[indexList.size()];
            for (int i = 0; i < indexList.size(); i++)
            {
                inds[i] = indexList.get(i);
            }
            return inds;
        }
        
        private void setupLayout()
        {
            JPanel mainPanel = new JPanel();
            
            JPanel optionsPanel = new JPanel();
            optionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            optionsPanel.setLayout(new GridBagLayout());
            
            // create constraints for the different types of elements
            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.gridy = 0;
            labelConstraints.gridwidth = 1;
            labelConstraints.gridheight = 1;
            labelConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
            labelConstraints.insets = new Insets(5, 5, 5, 5);            
            GridBagConstraints fieldsConstraints = new GridBagConstraints();
            fieldsConstraints.gridx = 1;
            fieldsConstraints.gridy = 0;
            fieldsConstraints.gridwidth = GridBagConstraints.REMAINDER;
            fieldsConstraints.gridheight = 1;
            fieldsConstraints.fill = GridBagConstraints.HORIZONTAL;
            fieldsConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
            fieldsConstraints.insets = new Insets(5, 5, 5, 5);
            
            // choose the column
            int row = 0;
            fieldsConstraints.gridy = labelConstraints.gridy = row++;
            optionsPanel.add(new JLabel("Column:"), labelConstraints);
            optionsPanel.add(columnSelection, fieldsConstraints);
            
            // choose the min/max values
            fieldsConstraints.gridy = labelConstraints.gridy = row++;
            optionsPanel.add(new JLabel("Min Value:"), labelConstraints);
            optionsPanel.add(minValueTextField, fieldsConstraints);
            fieldsConstraints.gridy = labelConstraints.gridy = row++;
            optionsPanel.add(new JLabel("Max Value:"), labelConstraints);
            optionsPanel.add(maxValueTextField, fieldsConstraints);

            // Number of bins
            fieldsConstraints.gridy = labelConstraints.gridy = row++;
            optionsPanel.add(new JLabel("Bin count:"), labelConstraints);
            optionsPanel.add(binNumberTextField, fieldsConstraints);
            
            // encapsulate the buttons into a JPanel
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonsPanel.add(okButton);
            buttonsPanel.add(closeButton);

            // make the panel containing the buttons to fill the whole row
            row++;
            GridBagConstraints buttonsConstraints = new GridBagConstraints();
            buttonsConstraints.gridx = 0;
            buttonsConstraints.gridy = row++;
            buttonsConstraints.gridheight = buttonsConstraints.gridwidth = 2;
            buttonsConstraints.weightx = 1.;
            buttonsConstraints.weighty = 1.;
            buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;
            optionsPanel.add(buttonsPanel, buttonsConstraints);
            
            mainPanel.add(optionsPanel, BorderLayout.CENTER);
            
            dlg.setContentPane(optionsPanel);
            dlg.pack();
        }
        
        public void setVisible(boolean state)
        {
            this.dlg.setVisible(state);
        }
    }
    
    public static void main(String... args) throws IOException
    {
        InputStream inputStream = OpenDemoTable.class.getResourceAsStream("/tables/fisherIris.txt");
        Table table = new ijt.table.io.DelimitedTableReader().readTable(inputStream);
        table.setName("fisherIris");
        
        System.out.println("Table " + table.rowCount() + "x" + table.columnCount());
        
        new HistogramDialog(new TableFrame(Table.create(5, 3)), "Histogram", table);
    }
}
