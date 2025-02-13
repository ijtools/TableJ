/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * Clear the row names of the current table.
 * 
 * @author dlegland
 *
 */
public class ClearRowNames implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();
        if (table == null)
        {
            return;
        }
        
        table.setRowNames(null);
        table.setRowNameLabel(null);
        
        // to update display
        frame.repaint();
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
