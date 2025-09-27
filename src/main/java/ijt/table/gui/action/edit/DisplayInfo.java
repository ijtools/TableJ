/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;

/**
 * Display some information on the table into a new text frame.
 * 
 * @author dlegland
 *
 */
public class DisplayInfo implements FramePlugin
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
        
        Table tableStructure = table.structure();
        TableFrame.create(tableStructure, frame);
    }
    
    
    public boolean isAvailable(BaseFrame frame)
    {
        if (!(frame instanceof TableFrame)) return false;
        return ((TableFrame) frame).getTable() != null;
    }
}
