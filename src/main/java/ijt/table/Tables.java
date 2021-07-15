/**
 * 
 */
package ijt.table;

/**
 * A collection of utility methods for managing Table classes.
 * 
 * @author dlegland
 *
 */
public class Tables
{
    public final static Table mergeColumns(Table first, Table... others)
    {
        int nRows = first.rowCount();
        // initialize result
        Table res = Table.create(nRows, 0);
        if (first.hasRowNames())
        {
            res.setRowNames(first.getRowNames());
        }
                
        // first table
        for (int iCol = 0; iCol < first.columnCount(); iCol++)
        {
            res.addColumn(first.getColumnName(iCol), first.getColumn(iCol));
        }
        String name = "mergeColumns(" + first.getName();
        
        // add columns of other tables
        for (Table table : others)
        {
            for (int iCol = 0; iCol < table.columnCount(); iCol++)
            {
                res.addColumn(table.getColumnName(iCol), table.getColumn(iCol));
            }
            name += ", " + table.getName();
        }
        
        name += ")";
        res.setName(name);
        
        // return concatenation
        return res;
    }
    
    public static final Table crossTable(CategoricalColumn col1, CategoricalColumn col2)
    {
        int nRows = col1.size();
        if (col1.size() != nRows)
        {
            throw new RuntimeException("Both columns must have same number of rows");
        }
        int nLevels1 = col1.levelCount();
        int nLevels2 = col2.levelCount();
        
        Table res = Table.create(nLevels1, nLevels2);
        res.setRowNames(col1.levelNames());
        res.setColumnNames(col2.levelNames());
        
        for (int iRow = 0; iRow < nRows; iRow++)
        {
            int level1 = (int) col1.getValue(iRow);
            int level2 = (int) col2.getValue(iRow);
            res.setValue(level1, level2, res.getValue(level1, level2) + 1);
        }
        
        return res;
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Tables()
    {
    }
}
