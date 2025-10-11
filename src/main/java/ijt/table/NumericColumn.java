/**
 * 
 */
package ijt.table;

import ijt.table.columns.DoubleColumn;
import ijt.table.columns.IntegerColumn;

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

    public static IntegerColumn create(String name, int[] data)
    {
        return new IntegerColumn(name, data);
    }
    
    /**
     * Converts an arbitrary column into a numeric column. If the input column
     * is already an instance of NumericColumn, it is simply returned.
     * 
     * @param column
     *            the column to convert.
     * @return an instance of CategoricalColumn based on input table.
     */
    public static NumericColumn convert(Column column)
    {
        if (column instanceof NumericColumn)
        {
            return (NumericColumn) column;
        }
        
        return NumericColumn.create(column.getName(), column.getValues());
    }

    
    // =============================================================
    // New methods
    
    /**
     * Returns the range of values within this numeric column, as an array of
     * doubles with two values. First value corresponds to minimum value in
     * column, second value to maximum.
     * 
     * @return a new array with two values corresponding to the minimum and the
     *         maximum.
     */
    public default double[] valueRange()
    {
        double vmin = Double.POSITIVE_INFINITY;
        double vmax = Double.NEGATIVE_INFINITY;
        for (double v : this)
        {
            vmin = Math.min(vmin,  v);
            vmax = Math.max(vmax,  v);
        }
        return new double[] {vmin, vmax};
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
