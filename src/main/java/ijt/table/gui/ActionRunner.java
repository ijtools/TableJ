/**
 * 
 */
package ijt.table.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ij.IJ;

/**
 * Runs an instance of TableFrame action, by superclassing the Action class to
 * facilitate integration into frame menu.
 * 
 * @author dlegland
 *
 */
public class ActionRunner extends AbstractAction
{
    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The frame from which the acton is run.
     */
    BaseFrame frame;
    
    /**
     * The action to run.
     */
    FramePlugin action;
    
    String optionsString;
    
    
    public ActionRunner(BaseFrame frame, FramePlugin action)
    {
        this(frame, action, "");
    }

    public ActionRunner(BaseFrame frame, FramePlugin action, String optionsString)
    {
        this.frame = frame;
        this.action = action;
        this.optionsString = optionsString;
    }

    @Override
    public void actionPerformed(ActionEvent evt)
    {
        Thread t = new Thread(new ActionRunnable());
        t.start();
    }

    public FramePlugin getAction()
    {
        return action;
    }
    
    private class ActionRunnable implements Runnable
    {
        @Override
        public void run()
        {
            try 
            {
                action.run(frame, optionsString);
            }
            catch (Exception ex)
            {
                ex.printStackTrace(System.err);
                IJ.error("Plugin Error", action.getClass().getSimpleName() + " Plugin Error");
            }
        }
        
    }
}
