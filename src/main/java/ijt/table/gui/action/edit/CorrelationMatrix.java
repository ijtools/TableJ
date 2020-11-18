/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.DataTable;
import ijt.table.gui.DataTableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * Computes correlation matrix of columns within a data table.
 * 
 * @author dlegland
 *
 */
public class CorrelationMatrix implements TableFrameAction
{

    @Override
    public void run(DataTableFrame frame)
    {
        DataTable table = frame.getTable();
        if (table == null)
        {
            return;
        }
        
        DataTable res = new ijt.table.process.CorrelationMatrix().process(table);
        frame.createNewTableFrame(res);
    }
    
    public boolean isAvailable(DataTableFrame frame)
    {
        return frame.getTable() != null;
    }
}
