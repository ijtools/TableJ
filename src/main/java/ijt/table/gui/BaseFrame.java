/**
 * 
 */
package ijt.table.gui;

import javax.swing.JFrame;

/**
 * An abstract class that contains the data associated to a table frame, as well
 * as a reference to the Swing JFrame used to represent the frame.
 * 
 * @author dlegland
 *
 */
public class BaseFrame
{
    /**
     * The Swing widget used to display this frame.
     */
    protected JFrame jFrame;
    
    protected BaseFrame(String name)
    {
        this.jFrame = new JFrame(name);
    }
    
    
    // ===================================================================
    // Overload some methods from the inner JFrame

    public void repaint()
    {
        this.jFrame.repaint();
    }
    
    public void setVisible(boolean b)
    {
        this.jFrame.setVisible(b);
    }

    public void setTitle(String title)
    {
        this.jFrame.setTitle(title);
    }
    
    public void close()
    {
        // remove widget
        this.jFrame.dispose();
    }
}
