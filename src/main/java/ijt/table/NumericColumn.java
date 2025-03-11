/**
 * 
 */
package ijt.table;

import ijt.table.columns.DoubleColumn;

/**
 * A Table column that contains only numerical values.
 *  
 * @author dlegland
 */
public interface NumericColumn extends Column, Iterable<Double>
{
    // =============================================================
    // static factories

    public static NumericColumn create(String name, int size)
    {
        return new DoubleColumn(name, size);
    }
    
    public static NumericColumn create(String name, double[] data)
    {
        return new DoubleColumn(name, data);
    }

    
    // =============================================================
    // Implementation of Column methods
 
    /**
     * Returns the numerical value of the specified row.
     * 
     * @param row
     *            the row index
     * @return the value at specified row index
     */
    public double getValue(int row);
    
    /**
     * Creates a new numeric column with the specified number of rows, and
     * propagating meta data.
     * 
     * @param name
     *            the name of the new column
     * @param values
     *            the numeric values to store in the new column
     * @return a new column containing numeric values
     */
    public NumericColumn newInstance(String name, double[] values);
    
    /**
     * Creates a new numeric column with the specified number of rows, and
     * propagating meta data.
     * 
     * @param name
     *            the name of the new column
     * @param nRows
     *            the number of rows of the new column
     * @return a new column containing numeric values
     */
    public NumericColumn newInstance(String name, int nRows);
}
