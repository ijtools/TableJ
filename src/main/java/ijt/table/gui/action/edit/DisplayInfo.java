/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * Display some information on the table into a new text frame.
 * 
 * @author dlegland
 *
 */
public class DisplayInfo implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();
        if (table == null)
        {
            return;
        }
        
        Table tableStructure = table.structure();
        frame.createNewTableFrame(tableStructure);
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
