/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;

/**
 * Clear the row names of the current table.
 * 
 * @author dlegland
 *
 */
public class ClearRowNames implements FramePlugin
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
