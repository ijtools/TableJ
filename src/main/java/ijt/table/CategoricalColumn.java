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
    // static factories
    
    public static CategoricalColumn create(String name, int size)
    {
        return new IndexedStringColumn(name, size);
    }

    public static CategoricalColumn create(String name, String[] levels)
    {
    	return new IndexedStringColumn(name, levels);
    }
    
    /**
     * Converts an arbitrary column into a categorical column. If the input
     * column is already an instance of CategoricalColumn, it is simply
     * returned.
     * 
     * @param column
     *            the column to convert.
     * @return an instance of CategoricalColumn based on the input column.
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
        
        return new IndexedStringColumn(column.getName(), levels.toArray(new String[] {}));
    }

    
    // =============================================================
    // Management of levels
    
    public String[] levelNames();
    
    /**
     * Returns the level index at the specified row.
     * 
     * @param row
     *            the row index
     * @return the level index at the specified row
     */
    public int getLevelIndex(int row);

    /**
     * Changes the level index at the specified row.
     * 
     * @param row
     *            the row index
     * @param index
     *            the new index at the specified row
     */
    public void setLevelIndex(int row, int index);


    public int levelCount();
    
    @Override
    public default String getString(int rowIndex)
    {
    	return levelNames()[getLevelIndex(rowIndex)];
    }
}
