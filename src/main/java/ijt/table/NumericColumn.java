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

    public static NumericColumn create(int size)
    {
        return new DoubleColumn(size);
    }
    
    public static NumericColumn create(double[] data)
    {
        return new DoubleColumn(data);
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
}
