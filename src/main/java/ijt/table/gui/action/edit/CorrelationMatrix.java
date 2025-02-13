/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
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
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();
        if (table == null)
        {
            return;
        }
        
        Table res = new ijt.table.process.CorrelationMatrix().process(table);
        TableFrame.create(res, frame);
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
