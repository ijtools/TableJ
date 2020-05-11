/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.DataTable;
import ijt.table.gui.DataTableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * Computes several summary statistics of a data table.
 * 
 * @author dlegland
 *
 */
public class Summary implements TableFrameAction
{

    @Override
    public void run(DataTableFrame frame)
    {
        DataTable table = frame.getTable();
        if (table == null)
        {
            return;
        }
        
        DataTable res = table.summary();
        frame.createNewTableFrame(res);
    }
    
    public boolean isAvailable(DataTableFrame frame)
    {
        return frame.getTable() != null;
    }
}
