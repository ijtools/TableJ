/**
 * 
 */
package ijt.table.columns;

import java.util.Arrays;
import java.util.Iterator;

import ijt.table.NumericColumn;

/**
 * A column containing double values.
 * 
 * @author dlegland
 *
 */
public class DoubleColumn extends AbstractColumn implements NumericColumn, Iterable<Double>
{
    // =============================================================
    // Class variables
    
    /**
     * The numerical data contained within this column.
     */
    double[] data;
    
    
    // =============================================================
    // Constructors

    public DoubleColumn(String name, int size)
    {
        super(name);
        this.data = new double[size];
    }
    
    public DoubleColumn(String name, double[] data)
    {
        super(name);
        this.data = new double[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    
    // =============================================================
    // Methods

    @Override
    public NumericColumn newInstance(String name, double[] values)
    {
        return new DoubleColumn(name, values);
    }

    @Override
    public DoubleColumn newInstance(String name, int nRows)
    {
        return new DoubleColumn(name, nRows);
    }

    @Override
    public Object get(int row)
    {
        return Double.valueOf(data[row]);
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

    /**
     * Returns a defensive copy of the values within this column.
     * 
     * @return an array of double containing the values within this column.
     */
    @Override
    public double[] getValues()
    {
        return Arrays.copyOf(this.data, this.data.length);
    }


    // =============================================================
	// Implementation of Column methods
	
    @Override
    public String getString(int rowIndex)
    {
    	return Double.toString(data[rowIndex]);
    }
	
	@Override
	public int size()
	{
	    return data.length;
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
