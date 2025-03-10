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
    /**
     * Merges two or more tables with same number of rows by concatenating them
     * in the horizontal direction.
     * 
     * @param first
     *            the first table
     * @param others
     *            the other tables
     * @return the new table resulting from the concatenation
     */
    public static final Table mergeColumns(Table first, Table... others)
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
            if (table.rowCount() != first.rowCount())
            {
                throw new RuntimeException("Table \"" + table.getName() + "\" has different number of rows.");
            }
            
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
     * Merges two or more tables with same number of columns by concatenating
     * them in the vertical direction.
     * 
     * @param first
     *            the first table
     * @param others
     *            the other tables
     * @return the new table resulting from the concatenation
     */
    public static final Table mergeRows(Table first, Table... others)
    {
        int nCols = first.columnCount();
//        if (first.hasRowNames())
//        {
//            res.setRowNames(first.getRowNames());
//        }
                
        // initialize columns from first table
        Column[] columns = new Column[nCols];
        for (int iCol = 0; iCol < nCols; iCol++)
        {
            columns[iCol] = first.getColumn(iCol);
        }
        String resName = "mergeColumns(" + first.getName();
        
        // add columns of other tables
        for (Table table : others)
        {
            if (table.columnCount() != nCols)
            {
                throw new RuntimeException("Table \"" + table.getName() + "\" has different number of columns.");
            }
            
            for (int iCol = 0; iCol < nCols; iCol++)
            {
                columns[iCol] = mergeColumns(columns[iCol], table.getColumn(iCol));
            }
            
            resName += ", " + table.getName();
        }
        
        // create table of concatenated columns
        Table res = Table.create(columns);
        
        resName += ")";
        res.setName(resName);
        
        // return concatenation
        return res;
    }
    
    private static final Column mergeColumns(Column col1, Column col2)
    {
        if (col1 instanceof NumericColumn && col1 instanceof NumericColumn)
        {
            return mergeNumericColumns((NumericColumn) col1, (NumericColumn) col2);
        }
        else
        {
            throw new RuntimeException("Both columns must have same type, or type is not managed.");
        }
    }
    
    private static final NumericColumn mergeNumericColumns(NumericColumn col1, NumericColumn col2)
    {
        int n1 = col1.size();
        int n2 = col2.size();
        double[] values = new double[n1 + n2];
        System.arraycopy(col1.getValues(), 0, values, 0, n1);
        System.arraycopy(col2.getValues(), 0, values, n1, n2);
        return NumericColumn.create(col1.getName(), values); // TODO: implement NumericColumn.newInstance();
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
