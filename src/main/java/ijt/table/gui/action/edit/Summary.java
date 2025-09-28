/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;

/**
 * Computes several summary statistics of a data table.
 * 
 * @author dlegland
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
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
