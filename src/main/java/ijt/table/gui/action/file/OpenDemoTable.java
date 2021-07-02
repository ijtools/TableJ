/**
 * 
 */
package ijt.table.gui.action.file;

import java.io.File;
import java.io.IOException;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * @author dlegland
 *
 */
public class OpenDemoTable implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        String fileName = this.getClass().getResource("/tables/fisherIris.txt").getFile();
        System.out.println(fileName);
        File file = new File(fileName);

        Table table;
        try
        {
            table = new ijt.table.io.DelimitedTableReader().readTable(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        
        table.setName("fisherIris");
        
        // add the new frame to the GUI
        frame.createNewTableFrame(table);
    }

}
