/**
 * 
 */
package ijt.table.gui.action.process;

import ij.gui.GenericDialog;
import ijt.table.CategoricalColumn;
import ijt.table.Column;
import ijt.table.Table;
import ijt.table.Tables;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;


/**
 * Computes the cross-table of two Categorical columns within a table.
 * 
 * @author dlegland
 */
public class CrossTableAction implements TableFrameAction
{

    /* (non-Javadoc)
     * @see imago.gui.Plugin#run(imago.gui.ImagoFrame, java.lang.String)
     */
    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();
        String[] colNames = table.getColumnNames();

        GenericDialog gd = new GenericDialog("Cross Table");
        gd.addChoice("First Column", colNames, colNames[0]);
        gd.addChoice("Second Column", colNames, colNames[0]);
        gd.showDialog();

        // wait for user input
        if (gd.wasCanceled()) 
        {
            return;
        }
        
        // extract information provided by user
        String colName1 = gd.getNextChoice();
        String colName2 = gd.getNextChoice();
        Column col1 = table.getColumn(table.findColumnIndex(colName1));
        Column col2 = table.getColumn(table.findColumnIndex(colName2));
        
        // ensure columns are categorical
        if (!(col1 instanceof CategoricalColumn))
        {
            col1 = CategoricalColumn.convert(col1);
        }
        if (!(col2 instanceof CategoricalColumn))
        {
            col2 = CategoricalColumn.convert(col2);
        }
        
        Table res = Tables.crossTable((CategoricalColumn) col1, (CategoricalColumn) col2);
        
        // add the new frame to the GUI
        TableFrame.create(res, frame);
    }
}
