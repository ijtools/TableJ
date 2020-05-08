/**
 * 
 */
package ijt.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A simple plain table for storing measurement results. Data table is indexed
 * by column first. Methods access data by row indexing first.
 * 
 * @author David Legland
 *
 */
public class DataTable
{

    /**
     * Inner data array, first index corresponds to columns.
     */
    double[][] data;

    int nCols;
    int nRows;

    String name = null;
    String[] colNames = null;
    String[] rowNames = null;

    /**
     * Creates a new data table with the given number of rows and columns.
     * 
     * @param nRows the number of rows
     * @param nCols the number of columns
     */
    public DataTable(int nRows, int nCols)
    {
        this.data = new double[nCols][nRows];
        this.nCols = nCols;
        this.nRows = nRows;

        initColNames();
//		this.rowNames = new String[nRows];
    }

    public DataTable(double[][] data)
    {
        this.data = data;

        this.nCols = data.length;
        if (this.nCols > 0)
        {
            this.nRows = data[0].length;
        }
        else
        {
            this.nRows = 0;
        }

        initColNames();
//		this.rowNames = new String[this.nRows];
    }

    public DataTable(double[][] data, String[] colNames, String[] rowNames)
    {
        this.data = data;

        this.nCols = data.length;
        if (this.nCols > 0)
        {
            this.nRows = data[0].length;
        }
        else
        {
            this.nRows = 0;
        }

        if (colNames.length != this.nCols)
            throw new IllegalArgumentException("Number of column names should match number of data columns");
        this.colNames = colNames;

        if (rowNames.length != this.nRows)
            throw new IllegalArgumentException("Number of row names should match number of data rows");
        this.rowNames = rowNames;

    }

    /**
     * Initialize column names with index 1-based.
     */
    private void initColNames()
    {
        this.colNames = new String[this.nCols];
        for (int c = 0; c < this.nCols; c++)
            this.colNames[c] = Integer.toString(c + 1);
    }

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row the row index, 0-indexed
     * @param col the column index, 0-indexed
     * @return the value at the specified position
     */
    public double getValue(int row, int col)
    {
        return this.data[col][row];
    }

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row     the row index, 0-indexed
     * @param colName the name of the column
     * @return the value at the specified position
     */
    public double getValue(int row, String colName)
    {
        int col = this.getColumnIndex(colName);
        return this.data[col][row];
    }

    /**
     * Changes the value at the specified position
     * 
     * @param col   the column index, 0-indexed
     * @param row   the row index, 0-indexed
     * @param value the new value
     */
    public void setValue(int row, int col, double value)
    {
        this.data[col][row] = value;
    }

    /**
     * Changes the value at the specified position
     * 
     * @param row     the row index, 0-indexed
     * @param colName the name of the column to modify
     * @param value   the new value
     */
    public void setValue(int row, String colName, double value)
    {
        int col = this.getColumnIndex(colName);
        this.data[col][row] = value;
    }

    /**
     * Returns an entire column of the data table.
     */
    public double[] getColumnValues(int col)
    {
        return this.data[col];
    }

    /**
     * Returns an entire row of the data table.
     */
    public double[] getRowValues(int row)
    {
        double[] res = new double[this.nCols];
        for (int c = 0; c < this.nCols; c++)
        {
            res[c] = this.data[c][row];
        }
        return res;
    }

    /**
     * Returns the number of columns (measurements, variables) in the data table.
     */
    public int geColumnNumber()
    {
        return this.nCols;
    }

    /**
     * Returns the number of rows (individuals, observations) in the data table.
     */
    public int getRowNumber()
    {
        return this.nRows;
    }

    public String[] getColumnNames()
    {
        return this.colNames;
    }

    public void setColumnNames(String[] names)
    {
        if (names.length != this.nCols)
            throw new IllegalArgumentException("String array must have same length as the number of columns.");
        this.colNames = names;
    }

    public void setColumnName(int colIndex, String name)
    {
        if (colIndex >= this.nCols)
            throw new IllegalArgumentException(
                    "Index column greater than column number: " + colIndex + ">" + this.nCols);
        this.colNames[colIndex] = name;
    }

    public int getColumnIndex(String name)
    {
        if (name == null || this.colNames == null)
            return -1;
        for (int c = 0; c < this.nCols; c++)
        {
            if (name.equals(this.colNames[c]))
                return c;
        }
        return -1;
    }

    public String[] getRowNames()
    {
        return this.rowNames;
    }

    public void setRowNames(String[] names)
    {
        if (names.length != this.nRows)

            this.rowNames = names;
    }

    /**
     * Changes the name of this data table.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of this data table
     */
    public String getName()
    {
        return this.name;
    }

    public DataTable concatenateColumns(DataTable table)
    {
        if (this.nRows != table.nRows)
            throw new IllegalArgumentException("Data tables must have the same number of rows");

        // allocate result
        int newNCols = this.nCols + table.nCols;
        DataTable result = new DataTable(nRows, newNCols);

        // add values of this table
        for (int col = 0; col < nCols; col++)
        {
            result.colNames[col] = this.colNames[col];
            for (int row = 0; row < nRows; row++)
            {
                result.setValue(row, col, this.getValue(row, col));
                result.rowNames[row] = this.rowNames[row];
            }
        }

        // add values of other table
        for (int col = 0; col < table.nCols; col++)
        {
            result.colNames[col + nCols] = table.colNames[col];
            for (int row = 0; row < nRows; row++)
            {
                result.setValue(row, col + nCols, table.getValue(row, col));
            }
        }

        return result;
    }

    public DataTable concatenateRows(DataTable table)
    {
        if (this.nCols != table.nCols)
            throw new IllegalArgumentException("Data tables must have the same number of columns");

        // allocate result
        int newNRows = this.nRows + table.nRows;
        DataTable result = new DataTable(newNRows, nCols);

        for (int col = 0; col < nCols; col++)
        {
            // add values of this table
            result.colNames[col] = this.colNames[col];
            for (int row = 0; row < nRows; row++)
            {
                result.setValue(row, col, this.getValue(row, col));
            }
            // add values of the other table
            for (int row = 0; row < table.nRows; row++)
            {
                result.setValue(row + nRows, col, table.getValue(row, col));
            }
        }

        for (int row = 0; row < nRows; row++)
            result.rowNames[row] = this.rowNames[row];

        return result;
    }

    public JFrame linePlot(int colIndex)
    {

        // Initialize XY series
        XYSeries seriesXY = new XYSeries(this.colNames[colIndex]);
        for (int i = 0; i < nRows; i++)
        {
            seriesXY.add(i, this.data[colIndex][i]);
        }

        // add series to a data set
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        xyDataset.addSeries(seriesXY);

        String tableName = this.name == null ? "No Name" : this.name;

        // create line chart from data set
        JFreeChart chart = ChartFactory.createXYLineChart(tableName, "", "", xyDataset, PlotOrientation.VERTICAL, true,
                true, true);
        chart.fireChartChanged();

        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart, 500, 200, 500, 200, 500, 500, false, false, true, true, true,
                true);

//		this.getContentPane().add(  GuiUtil.createLineBoxPanel( chartPanel ) );
//		this.getContentPane().add(chartPanel);

        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        // add it to our application
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();

        // show !
        frame.setVisible(true);
        return frame;
    }

    public JFrame linePlot(int xColIndex, int[] yColIndices)
    {

        // Create the data set
        XYSeriesCollection xyDataset = new XYSeriesCollection();

        // add one series for each column
        for (int c = 0; c < yColIndices.length; c++)
        {
            int col = yColIndices[c];
            // Initialize XY series
            XYSeries seriesXY = new XYSeries(this.colNames[col]);
            for (int i = 0; i < nRows; i++)
            {
                seriesXY.add(this.data[xColIndex][i], this.data[yColIndices[c]][i]);
            }

            // add series to the data set
            xyDataset.addSeries(seriesXY);
        }

        String tableName = this.name == null ? "No Name" : this.name;

        // create line chart from data set
        JFreeChart chart = ChartFactory.createXYLineChart(tableName, this.colNames[xColIndex], "", xyDataset,
                PlotOrientation.VERTICAL, true, true, true);
        chart.fireChartChanged();

        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart, 500, 200, 500, 200, 500, 500, false, false, true, true, true,
                true);

//		this.getContentPane().add(  GuiUtil.createLineBoxPanel( chartPanel ) );
//		this.getContentPane().add(chartPanel);

        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        // add it to our application
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();

        // show !
        frame.setVisible(true);
        return frame;
    }

    /**
     * Display the content of the data table to standard output.
     */
    public void print()
    {

        // First display column headers
        if (this.colNames != null)
        {
            for (int c = 0; c < this.nCols; c++)
            {
                if (this.rowNames != null)
                    System.out.print("\t");
                System.out.print(this.colNames[c]);
            }
            System.out.println();
        }

        // Then display content of each row
        for (int r = 0; r < this.nRows; r++)
        {
            // row header
            if (this.rowNames != null)
                System.out.print(this.rowNames[r] + "\t");

            // row data
            for (int c = 0; c < this.nCols; c++)
            {
                System.out.print(this.data[c][r] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Opens a new JFrame and shows this table inside
     */
    public JFrame show()
    {
        // Need to cast to object array...
        Object[][] dats = new Object[this.nRows][this.nCols];
        for (int r = 0; r < this.nRows; r++)
        {
            for (int c = 0; c < this.nCols; c++)
            {
                dats[r][c] = this.data[c][r];
            }
        }

        // Create JTable instance
        JTable table = new JTable(dats, this.colNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Create the frame containing the table
        JFrame frame = new JFrame("Data Table");
        frame.setPreferredSize(new Dimension(400, 300));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Setup layout
        Container panel = frame.getContentPane();

        JScrollPane scrollPane = new JScrollPane(table);
        JTable rowTable = new RowNumberTable(table);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        // panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.pack();

        // show !
        frame.setVisible(true);
        return frame;
    }

    public void save(String fileName)
    {
        // Open a stream to write formatted text data
        PrintWriter writer;
        try
        {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Could not open file: " + fileName, ex);
        }

        // Write header name of each column
        writer.print("name");
        for (int c = 0; c < this.nCols; c++)
        {
            writer.print("\t" + this.colNames[c]);
        }
        writer.println();

        // Write header name of each column
        for (int r = 0; r < this.nRows; r++)
        {

            if (this.rowNames != null)
                writer.print(this.rowNames[r]);
            else
                writer.print(r);

            for (int c = 0; c < this.nCols; c++)
            {
                double val = this.getValue(r, c);
                String str = String.format(Locale.US, "%7.4f", val);
                writer.print("\t" + str);
            }

            writer.println();
        }

        // Closes the file
        writer.close();
    }

    /**
     * Small demonstration of the usage of the DataTable class.
     */
    public final static void main(String[] args)
    {
        DataTable table = new DataTable(15, 5);
        Random randgen = new Random(156);
        for (int c = 0; c < 5; c++)
        {
            for (int r = 0; r < 15; r++)
            {
                table.setValue(r, c, randgen.nextGaussian());
            }
        }

        table.setColumnNames(new String[] { "length", "area", "diameter", "number", "density" });
        table.show();
        table.linePlot(1, new int[] { 2, 3 });
        table.save("trySave.txt");
    }
}
