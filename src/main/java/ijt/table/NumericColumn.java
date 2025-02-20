/**
 * 
 */
package ijt.table;

import ijt.table.columns.DoubleColumn;

/**
 * @author dlegland
 *
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
    
    
    public NumericColumn newInstance(String name, int nRows);
}
