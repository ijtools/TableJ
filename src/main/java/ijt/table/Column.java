/**
 * 
 */
package ijt.table;

/**
 * The values in a Table column.
 * 
 * @author dlegland
 *
 */
public interface Column
{
    /**
     * @return the number of elements within this column
     */
    public int size();
    
    /**
     * Returns the content of the i-th row as an Object.
     * 
     * @param row
     *            the row index within this column
     * @return the content at the specified row index
     */
    public Object get(int row);
    
    public void set(int row, Object obj);
    
    /**
     * Returns the value at the i-th row as a double.
     * 
     * @param row
     *            the row index within this column
     * @return the value at specified row index
     */
    public double getValue(int row);
    
    public void setValue(int row, double value);

    
    /**
     * @return the set of values within this column as an array of double.
     */
    public double[] getValues();
    
    /**
     * Creates a new instance of column with the same type, with the specified number of rows.
     *  
     * @param nRows the number of rows of the new column
     * @return a new Column instance with the same data type as this column.  
     */
    public Column newInstance(int nRows);
}
