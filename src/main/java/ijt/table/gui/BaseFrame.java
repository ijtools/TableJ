/**
 * 
 */
package ijt.table.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

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
    // ===================================================================
    // Private constants

    /**
     * The amount of displacement in the x-direction to locate a new frame with
     * respect to the parent one
     */
    protected static final int FRAME_OFFSET_X = 20;

    /**
     * The amount of displacement in the y-direction to locate a new frame with
     * respect to the parent one
     */
    protected static final int FRAME_OFFSET_Y = 30;
    
    
    // ===================================================================
    // Class variables
    
    /**
     * The Swing widget used to display this frame.
     */
    protected JFrame jFrame;
    
    
    // ===================================================================
    // Constructor
    
    protected BaseFrame(String name)
    {
        this.jFrame = new JFrame(name);
    }
    
    
    // ===================================================================
    // New methods
    
    public JFrame getJFrame()
    {
        return this.jFrame;
    }
    
    protected void putMiddleScreen()
    {
        // set up frame size depending on screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(800, screenSize.width - 100);
        int height = Math.min(700, screenSize.width - 100);
        Dimension frameSize = new Dimension(width, height);
        this.jFrame.setSize(frameSize);

        // set up frame position depending on frame size
        int posX = (screenSize.width - width) / 4;
        int posY = (screenSize.height - height) / 4;
        this.jFrame.setLocation(posX, posY);
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
