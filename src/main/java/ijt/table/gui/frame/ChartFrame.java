/**
 * 
 */
package ijt.table.gui.frame;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.internal.chartpart.Chart;

import ijt.table.gui.BaseFrame;


/**
 * A frame that displays a chart, together with few options in a menu.
 */
public class ChartFrame extends BaseFrame
{
    // ===================================================================
    // Static factories

    /**
     * Creates a new frame for displaying a chart, located with respect to the
     * specified frame.
     * 
     * @param chart
     *            the chart to display in the frame
     * @param frameTitle
     *            the title of the frame
     * @param parentFrame
     *            (optional) an existing frame used to locate the new frame. If
     *            null, a new ImagoGui is created.
     * @return a new frame displaying the input chart
     */
    public static final ChartFrame create(Chart<?,?> chart, String frameTitle, BaseFrame parentFrame)
    {
        ChartFrame frame = new ChartFrame(frameTitle, chart);
        if (parentFrame != null)
        {
            Point pos = parentFrame.getJFrame().getLocation();
            frame.jFrame.setLocation(pos.x + FRAME_OFFSET_X, pos.y + FRAME_OFFSET_Y);
        }
        else
        {
            frame.putMiddleScreen();
        }

        frame.setVisible(true);
        return frame;
    }
    

    // ===================================================================
    // Class variables

    /**
     * The chart to display.
     */
    Chart<?,?> chart;
    
    
    // ===================================================================
    // Constructor
    

    /**
     * @param title
     *            the title of the frame
     * @param chart
     *            the chart to display
     */
    public ChartFrame(String title, Chart<?,?> chart)
    {
        super(title);
        this.chart = chart;
        
        createMenuBar();
        jFrame.doLayout();
        
        // setup window listener
        this.jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.jFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent evt)
            {
                ChartFrame.this.close();
            }           
        });
        
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        try
        {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable()
            {
                @Override
                public void run()
                {
                    // add a panel containing the chart 
                    @SuppressWarnings({ "rawtypes", "unchecked" })
                    XChartPanel chartPanel = new XChartPanel(chart);
                    jFrame.add(chartPanel);
                    
                    // Display the window.
                    jFrame.pack();
                    jFrame.setVisible(true);
                }
            });
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        
        this.putMiddleScreen();
    }
    
    private void createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener(evt -> ChartFrame.this.close());
        fileMenu.add(closeItem);
        
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        
        this.jFrame.setJMenuBar(menuBar);
    }
}
