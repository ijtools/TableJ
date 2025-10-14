/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.TableJ;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;

/**
 * Computes several summary statistics of a data table. Requires the table to
 * contain only numeric columns.
 * 
 */
public class Summary implements FramePlugin
{

    @Override
    public void run(BaseFrame frame, String options)
    {
        if (!(frame instanceof TableFrame)) return;
        Table table = ((TableFrame) frame).getTable();
        if (table == null)
        {
            return;
        }
        
        Table res = table.summary();
        TableFrame.create(res, frame);
        
        TableJ.recordCommand(getClass(), table, options);
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        Table table = frame.getTable();
        if (table == null) return false;
        return Table.hasOnlyNumericColumns(table);
    }
}
