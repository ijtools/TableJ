/**
 * 
 */
package ijt.table.gui;

/**
 * An Exception thrown when a Frame plugin could not be instantiated.  
 */
public class FramePluginInstantiationException extends Exception
{
    /**
     * default serial version ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new FramePluginInstantiationException with the specified
     * message.
     * 
     * @param errorMessage
     *            the error message
     */
    public FramePluginInstantiationException(String errorMessage)
    {
        super(errorMessage);
    }

    /**
     * Creates a new FramePluginInstantiationException with the specified
     * message, and the parent error.
     * 
     * @param errorMessage
     *            the error message
     * @param err
     *            the parent error
     */
    public FramePluginInstantiationException(String errorMessage, Throwable err)
    {
        super(errorMessage, err);
    }
}
