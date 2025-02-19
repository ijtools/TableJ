/**
 * 
 */
package ijt.table.columns;

import java.util.ArrayList;
import java.util.Iterator;

import ijt.table.CategoricalColumn;
import ijt.table.Column;

/**
 * A column of strings that stores the list of strings, and the string index for
 * each row.
 * 
 * @author dlegland
 *
 */
public class IndexedStringColumn implements CategoricalColumn, Iterable<String>
{
    // =============================================================
    // Class variables
    
    int[] levelIndices;
    
    ArrayList<String> levelLabels;
    
    
    // =============================================================
    // Constructors
    
    public IndexedStringColumn(int size)
    {
        this.levelIndices = new int[size];
        this.levelLabels = new ArrayList<String>();
    }

    public IndexedStringColumn(String[] levels)
    {
        int size = levels.length;
        
        this.levelIndices = new int[size];
        
        ArrayList<String> uniqueLevels = new ArrayList<String>();
        for (int i = 0; i < levels.length; i++)
        {
            String str = levels[i];
            if (uniqueLevels.contains(str))
            {
                levelIndices[i] = uniqueLevels.indexOf(str);
            }
            else
            {
                uniqueLevels.add(str);
                levelIndices[i] = uniqueLevels.size() - 1;
            }
        }
        
        this.levelLabels = uniqueLevels;
    }
    
    
    // =============================================================
    // Management of levels
    
    public String[] levelNames()
    {
        return this.levelLabels.toArray(new String[0]);
    }
    
    @Override
    public int getLevelIndex(int row)
    {
        return this.levelIndices[row];
    }

    @Override
    public void setLevelIndex(int row, int index)
    {
        if (index >= levelLabels.size() || index < 0)
        {
            throw new IllegalArgumentException("Index must be smaller than level number");
        }
        this.levelIndices[row] = index;
    }


    public int levelCount()
    {
        return this.levelLabels.size();
    }

    
    // =============================================================
    // Implementation of Column methods
    
    @Override
    public int size()
    {
        return levelIndices.length;
    }

    @Override
    public Column newInstance(int nRows)
    {
        IndexedStringColumn col = new IndexedStringColumn(nRows);
        col.levelLabels.ensureCapacity(levelLabels.size());
        for (String label : this.levelLabels)
        {
            col.levelLabels.add(label);
        }
        return col;
    }

    @Override
    public Object get(int row)
    {
        return levelLabels.get(levelIndices[row]);
    }

    @Override
    public void set(int row, Object obj)
    {
        // check class
        if (!(obj instanceof String))
        {
            throw new IllegalArgumentException("value must be given as a string");
        }
        String level = (String) obj;

        // determine index of label, creating new one if necessary
        int index = levelLabels.indexOf(level);
        if (index < 0)
        {
            levelLabels.add(level);
            index = levelLabels.size() - 1;
        }
        
        levelIndices[row] = index;
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
        return levelIndices[row];
    }
    
    @Override
    public void setValue(int row, double value)
    {
        throw new RuntimeException("Cannot set double value for categorical column");
    }


    @Override
    public double[] getValues()
    {
        double[] res = new double[levelIndices.length];
        for (int i = 0; i < levelIndices.length; i++)
            res[i] = levelIndices[i];
        return res;
    }

    @Override
    public String getString(int rowIndex)
    {
    	return levelLabels.get(levelIndices[rowIndex]);
    }

    // =============================================================
    // Implementation of Iterable methods
    
    public Iterator<String> iterator()
    {
        return new RowIterator();
    }
    
    class RowIterator implements Iterator<String>
    {
        int index = 0;
        
        public RowIterator()
        {
        }

        @Override
        public boolean hasNext()
        {
            return index < levelIndices.length;
        }

        @Override
        public String next()
        {
            return levelLabels.get(levelIndices[index++]);
        }
    }

}
