/**
 * 
 */
package ijt.table.gui.action.edit;

import ij.gui.GenericDialog;
import ijt.table.DataTable;
import ijt.table.TableManager;
import ijt.table.gui.DataTableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * @author dlegland
 *
 */
public class Rename implements TableFrameAction
{

    @Override
    public void run(DataTableFrame frame)
    {
        DataTable table = frame.getTable();
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
        
        // to update title display
        frame.repaint();
    }
    
    public boolean isAvailable(DataTableFrame frame)
    {
        return frame.getTable() != null;
    }
}
