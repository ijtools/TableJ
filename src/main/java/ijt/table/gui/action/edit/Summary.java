/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
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
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();
        if (table == null)
        {
            return;
        }
        
        Table res = table.summary();
        TableFrame.create(res, frame);
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
