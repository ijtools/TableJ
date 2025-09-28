/**
 * 
 */
package ijt.table.gui.action.help;

import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;

/**
 * @author dlegland
 *
 */
public class About implements FramePlugin
{

    @Override
    public void run(BaseFrame frame, String options)
    {
        System.out.println("About...");
    }

}
