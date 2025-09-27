/**
 * 
 */
package ijt.table.gui;

/**
 * The result of the action of the selection of a menu item.
 * 
 * @author dlegland
 */
public interface TableFrameAction extends FramePlugin
{
    /**
     * Runs this action.
     * 
     * @param frame the frame containing the menu item used to run this action.
     */
	public abstract void run(TableFrame frame);
	
    /**
     * Runs this action, by ignoring the options String.
     * 
     * @param frame the frame containing the menu item used to run this action.
     */
	@Override
    public default void run(BaseFrame frame, String optionsString)
    {
        run((TableFrame) frame);
    }
    
	/**
     * Utility methods that can be used to decide whether the action should be
     * enabled or not in the menu. Default is true, but can be overloaded to
     * decide depending on the content.
     * 
     * @param frame the frame that intend to run this action
     * @return a boolean indicating whether the action can be run or not.
     */
	@Override
	public default boolean isAvailable(BaseFrame frame)
	{
	    return true;
	}
}
