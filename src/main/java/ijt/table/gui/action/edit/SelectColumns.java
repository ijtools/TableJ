/**
 * 
 */
package ijt.table.gui.action.edit;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.dialogs.ListSelectionDialog;


/**
 * Opens a dialog to choose a selection of columns, and create a new table
 * containing only these columns.
 * 
 * @author dlegland
 */
public class SelectColumns implements TableFrameAction
{

    /* (non-Javadoc)
     * @see imago.gui.Plugin#run(imago.gui.ImagoFrame, java.lang.String)
     */
    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();

        // get general info from table
        int nRows = table.rowCount();
        String[] colNames = table.getColumnNames();

        // Opens a custom dialog to choose the name of columns to keep
        int[] columnIndices = ListSelectionDialog.selectElementIndices(frame.getJFrame(), 
                "Choose Columns:", 
                "Select Columns", 
                colNames);
        if (columnIndices == null || columnIndices.length == 0)
        {
            return;
        }
        
        // Default name for table
        String baseName = table.getName();
        if (baseName == null || baseName.length() == 0)
        {
            baseName = "data";
        }
        
        Table res = Table.create(nRows, columnIndices.length);
        for (int i = 0; i < columnIndices.length; i++)
        {
            int index = columnIndices[i];
            res.setColumn(i, table.getColumn(index));
            res.setColumnName(i, colNames[index]);
        }
        
        if (table.hasRowNames())
        {
            res.setRowNames(table.getRowNames());
        }
        res.setName(baseName + "-colSel");
        
        // add the new frame to the GUI
        TableFrame.create(res, frame);
    }
}
