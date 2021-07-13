/**
 * 
 */
package ijt.table;

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
    
    
    // =============================================================
    // Management of levels
    
    public String[] levelNames();
    
    public int levelCount();
}
