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
     * Returns the content of the specified row as an Object.
     * 
     * @param row
     *            the row index within this column
     * @return the content at the specified row index
     */
    public Object get(int row);
    
    public void set(int row, Object obj);
    
    /**
     * Returns the value at the specified row as a double.
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
     * Returns the content of the specified row as a String.
     * 
     * @param row
     *            the row index within this column
     * @return a String representation of the content at the specified row index
     */
    public String getString(int row);
    
    /**
     * Returns the name of this column.
     * 
     * @return the name of this column.
     */
    public String getName();

    /**
     * Changes the name of this column (optional operation).
     * 
     * @param newName
     *            The new name of the column.
     */
    public void setName(String newName);
    
    /**
	 * @return the number of elements within this column
	 */
	public int size();

	/**
     * Creates a new instance of column with the same type, with the specified number of rows.
     *  
     * @param nRows the number of rows of the new column
     * @return a new Column instance with the same data type as this column.  
     */
    public Column newInstance(int nRows);
}
