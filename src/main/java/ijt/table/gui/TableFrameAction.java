/**
 * 
 */
package ijt.table.gui;

/**
 * The result of the action of the selection of a manu item.
 * 
 * @author dlegland
 */
public interface TableFrameAction
{
    /**
     * Runs this action.
     * 
     * @param frame the frame containing the menu item used to run this action.
     */
	public abstract void run(TableFrame frame);
	
	/**
     * Utility methods that can be used to decide whether the action should be
     * enabled or not in the menu. Default is true, but can be overloaded to
     * decide depending on the content.
     * 
     * @param frame the frame that intend to run this action
     * @return a boolean indicating whether the action can be run or not.
     */
	public default boolean isAvailable(TableFrame frame)
	{
	    return true;
	}
}
