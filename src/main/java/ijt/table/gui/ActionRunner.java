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
    TableFrame frame;
    
    /**
     * The action to run.
     */
    TableFrameAction action;
    
    public ActionRunner(TableFrame frame, TableFrameAction action)
    {
        this.frame = frame;
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent evt)
    {
        Thread t = new Thread(new ActionRunnable());
        t.start();
    }

    public TableFrameAction getAction()
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
                action.run(frame);
            }
            catch (Exception ex)
            {
                ex.printStackTrace(System.err);
                IJ.error("Plugin Error", action.getClass().getSimpleName() + " Plugin Error");
            }
        }
        
    }
}
