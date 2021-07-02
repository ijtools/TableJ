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

import ijt.table.process.SummaryStatistics;

/**
 * A simple plain table for storing measurement results. 
 * Methods access data by row indexing first.
 * 
 * @author David Legland
 *
 */
public interface Table extends Iterable<Column>
{
    // =============================================================
    // Static factories
    
    public static Table create(int nRows, int nCols)
    {
        return new DefaultTable(nRows, nCols);
    }
    
    
    // =============================================================
    // Global methods
    
    /**
     * @return a table containing summary statistics for each numeric column.
     */
    public default Table summary()
    {
        if (!isNumeric())
        {
            throw new RuntimeException("Can not compute summary for tabe with non-numeric columns.");
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
     * @return true if this table contains only numeric columns.
     */
    public default boolean isNumeric()
    {
        for (Column col : this)
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
     * Returns the index of the first column with the specified name, or -1 if there
     * is no column with this name.
     * 
     * @param colName the name of a column.
     * @return the index of the first column with the specified name, or -1 if there
     *         is no column with this name.
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
    	String[] rowNames = this.getRowNames();
    	String[] colNames = this.getColumnNames();

        // First display column headers
        if (this.hasColumnNames())
        {
            if (rowNames != null)
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
            if (this.hasRowNames())
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

    public default void save(String fileName)
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

        int nRows = this.rowCount();
    	int nCols = this.columnCount();
    	
    	// Write header name of each column
        writer.print("name");
        for (int c = 0; c < nCols; c++)
        {
            writer.print("\t" + this.getColumnName(c));
        }
        writer.println();

        // Write header name of each column
        for (int r = 0; r < nRows; r++)
        {

            if (this.hasRowNames())
                writer.print(this.getRowName(r));
            else
                writer.print(r);

            for (int c = 0; c < nCols; c++)
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
