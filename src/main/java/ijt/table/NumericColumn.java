/**
 * 
 */
package ijt.table;

import java.util.Iterator;

/**
 * @author dlegland
 *
 */
public class NumericColumn extends Column implements Iterable<Double>
{
    // =============================================================
    // Class variables
    
    double[] data;
    
    
    // =============================================================
    // Constructors

    public NumericColumn(int size)
    {
        this.data = new double[size];
    }
    
    public NumericColumn(double[] data)
    {
        this.data = new double[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    
    // =============================================================
    // Implementation of Column methods
    
    //    public void copyValues(double[] values, int index);

    @Override
    public int size()
    {
        return data.length;
    }

    @Override
    public Object get(int row)
    {
        return new Double(data[row]);
    }

    @Override
    public void set(int row, Object obj)
    {
        if (obj instanceof Number)
        {
            data[row] = ((Number) obj).doubleValue();
        }
        else
        {
            throw new IllegalArgumentException("Input argument must be a number");
        }
    }

    /**
     * Returns the numerical value of the specified row.
     * 
     * @param row
     *            the row index
     * @return the value at specified row index
     */
    public double getValue(int row)
    {
        return data[row];
    }
    
    @Override
    public void setValue(int row, double value)
    {
        data[row] = value;
    }

    @Override
    public double[] getValues()
    {
        return this.data;
    }

    
    // =============================================================
    // Implementation of Iterable interface

    @Override
    public Iterator<Double> iterator()
    {
        return new RowIterator();
    }
    
    class RowIterator implements Iterator<Double>
    {
        int index = 0;
        
        public RowIterator()
        {
        }

        @Override
        public boolean hasNext()
        {
            return index < data.length;
        }

        @Override
        public Double next()
        {
            return data[index++];
        }
    }
}
