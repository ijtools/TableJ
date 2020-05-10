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
public abstract class Column
{
    /**
     * @return the number of elements within this column
     */
    public abstract int size();
    
    /**
     * Returns the content of the i-th row as an Object.
     * 
     * @param row
     *            the row index within this column
     * @return the content at the specified row index
     */
    public abstract Object get(int row);
    
    public abstract void set(int row, Object obj);
    
    /**
     * Returns the value at the i-th row as a double.
     * 
     * @param row
     *            the row index within this column
     * @return the value at specified row index
     */
    public abstract double getValue(int row);
    
    public abstract void setValue(int row, double value);

    
    /**
     * @return the set of values within this column as an array of double.
     */
    public abstract double[] getValues();
}
