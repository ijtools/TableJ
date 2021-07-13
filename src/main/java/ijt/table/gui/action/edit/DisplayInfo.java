/**
 * 
 */
package ijt.table.gui.action.edit;

import java.util.ArrayList;
import java.util.Locale;

import ijt.table.CategoricalColumn;
import ijt.table.Column;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.frame.TextFrame;

/**
 * Display some information on the table into a new text frame.
 * 
 * @author dlegland
 *
 */
public class DisplayInfo implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();
        if (table == null)
        {
            return;
        }
        
        int nCols = table.columnCount();
        ArrayList<String> text = new ArrayList<String>();
        text.add("Name: " + table.getName());
        text.add("Row Count: " + table.rowCount());
        text.add("Column Count: " + nCols);
        
        for (int iCol = 0; iCol < Math.max(nCols, 50); iCol++)
        {
            Column col = table.getColumn(iCol);
            String name = table.getColumnName(iCol);
            text.add("[col " + iCol + "] " + name + " - " + summary(col));
        }
        if (nCols >= 50)
        {
            text.add("  (" + (nCols - 50) + " columns more)");
        }
        
        
        TextFrame tf = new TextFrame(frame.getJFrame(), "Table Information", text);
        tf.setVisible(true);
    }
    
    private String summary(Column col)
    {
        if (col instanceof NumericColumn)
        {
            return summaryNumeric((NumericColumn) col);
        }
        else if (col instanceof CategoricalColumn)
        {
            return summaryCategorical((CategoricalColumn) col);
        }
        else
        {
            throw new RuntimeException("Unknow type of column: " + col.getClass().getName());
        }
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
        
        return String.format(Locale.ENGLISH, "Numeric: [%g ; %g]", minVal, maxVal);
    }
    
    private String summaryCategorical(CategoricalColumn col)
    {
        int nLevels = col.levelCount();
        if (nLevels == 0)
        {
            return "Categorical with 0 levels.";
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
        return "Categorical with " + nLevels + " levels: " + levelString;
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
