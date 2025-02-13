/**
 * 
 */
package ijt.table.gui.action.file;

import java.io.IOException;
import java.io.InputStream;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * Opens a demo data table, containing Fisher's iris data (150 observations, 4
 * numerical features, 1 categorical column).
 * 
 * @author dlegland
 */
public class OpenDemoTable implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        InputStream inputStream = OpenDemoTable.class.getResourceAsStream("/tables/fisherIris.txt");

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
        TableFrame.create(table, frame);
    }

}
