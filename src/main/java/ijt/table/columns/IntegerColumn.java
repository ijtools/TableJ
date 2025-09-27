/**
 * 
 */
package ijt.table.columns;

import java.util.Arrays;
import java.util.Iterator;

import ijt.table.NumericColumn;

/**
 * A column containing integer values.
 * 
 * @author dlegland
 *
 */
public class IntegerColumn extends AbstractColumn implements NumericColumn
{
    // =============================================================
    // Class variables
    
    /**
     * The numerical data contained within this column.
     */
    int[] data;
    
    
    // =============================================================
    // Constructors

    public IntegerColumn(String name, int size)
    {
        super(name);
        this.data = new int[size];
    }
    
    public IntegerColumn(String name, int[] data)
    {
        super(name);
        this.data = new int[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    
    // =============================================================
    // New Methods

    /**
     * Returns a copy of the values within this column.
     * 
     * @return an array of int containing the values within this column.
     */
    public int[] getIntValues()
    {
        return Arrays.copyOf(data, data.length);
    }
    
    public NumericColumn newInstance(String name, int[] values)
    {
        return new IntegerColumn(name, values);
    }

    
    // =============================================================
    // Implementation of the NumericColumn interface

    @Override
    public NumericColumn newInstance(String name, double[] values)
    {
        int n = values.length;
        int[] data = new int[n];
        for (int i = 0; i < n; i++)
        {
            // add 0.5 for rounding
            data[i] = (int) (values[i] + 0.5);
        }
        return new IntegerColumn(name, data);
    }

    @Override
    public IntegerColumn newInstance(String name, int nRows)
    {
        return new IntegerColumn(name, nRows);
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
            data[row] = ((Number) obj).intValue();
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
        data[row] = (int) value;
    }

    /**
     * Returns a copy of the values within this column, converted into an array
     * of double.
     * 
     * @return an array of double containing the values within this column.
     */
    @Override
    public double[] getValues()
    {
        double[] res = new double[this.data.length];
        for (int i = 0; i < this.data.length; i++)
        {
            res[i] = this.data[i];
        }
        return res;
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
            return (double) data[index++];
        }
    }

}
