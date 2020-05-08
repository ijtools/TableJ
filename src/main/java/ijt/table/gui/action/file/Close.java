/**
 * 
 */
package ijt.table.gui.action.file;

import ijt.table.gui.DataTableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * @author dlegland
 *
 */
public class Close implements TableFrameAction
{

    @Override
    public void run(DataTableFrame frame)
    {
        System.out.println("close frame...");
    }

}
