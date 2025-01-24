/**
 * 
 */
package ijt.table.plugins;

import java.io.IOException;
import java.io.InputStream;

import ij.plugin.PlugIn;
import ijt.table.Table;
import ijt.table.gui.TableFrame;

/**
 * Plugin for opening Fisher Iris demonstration table.
 * 
 * @see ijt.table.gui.action.file.OpenDemoTable
 */
public class OpenDemoTablePlugin implements PlugIn
{
    @Override
    public void run(String arg)
    {
        InputStream inputStream = OpenDemoTablePlugin.class.getResourceAsStream("/tables/fisherIris.txt");

        Table table;
        try
        {
            table = new ijt.table.io.DelimitedTableReader().readTable(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        
        table.setName("fisherIris");
        
        // add the new frame to the GUI
        TableFrame frame = new TableFrame(table);
        frame.setVisible(true);
    }
}
