/**
 * 
 */
package ijt.table;

import java.util.ArrayList;
import java.util.Iterator;

import ijt.table.columns.DoubleColumn;


/**
 * Default implementation for a table with columns from any type.
 * 
 * @author dlegland
 *
 */
public class DefaultTable implements Table
{
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

    /**
     * The label associated to row names axis.
     */
    String rowNameLabel = null;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new data table with the given number of rows and columns.
     * 
     * @param nRows
     *            the number of rows
     * @param nCols
     *            the number of columns
     */
    public DefaultTable(int nRows, int nCols)
    {
        this.nCols = nCols;
        this.nRows = nRows;

        this.columns = new ArrayList<Column>(nCols);
        for (int c = 0; c < nCols; c++)
        {
            this.columns.add(new DoubleColumn(nRows));
        }
        
        initColNames();
        initRowNames();
    }

    public DefaultTable(double[][] data)
    {
        initData(data);
        this.nCols = data.length;

        initColNames();
        initRowNames();
    }

    public DefaultTable(double[][] data, String[] colNames, String[] rowNames)
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
            this.columns.add(new DoubleColumn(data[c]));
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

    @Override
    public Table newInstance(int nRows)
    {
        int nCols = columnCount();
        Table res = new DefaultTable(nRows, nCols);
        for (int iCol = 0; iCol < this.columnCount(); iCol++)
        {
            res.setColumn(iCol, this.getColumn(iCol).newInstance(nRows));
        }
        res.setColumnNames(this.getColumnNames());
        
        return res;
    }
    

    // =============================================================
    // Management of columns

    /**
     * Returns the number of columns (measurements, variables) in the data table.
     */
    @Override
    public int columnCount()
    {
        return this.nCols;
    }

    @Override
    public Column getColumn(int index)
    {
        return this.columns.get(index);
    }

    @Override
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
    @Override
    public double[] getColumnValues(int col)
    {
        return columns.get(col).getValues();
    }

    @Override
    public String[] getColumnNames()
    {
        String[] names = new String[this.nCols];
        for (int c = 0; c < this.nCols; c++)
        {
            names[c] = this.colNames.get(c);
        }
        return names;
    }
    
    @Override
    public String getColumnName(int iCol)
    {
        return this.colNames.get(iCol);
    }
    
    @Override
    public void setColumnNames(String[] names)
    {
        if (names.length != this.nCols)
            throw new IllegalArgumentException("String array must have same length as the number of columns.");

        this.colNames.clear();
        for (String name : names)
            this.colNames.add(name);
    }

    @Override
    public void setColumnName(int colIndex, String name)
    {
        if (colIndex >= this.nCols)
            throw new IllegalArgumentException(
                    "Index column greater than column number: " + colIndex + ">" + this.nCols);
        this.colNames.set(colIndex, name);
    }

    @Override
    public int findColumnIndex(String colName)
    {
        if (this.colNames.isEmpty())
        {
            throw new IllegalArgumentException("Can not retrieve column index when names are not initialized");
        }
        int index = this.colNames.indexOf(colName);
        if (index < 0)
        {
            throw new IllegalArgumentException("Could not find index of column with name: " + colName);
        }
        return index;
    }
    

    /**
     * @return true if all columns has a valid name.
     */
    @Override
    public boolean hasColumnNames()
    {
        for (String name : this.colNames)
        {
            if (name == null || name.isEmpty())
                return false;
        }
        
        return true;
    }
    
    @Override
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
    @Override
    public int rowCount()
    {
        return this.nRows;
    }

    /**
     * Returns an entire row of the data table.
     */
    @Override
    public double[] getRowValues(int row)
    {
        double[] res = new double[this.nCols];
        for (int c = 0; c < this.nCols; c++)
        {
            res[c] = this.columns.get(c).getValue(row);
        }
        return res;
    }

    @Override
    public String[] getRowNames()
    {
        return this.rowNames.toArray(new String[0]);
    }

    @Override
    public String getRowName(int iRow)
    {
        return this.rowNames.get(iRow);
    }

    @Override
    public void setRowNames(String[] names)
    {
        if (names.length != this.nRows)
            throw new IllegalArgumentException("String array must have same length as the number of rows.");

        this.rowNames.clear();
        for (String name : names)
            this.rowNames.add(name);
    }

    @Override
    public void setRowName(int rowIndex, String name)
    {
        if (rowIndex >= this.nRows)
            throw new IllegalArgumentException(
                    "Row index greater than row number: " + rowIndex + ">" + this.nRows);
        this.rowNames.set(rowIndex, name);
    }
    
    @Override
    public String getRowNameLabel()
    {
        return this.rowNameLabel;
    }

    @Override
    public void setRowNameLabel(String label)
    {
        this.rowNameLabel = label;
    }

    @Override
    public int findRowIndex(String rowName)
    {
        return this.rowNames.indexOf(rowName);
    }

    /**
     * @return true if all columns has a valid name.
     */
    @Override
    public boolean hasRowNames()
    {
        return !this.rowNames.isEmpty();
    }
    

    // =============================================================
    // Other Meta-Data management

    /**
     * Changes the name of this data table.
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of this data table
     */
    @Override
    public String getName()
    {
        return this.name;
    }


    // =============================================================
    // Data management

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row
     *            the row index, 0-indexed
     * @param col
     *            the column index, 0-indexed
     * @return the value at the specified position
     */
    @Override
    public Object get(int row, int col)
    {
        return this.columns.get(col).get(row);
    }

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row
     *            the row index, 0-indexed
     * @param col
     *            the column index, 0-indexed
     * @return the value at the specified position
     */
    @Override
    public double getValue(int row, int col)
    {
        return this.columns.get(col).getValue(row);
    }

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row
     *            the row index, 0-indexed
     * @param colName
     *            the name of the column
     * @return the value at the specified position
     */
    @Override
    public double getValue(int row, String colName)
    {
        int col = this.findColumnIndex(colName);
        return this.columns.get(col).getValue(row);
    }

    /**
     * Changes the value at the specified position
     * 
     * @param col
     *            the column index, 0-indexed
     * @param row
     *            the row index, 0-indexed
     * @param value
     *            the new value
     */
    @Override
    public void setValue(int row, int col, double value)
    {
        this.columns.get(col).setValue(row, value);
    }
    
    @Override
    public Iterable<Column> columns()
    {
        return new Iterable<Column>()
        {
            @Override
            public Iterator<Column> iterator()
            {
                return new ColumnIterator();
            }
        };
    }
    
    /**
     * Changes the value at the specified position
     * 
     * @param row
     *            the row index, 0-indexed
     * @param colName
     *            the name of the column to modify
     * @param value
     *            the new value
     */
    @Override
    public void setValue(int row, String colName, double value)
    {
        int col = this.findColumnIndex(colName);
        this.columns.get(col).setValue(row, value);
    }

    
    // =============================================================
    // Implementation of column iterator

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
}
