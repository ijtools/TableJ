/**
 * 
 */
package ijt.table.gui.action.process;

import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;

/**
 * Computes correlation matrix of columns within a data table.
 * 
 * @author dlegland
 *
 */
public class CorrelationMatrix implements FramePlugin
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
        
        // Check all columns are numeric
        if(!Table.hasOnlyNumericColumns(table))
        {
            throw new IllegalArgumentException("Requires table with numeric columns only");
        }
        
        Table res = new ijt.table.process.CorrelationMatrix().process(table);
        TableFrame.create(res, frame);
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        Table table = frame.getTable();
        if (table == null) return false;
        return Table.hasOnlyNumericColumns(table);
    }
}
