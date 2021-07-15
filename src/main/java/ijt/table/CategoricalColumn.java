/**
 * 
 */
package ijt.table;

import java.util.ArrayList;

import ijt.table.columns.IndexedStringColumn;

/**
 * @author dlegland
 *
 */
public interface CategoricalColumn extends Column, Iterable<String>
{
    // =============================================================
    // Constructors
    
    public static CategoricalColumn create(int size)
    {
        return new IndexedStringColumn(size);
    }

    public static CategoricalColumn create(String[] levels)
    {
    	return new IndexedStringColumn(levels);
    }
    
    /**
     * Converts a column into a categorical table.
     * 
     * @param column
     *            the column to convert.
     * @return an instance of CategoricalColumn based on input table.
     */
    public static CategoricalColumn convert(Column column)
    {
        if (column instanceof CategoricalColumn)
        {
            return (CategoricalColumn) column;
        }
        
        int nRows = column.size();
        ArrayList<String> levels = new ArrayList<String>(nRows);
        for (int iRow = 0; iRow < nRows; iRow++)
        {
            levels.add("" + column.getValue(iRow));
        }
        
        return new IndexedStringColumn(levels.toArray(new String[] {}));
    }

    
    // =============================================================
    // Management of levels
    
    public String[] levelNames();
    
    public int levelCount();
}
