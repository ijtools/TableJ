/**
 * 
 */
package ijt.table.gui;

import ijt.table.gui.frame.ChartFrame;

/**
 * The result of the action of the selection of a manu item.
 * 
 * @author dlegland
 */
public interface ChartFrameAction
{
    /**
     * Runs this action.
     * 
     * @param frame the frame containing the menu item used to run this action.
     */
	public abstract void run(ChartFrame frame);
	
	/**
     * Utility methods that can be used to decide whether the action should be
     * enabled or not in the menu. Default is true, but can be overloaded to
     * decide depending on the content.
     * 
     * @param frame the frame that intend to run this action
     * @return a boolean indicating whether the action can be run or not.
     */
	public default boolean isAvailable(ChartFrame frame)
	{
	    return true;
	}
}
