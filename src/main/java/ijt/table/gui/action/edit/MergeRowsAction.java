/**
 * 
 */
package ijt.table.gui.action.edit;

import ij.IJ;
import ij.gui.GenericDialog;
import ijt.table.Table;
import ijt.table.Tables;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.TableManager;


/**
 * Merge two tables with same number of columns, by concatenating them
 * "vertically".
 * 
 * @author dlegland
 */
public class MergeRowsAction implements TableFrameAction
{

    /* (non-Javadoc)
     * @see imago.gui.Plugin#run(imago.gui.ImagoFrame, java.lang.String)
     */
    @Override
    public void run(TableFrame frame)
    {
        TableManager manager = TableManager.getInstance();
        String[] tableNames = manager.getTableNames().toArray(new String[] {});
        if (tableNames.length == 0)
        {
            IJ.error("No Available Tables", "No table can be found within TableJ TableManager.");
            return;
        }
        
        // Display dialog for choosing options
        GenericDialog dlg = new GenericDialog("Merge Table Rows", frame.getJFrame());
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
        
        if (table1.columnCount() != table2.columnCount())
        {
            IJ.error("Table Size Error", "Data tables have different number of columns");
            return;
        }
        
        Table res = Tables.mergeRows(table1,  table2);
        
        // add the new frame to the GUI
        TableFrame.create(res, frame);
    }
}
