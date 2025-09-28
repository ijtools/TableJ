/**
 * 
 */
package ijt.table.gui.action.edit;

import ij.IJ;
import ij.gui.GenericDialog;
import ijt.table.Table;
import ijt.table.Tables;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableManager;


/**
 * Merge two tables with same number of rows, by concatenating them
 * "horizontally".
 * 
 * @author dlegland
 */
public class MergeColumnsAction implements FramePlugin
{

    /* (non-Javadoc)
     * @see imago.gui.Plugin#run(imago.gui.ImagoFrame, java.lang.String)
     */
    @Override
    public void run(BaseFrame frame, String options)
    {
        TableManager manager = TableManager.getInstance();
        String[] tableNames = manager.getTableNames().toArray(new String[] {});
        if (tableNames.length == 0)
        {
            IJ.error("No Available Tables", "No table can be found within TableJ TableManager.");
            return;
        }
        
        // Display dialog for choosing options
        GenericDialog dlg = new GenericDialog("Merge Table Columns", frame.getJFrame());
        dlg.addChoice("First Table", tableNames, tableNames[0]);
        dlg.addChoice("Second Table", tableNames, tableNames[0]);
        dlg.showDialog();

        // wait for user input
        if (dlg.wasCanceled()) 
        {
            return;
        }
        
        // concatenate the tables
        Table table1 = manager.getTable(dlg.getNextChoice());
        Table table2 = manager.getTable(dlg.getNextChoice());
        if (table1.rowCount() != table2.rowCount())
        {
            IJ.error("Table Size Error", "Data tables have different number of rows");
            return;
        }
        
        Table res = Tables.mergeColumns(table1,  table2);
        
        // add the new frame to the GUI
        TableFrame.create(res, frame);
    }
}
