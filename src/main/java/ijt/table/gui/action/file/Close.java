/**
 * 
 */
package ijt.table.gui.action.file;

import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableManager;

/**
 * Closes the current frame.
 */
public class Close implements FramePlugin
{
    @Override
    public void run(BaseFrame frame, String options)
    {
        if (frame instanceof TableFrame)
        {
            String tableName = ((TableFrame) frame).getTable().getName();
            TableManager manager = TableManager.getInstance();
            manager.removeTable(tableName);
        }
        frame.getJFrame().dispose();
    }
}
