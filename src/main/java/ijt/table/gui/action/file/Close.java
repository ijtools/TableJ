/**
 * 
 */
package ijt.table.gui.action.file;

import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * @author dlegland
 *
 */
public class Close implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        System.out.println("close frame...");
        frame.dispose();
    }

}
