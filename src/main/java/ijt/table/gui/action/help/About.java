/**
 * 
 */
package ijt.table.gui.action.help;

import ijt.table.gui.DataTableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * @author dlegland
 *
 */
public class About implements TableFrameAction
{

    @Override
    public void run(DataTableFrame frame)
    {
        System.out.println("About...");
    }

}
