/**
 * 
 */
package ijt.table.gui.action.file;

import ij.measure.ResultsTable;
import ijt.table.Column;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;

/**
 * 
 */
public class ConvertToResultsTable implements FramePlugin
{

    @Override
    public void run(BaseFrame frame, String optionsString)
    {
        // basic check
        if (!(frame instanceof TableFrame)) return;
        
        // retrieve table data
        Table table = ((TableFrame) frame).getTable();
        int nr = table.rowCount();
        
        // create empty results table
        ResultsTable res = new ResultsTable(nr);
        
        // iterate over columns to update results
        for (Column col : table.columns())
        {
            if (col instanceof NumericColumn)
            {
                res.setValues(col.getName(), col.getValues());
            }
            else
            {
                String colName = col.getName();
                for (int r = 0; r < nr; r++)
                {
                    res.setValue(colName, r, col.getString(r));
                }
            }
        }
        
        // show the results Table
        res.show(table.getName());
    }

}
