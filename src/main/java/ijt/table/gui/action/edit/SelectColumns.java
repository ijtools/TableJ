/**
 * 
 */
package ijt.table.gui.action.edit;

import java.util.stream.Stream;

import ijt.table.Table;
import ijt.table.TableJ;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;
import ijt.table.gui.dialogs.ListSelectionDialog;


/**
 * Opens a dialog to choose a selection of columns, and create a new table
 * containing only these columns.
 * 
 * @author dlegland
 */
public class SelectColumns implements FramePlugin
{

    /* (non-Javadoc)
     * @see imago.gui.Plugin#run(imago.gui.ImagoFrame, java.lang.String)
     */
    @Override
    public void run(BaseFrame frame, String options)
    {
        if (!(frame instanceof TableFrame)) return;
        Table table = ((TableFrame) frame).getTable();

        // get general info from table
        int nRows = table.rowCount();
        String[] colNames = table.getColumnNames();

        int[] columnIndices = null;
        if (!options.isEmpty())
        {
            String[] tokens = options.split("=", 2);
            String value = tokens[1].trim();
            value = value.substring(1, value.length()-1);
            columnIndices = Stream.of(value.split(","))
                    .mapToInt(token -> Integer.parseInt(token.trim()))
                    .toArray();
        }
        else
        {
            // Opens a custom dialog to choose the name of columns to keep
            columnIndices = ListSelectionDialog.selectElementIndices(frame.getJFrame(), 
                    "Choose Columns:", 
                    "Select Columns", 
                    colNames);
        }
        
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
            res.setRowNameLabel(table.getRowNameLabel());
        }
        res.setName(baseName + "-colSel");
        
        // add the new frame to the GUI
        TableFrame.create(res, frame);
        
        options = "columnIndices={" + createIndexListString(columnIndices) + "}";
        TableJ.recordCommand(getClass(), table, options);
    }
    
    private static final String createIndexListString(int[] indices)
    {
        if (indices.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(indices[0]);
        for (int i = 1; i < indices.length; i++)
        {
            sb.append(", " + indices[i]);
        }
        return sb.toString();
    }
}
