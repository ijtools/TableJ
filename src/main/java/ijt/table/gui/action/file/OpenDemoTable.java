/**
 * 
 */
package ijt.table.gui.action.file;

import java.io.File;
import java.io.IOException;

import ijt.table.DataTable;
import ijt.table.gui.DataTableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * @author dlegland
 *
 */
public class OpenDemoTable implements TableFrameAction
{

    @Override
    public void run(DataTableFrame frame)
    {
        String fileName = this.getClass().getResource("/tables/fisherIris.txt").getFile();
        System.out.println(fileName);
        File file = new File(fileName);

        DataTable table;
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
        frame.createNewTableFrame(table).setVisible(true);
    }

}
