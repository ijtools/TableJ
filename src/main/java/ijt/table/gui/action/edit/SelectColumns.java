/**
 * 
 */
package ijt.table.gui.action.edit;

import java.util.ArrayList;

import ij.gui.GenericDialog;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;


/**
 * @author dlegland
 *
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
        int nCols = table.columnCount();
        int nRows = table.rowCount();
        String[] colNames = table.getColumnNames();

        // Display dialog for choosing options
        GenericDialog dlg = new GenericDialog("Select Columns", frame.getJFrame());

        // add one check box for each column
        int nCols2 = Math.min(nCols, 20);
        for (int i = 0; i < nCols2; i++)
        {
            dlg.addCheckbox(colNames[i], false);
        }
        dlg.showDialog();

        // wait for user input
        if (dlg.wasCanceled()) 
        {
            return;
        }
        
        // Parse dialog contents
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < nCols2; i++)
        {
            if (dlg.getNextBoolean())
            {
                indices.add(i);
            }
        }
        
        // Default name for table
        String baseName = table.getName();
        if (baseName == null || baseName.length() == 0)
        {
            baseName = "data";
        }
        
        // convert arraylist to int[]
        int[] columnIndices = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++)
        {
            columnIndices[i] = indices.get(i);
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
