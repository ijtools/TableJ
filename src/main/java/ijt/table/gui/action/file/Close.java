/**
 * 
 */
package ijt.table.gui.action.file;

import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;

/**
 * Closes the current frame.
 */
public class Close implements FramePlugin
{
    @Override
    public void run(BaseFrame frame, String options)
    {
        frame.getJFrame().dispose();
    }
}
