/**
 * 
 */
package ijt.table;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import ijt.table.columns.DoubleColumn;


/**
 * Default implementation for a table with numeric columns.
 * 
 * @author dlegland
 *
 */
public class DefaultNumericTable implements NumericTable
{
    // =============================================================
    // Class variables

    /** Number of columns in the table. */
    int nCols;

    /** Number of rows in the table. */
    int nRows;

    /** The list of (numeric) columns composing this table. */
    ArrayList<NumericColumn> columns;
  
    /**
     * The name of the table (can be null).
     */
    String name = null;

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
    public DefaultNumericTable(int nRows, int nCols)
    {
        this.nCols = nCols;
        this.nRows = nRows;

        this.columns = new ArrayList<NumericColumn>(nCols);
        String[] colNames = defaultColNames(nCols);
        for (int c = 0; c < nCols; c++)
        {
            this.columns.add(new DoubleColumn(colNames[c], nRows));
        }
        
        initRowNames();
    }

    public DefaultNumericTable(double[][] data)
    {
        initData(data);
        this.nCols = data.length;

        initRowNames();
    }

    public DefaultNumericTable(double[][] data, String[] colNames, String[] rowNames)
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
        this.columns = new ArrayList<NumericColumn>(nCols);
        String[] colNames = defaultColNames(nCols);
        for (int c = 0; c < nCols; c++)
        {
            this.columns.add(new DoubleColumn(colNames[c], data[c]));
        }
    }
    
    /**
     * Initialize column names with index 1-based.
     */
    private static final String[] defaultColNames(int nCols)
    {
        String[] colNames = new String[nCols];
        for (int c = 0; c < nCols; c++)
        {
            colNames[c] = "C" + Integer.toString(c + 1);
        }
        return colNames;
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
        Table res = new DefaultNumericTable(nRows, nCols);
        for (int iCol = 0; iCol < this.columnCount(); iCol++)
        {
            Column col = this.getColumn(iCol);
            res.setColumn(iCol, col.newInstance(col.getName(), nRows));
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
    public NumericColumn getColumn(int index)
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
        if (!(col instanceof NumericColumn))
        {
            throw new IllegalArgumentException("Requires a numeric column as input");
        }
        this.columns.set(colIndex, (NumericColumn) col);
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
        return columns.stream().map(c -> c.getName()).toArray(String[]::new);
    }
    
    @Override
    public String getColumnName(int iCol)
    {
        return this.columns.get(iCol).getName();
    }
    
    @Override
    public void setColumnNames(String[] names)
    {
        if (names == null)
        {
            columns.stream().forEach(col -> col.setName(""));
            return;
        }
        
        if (names.length != this.nCols)
            throw new IllegalArgumentException("String array must have same length as the number of columns.");

        for (int i = 0; i < columns.size(); i++)
        {
            columns.get(i).setName(names[i]);
        }
    }

    @Override
    public void setColumnName(int colIndex, String name)
    {
        if (colIndex >= this.nCols)
            throw new IllegalArgumentException(
                    "Index column greater than column number: " + colIndex + ">" + this.nCols);
        this.columns.get(colIndex).setName(name);
    }

    @Override
    public int findColumnIndex(String colName)
    {
        for (int i = 0; i < columns.size(); i++)
        {
            if (colName.equals(columns.get(i).getName())) return i;
        }
        throw new IllegalArgumentException("Could not find index of column with name: " + colName);
    }
    

    /**
     * @return true if all columns has a valid name.
     */
    @Override
    public boolean hasColumnNames()
    {
        for (Column col : columns)
        {
            String name = col.getName();
            if (name == null || name.isEmpty())
                return false;
        }
        
        return true;
    }
    
    @Override
    public int addColumn(String colName, Column column)
    {
        if (!(column instanceof NumericColumn))
        {
            column = NumericColumn.convert(column);
        }
        this.columns.add((NumericColumn) column);
        this.nCols = this.columns.size();
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
    public Collection<NumericColumn> columns()
    {
        return new AbstractCollection<NumericColumn>()
        {
            @Override
            public int size()
            {
                return nCols;
            }
            
            @Override
            public Iterator<NumericColumn> iterator()
            {
                return Collections.unmodifiableList(columns).iterator();
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

    
    class ColumnIterator implements Iterator<NumericColumn>
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
        public NumericColumn next()
        {
            return columns.get(index++);
        }
    }
}
