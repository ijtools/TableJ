/**
 * 
 */
package ijt.table.gui;


/**
 * Encapsulates the behavior to response to menu item from a frame.
 */
public interface FramePlugin
{
    /**
     * Runs this plugin from the specified frame.
     * 
     * 
     * @param frame
     *            the current frame.
     * @param optionsString
     *            an optional String containing the list of options given to the
     *            plugin. Options are provided as name-value pairs, and are
     *            separated with comas.
     */
    public void run(BaseFrame frame, String optionsString);
    
    /**
     * Defines whether this plugin should be enabled for the given frame.
     * Default is true.
     * 
     * @param frame
     *            the parent frame
     * @return the default enable state
     */
    public default boolean isAvailable(BaseFrame frame)
    {
        return true;
    }
}
