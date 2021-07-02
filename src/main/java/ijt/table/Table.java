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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ijt.table.process.SummaryStatistics;

/**
 * A simple plain table for storing measurement results. 
 * Methods access data by row indexing first.
 * 
 * @author David Legland
 *
 */
public class Table implements Iterable<Column>
{
    // =============================================================
    // Static factories
    
    public static final Table create(int nRows, int nCols)
    {
        return new Table(nRows, nCols);
    }
    
    
    // =============================================================
    // Class variables

    /** Number of columns in the table. */
    int nCols;

    /** Number of rows in the table. */
    int nRows;

    /** The list of columns composing this table. */
    ArrayList<Column> columns;
  
    /**
     * The name of the table (can be null).
     */
    String name = null;

    /** 
     * The names of the columns.
     */
    ArrayList<String> colNames;

    /** 
     * The names of the rows.
     */
    ArrayList<String> rowNames;

    
    // =============================================================
    // Constructors

    /**
     * Creates a new data table with the given number of rows and columns.
     * 
     * @param nRows the number of rows
     * @param nCols the number of columns
     */
    public Table(int nRows, int nCols)
    {
        this.nCols = nCols;
        this.nRows = nRows;

        this.columns = new ArrayList<Column>(nCols);
        for (int c = 0; c < nCols; c++)
            this.columns.add(new NumericColumn(nRows));

        initColNames();
        initRowNames();
    }

    public Table(double[][] data)
    {
        initData(data);
        this.nCols = data.length;

        initColNames();
        initRowNames();
    }

    public Table(double[][] data, String[] colNames, String[] rowNames)
    {
        initData(data);

        setColumnNames(colNames);
        initRowNames();
        setRowNames(rowNames);
    }

    /**
     * Initializes class variables columns, nCols and nRows.
     * 
     * @param data the data to populate the table
     */
    private void initData(double[][] data)
    {
        // setup table size
        this.nCols = data.length;
        this.nRows = this.nCols > 0 ? data[0].length : -1;

        // init empty numerical columns
        this.columns = new ArrayList<Column>(nCols);
        for (int c = 0; c < nCols; c++)
        {
            this.columns.add(new NumericColumn(data[c]));
        }
    }
    
    /**
     * Initialize column names with index 1-based.
     */
    private void initColNames()
    {
        this.colNames = new ArrayList<String>(nCols);
        for (int c = 0; c < this.nCols; c++)
            this.colNames.add("C" + Integer.toString(c + 1));
    }

    /**
     * Initialize row names to an empty array
     */
    private void initRowNames()
    {
        this.rowNames = new ArrayList<String>(0);
    }

    
    // =============================================================
    // Global methods
    
    /**
     * @return a table containing summary statistics for each numeric column.
     */
    public Table summary()
    {
        if (!isNumeric())
        {
            throw new RuntimeException("Can not compute summary for tabe with non-numeric columns.");
        }
        
        int nc = this.nCols;
        int nr = 5;
        
        Table res = Table.create(nr, nc);
        for (int c = 0; c < nc; c++)
        {
            NumericColumn col = (NumericColumn) getColumn(c);
            res.setValue(0, c, SummaryStatistics.min(col));
            res.setValue(1, c, SummaryStatistics.mean(col));
            res.setValue(2, c, SummaryStatistics.median(col));
            res.setValue(3, c, SummaryStatistics.std(col));
            res.setValue(4, c, SummaryStatistics.max(col));
        }
        
        // setup names for columns and rows
        res.setColumnNames(this.getColumnNames());
        res.setRowNames(new String[] {"Min.", "Mean", "Median", "SD", "Max."});
        
        // setup name of summary table
        if (name != null && !name.isEmpty())
        {
            res.setName(this.name + "-Summary");
        }
        else
        {
            res.setName("Summary");
        }
        
        return res;
    }
    
    /**
     * @return true if this table contains only numeric columns.
     */
    public boolean isNumeric()
    {
        for (Column col : columns)
        {
            if (!(col instanceof NumericColumn))
            {
                return false;
            }
        }
        return true;
    }
    
    // =============================================================
    // Data management

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row the row index, 0-indexed
     * @param col the column index, 0-indexed
     * @return the value at the specified position
     */
    public Object get(int row, int col)
    {
        return this.columns.get(col).get(row);
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
        return this.columns.get(col).getValue(row);
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
        return this.columns.get(col).getValue(row);
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
        this.columns.get(col).setValue(row, value);
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
        this.columns.get(col).setValue(row, value);
    }

    
    // =============================================================
    // Management of columns

    /**
     * Returns the number of columns (measurements, variables) in the data table.
     */
    public int columnCount()
    {
        return this.nCols;
    }

    public Column getColumn(int index)
    {
        return this.columns.get(index);
    }

    public void setColumn(int colIndex, Column col)
    {
        if (col.size() != this.nRows)
        {
            throw new IllegalArgumentException("Column size does not match table row number: " + col.size());
        }
        this.columns.set(colIndex, col);
    }

    /**
     * Returns an entire column of the data table.
     */
    public double[] getColumnValues(int col)
    {
        return columns.get(col).getValues();
    }

    public String[] getColumnNames()
    {
        String[] names = new String[this.nCols];
        for (int c = 0; c < this.nCols; c++)
        {
            names[c] = this.colNames.get(c);
        }
        return names;
    }
    
    public String getColumnName(int iCol)
    {
        return this.colNames.get(iCol);
    }
    
    public void setColumnNames(String[] names)
    {
        if (names.length != this.nCols)
            throw new IllegalArgumentException("String array must have same length as the number of columns.");

        this.colNames.clear();
        for (String name : names)
            this.colNames.add(name);
    }

    public void setColumnName(int colIndex, String name)
    {
        if (colIndex >= this.nCols)
            throw new IllegalArgumentException(
                    "Index column greater than column number: " + colIndex + ">" + this.nCols);
        this.colNames.set(colIndex, name);
    }

    /**
     * Returns the index of the first column with the specified name, or -1 if there
     * is no column with this name.
     * 
     * @param colName the name of a column.
     * @return the index of the first column with the specified name, or -1 if there
     *         is no column with this name.
     */
    public int getColumnIndex(String colName)
    {
        if (colName == null)
            return -1;
        
        for (int c = 0; c < this.nCols; c++)
        {
            if (name.equals(this.colNames.get(c)))
                return c;
        }
        
        return -1;
    }
    
    /**
     * @return true if all columns has a valid name.
     */
    public boolean hasColumnNames()
    {
//        return !this.colNames.isEmpty();
        for (String name : this.colNames)
        {
            if (name == null || name.isEmpty())
                return false;
        }
        
        return true;
    }
    
    public int addColumn(String colName, Column column)
    {
        this.columns.add(column);
        this.nCols = this.columns.size();
        this.colNames.add(colName);
        return this.nCols - 1;
    }

    
    // =============================================================
    // Management of rows

    /**
     * Returns the number of rows (individuals, observations) in the data table.
     */
    public int rowCount()
    {
        return this.nRows;
    }

    /**
     * Returns an entire row of the data table.
     */
    public double[] getRowValues(int row)
    {
        double[] res = new double[this.nCols];
        for (int c = 0; c < this.nCols; c++)
        {
            res[c] = this.columns.get(c).getValue(row);
        }
        return res;
    }

    public String[] getRowNames()
    {
        return this.rowNames.toArray(new String[0]);
    }

    public String getRowName(int iRow)
    {
        return this.rowNames.get(iRow);
    }

    public void setRowNames(String[] names)
    {
        if (names.length != this.nRows)
            throw new IllegalArgumentException("String array must have same length as the number of rows.");

        this.rowNames.clear();
        for (String name : names)
            this.rowNames.add(name);
    }

    public void setRowName(int rowIndex, String name)
    {
        if (rowIndex >= this.nRows)
            throw new IllegalArgumentException(
                    "Row index greater than row number: " + rowIndex + ">" + this.nRows);
        this.rowNames.set(rowIndex, name);
    }
    
    /**
     * @return true if all columns has a valid name.
     */
    public boolean hasRowNames()
    {
        return !this.rowNames.isEmpty();
    }
    

    
    // =============================================================
    // Management of Table meta-data

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


    // =============================================================
    // Operations on tables

    public Table concatenateColumns(Table table)
    {
        if (this.nRows != table.nRows)
            throw new IllegalArgumentException("Data tables must have the same number of rows");

        // allocate result
        int newNCols = this.nCols + table.nCols;
        Table result = new Table(nRows, newNCols);

        // add values of this table
        for (int col = 0; col < nCols; col++)
        {
            result.setColumnName(col, this.getColumnName(col));
            for (int row = 0; row < nRows; row++)
            {
                result.setValue(row, col, this.getValue(row, col));
                result.setRowName(row, this.rowNames.get(row));
            }
        }

        // add values of other table
        for (int col = 0; col < table.nCols; col++)
        {
            result.setColumnName(col + nCols, this.getColumnName(col));
            for (int row = 0; row < nRows; row++)
            {
                result.setValue(row, col + nCols, table.getValue(row, col));
            }
        }

        return result;
    }

    public Table concatenateRows(Table table)
    {
        if (this.nCols != table.nCols)
            throw new IllegalArgumentException("Data tables must have the same number of columns");

        // allocate result
        int newNRows = this.nRows + table.nRows;
        Table result = new Table(newNRows, nCols);

        for (int col = 0; col < nCols; col++)
        {
            // add values of this table
            result.setColumnName(col, this.getColumnName(col));
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
            result.setRowName(row, this.getRowName(row));

        return result;
    }

//    public JFrame linePlot(int colIndex)
//    {
//
//        // Initialize XY series
//        XYSeries seriesXY = new XYSeries(this.colNames[colIndex]);
//        for (int i = 0; i < nRows; i++)
//        {
//            seriesXY.add(i, this.data[colIndex][i]);
//        }
//
//        // add series to a data set
//        XYSeriesCollection xyDataset = new XYSeriesCollection();
//        xyDataset.addSeries(seriesXY);
//
//        String tableName = this.name == null ? "No Name" : this.name;
//
//        // create line chart from data set
//        JFreeChart chart = ChartFactory.createXYLineChart(tableName, "", "", xyDataset, PlotOrientation.VERTICAL, true,
//                true, true);
//        chart.fireChartChanged();
//
//        // we put the chart into a panel
//        ChartPanel chartPanel = new ChartPanel(chart, 500, 200, 500, 200, 500, 500, false, false, true, true, true,
//                true);
//
////		this.getContentPane().add(  GuiUtil.createLineBoxPanel( chartPanel ) );
////		this.getContentPane().add(chartPanel);
//
//        // default size
//        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//
//        // add it to our application
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.setContentPane(chartPanel);
//        frame.pack();
//
//        // show !
//        frame.setVisible(true);
//        return frame;
//    }
//
//    public JFrame linePlot(int xColIndex, int[] yColIndices)
//    {
//
//        // Create the data set
//        XYSeriesCollection xyDataset = new XYSeriesCollection();
//
//        // add one series for each column
//        for (int c = 0; c < yColIndices.length; c++)
//        {
//            int col = yColIndices[c];
//            // Initialize XY series
//            XYSeries seriesXY = new XYSeries(this.colNames[col]);
//            for (int i = 0; i < nRows; i++)
//            {
//                seriesXY.add(this.data[xColIndex][i], this.data[yColIndices[c]][i]);
//            }
//
//            // add series to the data set
//            xyDataset.addSeries(seriesXY);
//        }
//
//        String tableName = this.name == null ? "No Name" : this.name;
//
//        // create line chart from data set
//        JFreeChart chart = ChartFactory.createXYLineChart(tableName, this.colNames[xColIndex], "", xyDataset,
//                PlotOrientation.VERTICAL, true, true, true);
//        chart.fireChartChanged();
//
//        // we put the chart into a panel
//        ChartPanel chartPanel = new ChartPanel(chart, 500, 200, 500, 200, 500, 500, false, false, true, true, true,
//                true);
//
////		this.getContentPane().add(  GuiUtil.createLineBoxPanel( chartPanel ) );
////		this.getContentPane().add(chartPanel);
//
//        // default size
//        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//
//        // add it to our application
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.setContentPane(chartPanel);
//        frame.pack();
//
//        // show !
//        frame.setVisible(true);
//        return frame;
//    }

    /**
     * Display the content of the data table to standard output.
     */
    public void print(java.io.PrintStream stream)
    {

        // First display column headers
        if (this.hasColumnNames())
        {
            if (this.rowNames != null)
                stream.print("\t");
            
            if (nCols > 0)
                stream.print(this.colNames.get(0));
            for (int c = 1; c < this.nCols; c++)
            {
                stream.print("\t" + this.colNames.get(c));
            }
            stream.println();
        }

        // Then display content of each row
        for (int r = 0; r < this.nRows; r++)
        {
            // row header
            if (this.hasRowNames())
                stream.print(this.getRowName(r) + "\t");

            // row data
            for (Column col : columns)
            {
                stream.print(col.get(r) + "\t");
            }
            stream.println();
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
                dats[r][c] = this.get(r, c);
            }
        }

        // Create JTable instance
        JTable table = new JTable(dats, this.getColumnNames());
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
            writer.print("\t" + this.colNames.get(c));
        }
        writer.println();

        // Write header name of each column
        for (int r = 0; r < this.nRows; r++)
        {

            if (this.hasRowNames())
                writer.print(this.getRowName(r));
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


    // =============================================================
    // Implementation of Iterable<Column>

    @Override
    public Iterator<Column> iterator()
    {
        return new ColumnIterator();
    }

    class ColumnIterator implements Iterator<Column>
    {
        int index = 0;
        
        public ColumnIterator()
        {
        }

        @Override
        public boolean hasNext()
        {
            return index < nCols;
        }

        @Override
        public Column next()
        {
            return columns.get(index++);
        }
    }

    
    // =============================================================
    // Demonstration program

    /**
     * Small demonstration of the usage of the DataTable class.
     */
    public final static void main(String[] args)
    {
        Table table = new Table(15, 5);
        Random randgen = new Random(156);
        for (int c = 0; c < 5; c++)
        {
            for (int r = 0; r < 15; r++)
            {
                table.setValue(r, c, randgen.nextGaussian());
            }
        }

        table.setColumnNames(new String[] { "length", "area", "diameter", "number", "density" });
        
        table.print(System.out);
        table.show();
//        table.linePlot(1, new int[] { 2, 3 });
        table.save("trySave.txt");
    }
}
