/**
 * 
 */
package ijt.table.process;

import java.io.File;
import java.io.IOException;

import ijt.table.CategoricalColumn;
import ijt.table.Column;
import ijt.table.Table;
import ijt.table.io.DelimitedTableReader;
import ijt.table.io.TableReader;

/**
 * @author dlegland
 *
 */
public class Aggregate
{
    public Table process(Table table, CategoricalColumn groups)
    {
        int nRows = table.rowCount();
        int nCols = table.columnCount();
        
        if (groups.size() != nRows)
        {
            throw new RuntimeException("Number of group elements must match row number of table");
        }
        
        int nGroups = groups.levelCount();
        Table res = Table.create(nGroups, nCols);
        res.setColumnNames(table.getColumnNames());
        
        double[] groupSums = new double[nGroups];
        int[] groupCounts = new int[nGroups];
        
        for (int iCol = 0; iCol < nCols; iCol++)
        {
            // compute average for each group
            for (int iRow = 0; iRow < nRows; iRow++)
            {
                int gIndex = (int) groups.getValue(iRow);
                groupSums[gIndex] += table.getValue(iRow, iCol);
                groupCounts[gIndex] ++;
            }
            
            for (int iGroup = 0; iGroup < nGroups; iGroup++)
            {
                double avg = groupSums[iGroup] / groupCounts[iGroup];
                res.setValue(iGroup, iCol, avg);
            }
        }
        
        // populate res table from group names
        String[] groupNames = groups.levels();
        String[] rowNames = new String[nGroups];
        for (int iGroup = 0; iGroup < nGroups; iGroup++)
        {
            rowNames[iGroup] = groupNames[iGroup] + "-mean";
        }
        res.setRowNames(rowNames);
        
        return res;
    }
    
    public Table process(Table table, String groupColumnName)
    {
        int nRows = table.rowCount();
        int nCols = table.columnCount();
        
        // split table into data table and grouping column
        Table data = Table.create(nRows, 0);
        CategoricalColumn groups = null;
        for (int i = 0; i < nCols; i++)
        {
            Column col = table.getColumn(i);
            if (col instanceof CategoricalColumn)
            {
                if (groups != null)
                {
                    throw new RuntimeException("Table must contains only one categorical column.");
                }
                groups = (CategoricalColumn) col;
            }
            else
            {
                data.addColumn(table.getColumnName(i), table.getColumn(i));
            }
        }
        
        return process(data, groups);
    }
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException
    {
        String fileName = Aggregate.class.getResource("/tables/fisherIris.txt").getFile();
        
        TableReader reader = new DelimitedTableReader();
        Table table = reader.readTable(new File(fileName));
        
        int nRows = table.rowCount();
        Table data = Table.create(nRows, 4);
        for (int i = 0; i < 4; i++)
        {
            data.setColumnName(i, table.getColumnName(i));
            data.setColumn(i, table.getColumn(i));
        }
        Column species = table.getColumn(4);
        
        Table res = new Aggregate().process(data, (CategoricalColumn) species);
        
        res.print(System.out);
    }

}
