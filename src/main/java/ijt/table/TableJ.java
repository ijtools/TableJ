/**
 * 
 */
package ijt.table;

import ij.IJ;
import ij.plugin.frame.Recorder;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.PluginManager;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableManager;

/**
 * Static class that should allow to run macros from ImageJ.
 */
public class TableJ
{
    public static final void runPlugin(Class<?> pluginClass, String tableName, String optionsString)
    {
        if (!FramePlugin.class.isAssignableFrom(pluginClass))
        {
            IJ.error("TableJ error", "The specified class must implement the \"PluginClass\" interface");
            return;
        }
        
        // retrieve the plugin from its name
        FramePlugin plugin = PluginManager.getInstance().retrievePlugin(pluginClass);
        if (plugin == null)
        {
            IJ.error("TableJ error", "Could not create plugin from class: " + pluginClass.getName());
            return;
        }
        
        // retrieve table, and it frame, from table name
        Table table = TableManager.getInstance().getTable(tableName);
        TableFrame frame = TableFrame.retrieveTableFrame(table);
        
        // run the plugin
        plugin.run(frame, optionsString);
    }

    public static final void recordCommand(Class<?> pluginClass, Table table, String optionsString)
    {
        // create command string
        String pattern = "ijt.table.TableJ.runPlugin(%s.class, \"%s\", \"%s\")";
        String cmd = String.format(pattern, pluginClass.getName(), table.getName(), optionsString);

        // record the command
        Recorder.record(cmd);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private TableJ()
    {       
    }
}
