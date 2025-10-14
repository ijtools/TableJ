/**
 * 
 */
package ijt.table.columns;

import java.util.List;

import ijt.table.Column;

/**
 * A column of strings. Contrary to the class {@code IndexedStringColumn},
 * Strings are not indexed.
 */
public class StringColumn extends AbstractColumn 
{
    // =============================================================
    // Class variables
    
    /**
     * The different String values within this column.
     */
    List<String> values;
    
    
    // =============================================================
    // Constructors

    public StringColumn(String name, String[] elements)
    {
        super(name);
        this.values = List.of(elements);
    }

    
    // =============================================================
    // Implementation of Column interface
    
    @Override
    public Object get(int row)
    {
        return values.get(row);
    }

    @Override
    public void set(int row, Object obj)
    {
        // check class
        if (!(obj instanceof String))
        {
            throw new IllegalArgumentException("value must be given as a string");
        }
        values.set(row, (String) obj);
    }

    @Override
    public double getValue(int row)
    {
        return Double.NaN;
    }

    @Override
    public void setValue(int row, double value)
    {
        throw new RuntimeException("Cannot set double value for string column");
    }

    @Override
    public double[] getValues()
    {
        double[] res = new double[values.size()];
        for (int i = 0; i < values.size(); i++)
            res[i] = Double.NaN;
        return res;
    }

    @Override
    public String getString(int row)
    {
        return values.get(row);
    }

    @Override
    public int size()
    {
        return values.size();
    }

    @Override
    public Column newInstance(String name, int nRows)
    {
        return new StringColumn(name, new String[nRows]);
    }
}
