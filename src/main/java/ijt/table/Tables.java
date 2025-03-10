/**
 * 
 */
package ijt.table;

import java.util.ArrayList;

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
     * @param firstTable
     *            the first table
     * @param others
     *            the other tables
     * @return the new table resulting from the concatenation
     */
    public static final Table mergeColumns(Table firstTable, Table... others)
    {
        int nRows = firstTable.rowCount();
        // initialize result
        Table res = Table.create(nRows, 0);
        if (firstTable.hasRowNames())
        {
            res.setRowNames(firstTable.getRowNames());
        }
                
        // first table
        for (int iCol = 0; iCol < firstTable.columnCount(); iCol++)
        {
            res.addColumn(firstTable.getColumnName(iCol), firstTable.getColumn(iCol));
        }
        String name = "mergeColumns(" + firstTable.getName();
        
        // add columns of other tables
        for (Table table : others)
        {
            if (table.rowCount() != firstTable.rowCount())
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
     * @param firstTable
     *            the first table
     * @param others
     *            the other tables
     * @return the new table resulting from the concatenation
     */
    public static final Table mergeRows(Table firstTable, Table... others)
    {
        int nCols = firstTable.columnCount();
        ArrayList<String> rowNames = null;
        if (firstTable.hasRowNames())
        {
            rowNames = new ArrayList<String>();
            for (String name : firstTable.getRowNames())
            {
                rowNames.add(name);
            }
        }
                
        // initialize columns from first table
        Column[] columns = new Column[nCols];
        for (int iCol = 0; iCol < nCols; iCol++)
        {
            columns[iCol] = firstTable.getColumn(iCol);
        }
        String resName = "mergeColumns(" + firstTable.getName();
        
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
            
            if (firstTable.hasRowNames())
            {
                for (String name : table.getRowNames())
                {
                    rowNames.add(name);
                }
            }
            resName += ", " + table.getName();
        }
        
        // create table of concatenated columns
        Table res = Table.create(columns);
        
        resName += ")";
        res.setName(resName);
        if (firstTable.hasRowNames())
        {
            res.setRowNames(rowNames.toArray(new String[0]));
            res.setRowNameLabel(firstTable.getRowNameLabel());
        }
        
        // return concatenation
        return res;
    }
    
    private static final Column mergeColumns(Column col1, Column col2)
    {
        if (col1 instanceof NumericColumn && col1 instanceof NumericColumn)
        {
            return mergeNumericColumns((NumericColumn) col1, (NumericColumn) col2);
        }
        else if (col1 instanceof CategoricalColumn && col1 instanceof CategoricalColumn)
        {
            return mergeCategoricalColumns((CategoricalColumn) col1, (CategoricalColumn) col2);
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
    
    private static final CategoricalColumn mergeCategoricalColumns(CategoricalColumn col1, CategoricalColumn col2)
    {
        int n1 = col1.size();
        int n2 = col2.size();
        String[] values = new String[n1 + n2];
        for (int i = 0; i < n1; i++)
        {
            values[i] = col1.getString(i);
        }
        for (int i = 0; i < n2; i++)
        {
            values[n1 + i] = col2.getString(i);
        }
        return CategoricalColumn.create(col1.getName(), values); // TODO: implement CategoricalColumn.newInstance();
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
