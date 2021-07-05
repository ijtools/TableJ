/**
 * 
 */
package ijt.table.gui.action.edit;

import java.util.ArrayList;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.frame.TextFrame;

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
        
        ArrayList<String> text = new ArrayList<String>();
        text.add("Name: " + table.getName());
        text.add("Row Count: " + table.rowCount());
        text.add("Column Count: " + table.columnCount());
        
        
        TextFrame tf = new TextFrame(frame.getJFrame(), "Table Information", text);
        tf.setVisible(true);
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }
}
