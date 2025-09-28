/**
 * 
 */
package ijt.table.gui.action.edit;

import ij.gui.GenericDialog;
import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableManager;

/**
 * @author dlegland
 *
 */
public class Rename implements FramePlugin
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
        
        String baseName = table.getName();
        GenericDialog gd = new GenericDialog("Rename table");
        gd.addStringField("New Name", baseName);
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }

        String newName = gd.getNextString();
        if (newName.isEmpty() || newName == baseName)
        {
            return;
        }
        
        TableManager mgr = TableManager.getInstance();
        mgr.removeTable(baseName);
        
        newName = mgr.createTableName(newName);
        table.setName(newName);
        mgr.addTable(table);
        
        // refresh name if frame title
        ((TableFrame) frame).updateTitle();
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
