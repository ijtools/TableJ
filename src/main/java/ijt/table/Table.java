/**
 * 
 */
package ijt.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ijt.table.gui.RowNumberTable;
import ijt.table.io.DelimitedTableWriter;
import ijt.table.process.Structure;
import ijt.table.process.SummaryStatistics;

/**
 * General interface for manipulation of data tables.
 * 
 * Data are organized by columns, and each column can be identified by its name.
 * Columns have different types: numeric or categorical. Other types may be
 * added in the future.
 * 
 * Table should ideally be able to manage any type of column, but specific
 * implementations may be limited to specific column types. For example, several
 * operators (PCA, KMeans...) requires table to contain only numeric columns.
 * 
 * Table interface also provides methods to access specific values, using
 * <code>(row, column)</code> indexing.
 * 
 * @author dlegland
 */
public interface Table
{
    // =============================================================
    // Static factories
    
    /**
     * Creates a new instance of Table with the specified dimensions.
     * 
     * @param nRows
     *            the number of rows of the new table
     * @param nCols
     *            the number of columns of the new table
     * @return a new Table instance
     */
    public static Table create(int nRows, int nCols)
    {
        return new DefaultTable(nRows, nCols);
    }
    
    /**
     * Creates a new instance of Table, based on the specified columns.
     * 
     * @param columns
     *            the columns composing the new table
     * @return a new Table instance
     */
    public static Table create(Column... columns)
    {
        int nRows = columns[0].size();
        int nCols = columns.length;

        // If all columns are numeric, should return a numeric table
        if (Arrays.stream(columns).allMatch(col -> (col instanceof NumericColumn)))
        {
            NumericTable table = new DefaultNumericTable(nRows, nCols);
            for (int c = 0; c < nCols; c++)
            {
                table.setColumn(c, columns[c]);
            }
            return table;
        }
        
        Table table = new DefaultTable(nRows, nCols);
        for (int c = 0; c < nCols; c++)
        {
            table.setColumn(c, columns[c]);
        }
        return table;
    }
    
    /**
     * Creates a new data table from a series of columns.
     * 
     * @param rowNames
     *            the names of the rows
     * @param columns
     *            the columns
     * @return a new Table instance            
     */
    public static Table create(String[] rowNames, Column... columns)
    {
        int nRows = rowNames.length;
        int nCols = columns.length;

        // If all columns are numeric, should return a numeric table
        if (Arrays.stream(columns).allMatch(col -> (col instanceof NumericColumn)))
        {
            NumericTable table = new DefaultNumericTable(nRows, nCols);
            for (int c = 0; c < nCols; c++)
            {
                table.setColumn(c, columns[c]);
            }
            return table;
        }
        
        Table table = new DefaultTable(nRows, nCols);
        for (int c = 0; c < nCols; c++)
        {
            table.setColumn(c, columns[c]);
        }
        
        table.setRowNames(rowNames);
        return table;
    }
    
    // =============================================================
    // Static methods
    
    public static boolean hasOnlyNumericColumns(Table table)
    {
        return table.columns().stream().allMatch(col -> col instanceof NumericColumn);
    }
    

    // =============================================================
    // Global methods
    
    /**
     * Returns a new Table containing for each column a short description of
     * column values.
     * 
     * @see ijt.table.process.Structure
     * 
     * @return a new Table with as many rows as the number of columns in this
     *         table.
     */
    public default Table structure()
    {
        return new Structure().process(this);
    }
    
    /**
     * Computes a new summary table, containing for each column a selection of
     * summary statistics.
     * 
     * @see ijt.table.process.SummaryStatistics
     * 
     * @return a table containing summary statistics for each numeric column.
     */
    public default Table summary()
    {
        if (!isNumeric())
        {
            throw new RuntimeException("Can not compute summary for tables with non-numeric columns.");
        }
        
        int nc = this.columnCount();
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
        String name = getName();
        if (name != null && !name.isEmpty())
        {
            res.setName(name + "-Summary");
        }
        else
        {
            res.setName("Summary");
        }
        
        return res;
    }
    
    /**
     * Creates a new empty table with the same type, the same column count and
     * names, and with the specified number of rows.
     * 
     * @param nRows
     *            the number of rows of the new table
     * @return a new table with specified number of rows and same number of
     *         columns as this table.
     */
    public Table newInstance(int nRows);
    
    /**
     * @return true if this table contains only numeric columns.
     */
    public default boolean isNumeric()
    {
        for (Column col : columns())
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
    public Object get(int row, int col);

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row the row index, 0-indexed
     * @param col the column index, 0-indexed
     * @return the value at the specified position
     */
    public double getValue(int row, int col);

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row     the row index, 0-indexed
     * @param colName the name of the column
     * @return the value at the specified position
     */
    public double getValue(int row, String colName);

    /**
     * Changes the value at the specified position
     * 
     * @param col   the column index, 0-indexed
     * @param row   the row index, 0-indexed
     * @param value the new value
     */
    public void setValue(int row, int col, double value);

    /**
     * Changes the value at the specified position
     * 
     * @param row     the row index, 0-indexed
     * @param colName the name of the column to modify
     * @param value   the new value
     */
    public void setValue(int row, String colName, double value);

    
    // =============================================================
    // Management of columns

    /**
     * Returns the number of columns (measurements, variables) in the data table.
     */
    public int columnCount();

    public Column getColumn(int index);

    public void setColumn(int colIndex, Column col);

    /**
     * Returns an entire column of the data table.
     */
    public double[] getColumnValues(int col);

    public String[] getColumnNames();
    
    public String getColumnName(int iCol);
    
    public void setColumnNames(String[] names);

    public void setColumnName(int colIndex, String name);

    /**
     * Returns the index of the first column with the specified name, or -1 if
     * there is no column with this name.
     * 
     * @param colName
     *            the name of a column.
     * @return the index of the first column with the specified name, or -1 if
     *         there is no column with this name.
     */
    public int findColumnIndex(String colName);
    
    /**
     * @return true if all columns has a valid name.
     */
    public boolean hasColumnNames();
    
    public int addColumn(String colName, Column column);

    
    // =============================================================
    // Management of rows

    /**
     * Returns the number of rows (individuals, observations) in the data table.
     */
    public int rowCount();

    /**
     * Returns an entire row of the data table.
     */
    public double[] getRowValues(int row);

    public String[] getRowNames();

    public String getRowName(int iRow);

    public void setRowNames(String[] names);

    public void setRowName(int rowIndex, String name);
    
    /**
     * Returns the (optional) label associated to the row axis, or null if none
     * is defined.
     * 
     * @return the (optional) label associated to the row axis, or null if none
     *         is defined.
     */
    public String getRowNameLabel();
    
    /**
     * Updates the label associated to the row axis.
     * 
     * @param label
     *            the new label associated to row names.
     */
    public void setRowNameLabel(String label);
    
    /**
     * Returns the index of the first row with the specified name, or -1 if
     * there is no row with this name.
     * 
     * @param rowName
     *            the name of a row.
     * @return the index of the first row with the specified name, or -1 if
     *         there is no row with this name.
     */
    public int findRowIndex(String rowName);

    /**
     * @return true if all columns has a valid name.
     */
    public boolean hasRowNames();
    

    // =============================================================
    // Management of Table meta-data

    /**
     * Changes the name of this data table.
     */
    public void setName(String name);

    /**
     * Returns the name of this data table
     */
    public String getName();


    // =============================================================
    // Operations on tables

    public default Table concatenateColumns(Table table)
    {
    	int nRows = this.rowCount();
        if (this.rowCount() != table.rowCount())
            throw new IllegalArgumentException("Tables must have the same number of rows");

        // allocate result
        int nCols1 = this.columnCount();
        int nCols2 = table.columnCount();
        int newNCols = nCols1 + nCols2;
        Table result = Table.create(nRows, newNCols);

        // add values of this table
        for (int col = 0; col < nCols1; col++)
        {
            result.setColumnName(col, this.getColumnName(col));
            for (int row = 0; row < nRows; row++)
            {
                result.setValue(row, col, this.getValue(row, col));
                result.setRowName(row, this.getRowName(row));
            }
        }

        // add values of other table
        for (int col = 0; col < nCols2; col++)
        {
            result.setColumnName(col + nCols1, this.getColumnName(col));
            for (int row = 0; row < nRows; row++)
            {
                result.setValue(row, col + nCols1, table.getValue(row, col));
            }
        }

        return result;
    }

    public default Table concatenateRows(Table table)
    {
        int nCols1 = this.columnCount();
        int nCols2 = table.columnCount();
        if (nCols1 != nCols2)
            throw new IllegalArgumentException("Tables must have the same number of columns");

        // allocate result
        int nRows1 = this.rowCount();
        int nRows2 = table.rowCount();
        int newNRows = nRows1 + nRows2;
        Table result = Table.create(newNRows, nCols1);

        for (int col = 0; col < nCols1; col++)
        {
            // add values of this table
            result.setColumnName(col, this.getColumnName(col));
            for (int row = 0; row < nRows1; row++)
            {
                result.setValue(row, col, this.getValue(row, col));
            }
            // add values of the other table
            for (int row = 0; row < nRows2; row++)
            {
                result.setValue(row + nRows1, col, table.getValue(row, col));
            }
        }

        for (int row = 0; row < nRows1; row++)
        {
            result.setRowName(row, this.getRowName(row));
        }
        for (int row = 0; row < nRows2; row++)
        {
            result.setRowName(row + nRows1, this.getRowName(row));
        }

        return result;
    }

    /**
     * Display the content of the data table to standard output.
     */
    public default void print(java.io.PrintStream stream)
    {
    	int nRows = this.rowCount();
    	int nCols = this.columnCount();
    	String[] colNames = this.getColumnNames();

        // First display column headers
        if (this.hasColumnNames())
        {
            if (hasRowNames())
                stream.print("\t");
            
            if (nCols > 0)
                stream.print(colNames[0]);
            for (int c = 1; c < nCols; c++)
            {
                stream.print("\t" + colNames[c]);
            }
            stream.println();
        }

        // Then display content of each row
        for (int r = 0; r < nRows; r++)
        {
            // row header
            if (hasRowNames())
                stream.print(this.getRowName(r) + "\t");

            // row data
            for (int c = 0; c < nCols; c++)
            {
                stream.print(getValue(r, c) + "\t");
            }
            stream.println();
        }
    }

    /**
     * Opens a new JFrame and shows this table inside
     */
    public default JFrame show()
    {
        // Need to cast to object array...
    	int nRows = this.rowCount();
    	int nCols = this.columnCount();
    	Object[][] dats = new Object[nRows][nCols];
        for (int r = 0; r < nRows; r++)
        {
            for (int c = 0; c < nCols; c++)
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
    
    /**
     * Writes the content of the current table into the specified file, using
     * CSV format.
     * 
     * @param fileName
     *            the name of the file to write in.
     * @throws IOException
     *             in case of I/O problem
     */
    public default void write(String fileName) throws IOException
    {
        DelimitedTableWriter tw = new DelimitedTableWriter(new File(fileName), ";");
        tw.writeTable(this);
    }
    
    /**
     * @return a Collection of the columns contained within this table.
     */
    public Collection<? extends Column> columns();
    
    // =============================================================
    // Demonstration program

    /**
     * Small demonstration of the usage of the DataTable class.
     */
    public static void main(String[] args)
    {
        Table table = Table.create(15, 5);
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
//        table.save("trySave.txt");
    }
}
