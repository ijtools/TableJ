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
	public abstract void run(DataTableFrame frame);
}
