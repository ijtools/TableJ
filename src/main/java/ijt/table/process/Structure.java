/**
 * 
 */
package ijt.table.process;

import java.util.Locale;

import ijt.table.CategoricalColumn;
import ijt.table.Column;
import ijt.table.NumericColumn;
import ijt.table.Table;

/**
 * @author dlegland
 *
 */
public class Structure
{
    /**
     * Creates a new table describing the structure of the input table.
     * @param table
     * @return
     */
    public Table process(Table table)
    {
        int nCols = table.columnCount();
        
        Table res = Table.create(nCols, 0);
        res.setName(table.getName() + "-Structure");
        
        res.addColumn("Name", CategoricalColumn.create(table.getColumnNames()));
        
        String[] colTypes = new String[nCols];
        String[] colDesc = new String[nCols];
        for (int i = 0; i < nCols; i++)
        {
            Column column = table.getColumn(i);
            if (column instanceof NumericColumn)
            {
                colTypes[i] = "Numeric";
                colDesc[i] = summaryNumeric((NumericColumn) column);
            }
            else
            {
                colTypes[i] = "Categorical";
                colDesc[i] = summaryCategorical((CategoricalColumn) column);
            }
        }
        res.addColumn("Type", CategoricalColumn.create(colTypes));
        res.addColumn("Desc.", CategoricalColumn.create(colDesc));
        
        return res;
    }

    private String summaryNumeric(NumericColumn col)
    {
        double minVal = Double.POSITIVE_INFINITY;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (double v : col)
        {
            if (Double.isFinite(v))
            {
                minVal = Math.min(v, minVal);
                maxVal = Math.max(v, maxVal);
            }
        }
        
        return String.format(Locale.ENGLISH, "[%g ; %g]", minVal, maxVal);
    }
    
    private String summaryCategorical(CategoricalColumn col)
    {
        int nLevels = col.levelCount();
        if (nLevels == 0)
        {
            return "No levels.";
        }
        
        String[] levels = col.levelNames();
        String levelString = "[" + levels[0];
        for (int iLev = 1; iLev < Math.max(nLevels, 3); iLev++)
        {
            levelString += ", " + levels[iLev];
        }
        if (nLevels > 3)
        {
            levelString+= ", ...";
        }
        levelString += "]";
        return "" + nLevels + " levels: " + levelString;
    }
    

}
