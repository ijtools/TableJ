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
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Tables()
    {
    }
}
