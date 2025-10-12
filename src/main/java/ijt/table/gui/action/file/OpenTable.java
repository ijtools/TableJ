/**
 * 
 */
package ijt.table.gui.action.file;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;

import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;
import ijt.table.io.DelimitedTableReader;
import ijt.table.io.Delimiters;

/**
 * Opens a dialog to choose a file, and another one to choose import options of
 * delimiter-separated values file.
 */
public class OpenTable implements FramePlugin
{
    private static String lastOpenDir = OpenDialog.getDefaultDirectory();

    @Override
    public void run(BaseFrame frame, String options)
    {
        // Open a FileDialog to select the table file
        FileDialog dlg = new FileDialog(frame.getJFrame(), "Choose a file", FileDialog.LOAD);
        dlg.setDirectory(lastOpenDir);
        dlg.setVisible(true);

        String fileName = dlg.getFile();
        if (fileName == null) return;
        String dir = dlg.getDirectory();
        
        // convert to file, and store last open directory
        File file = new File(dir, fileName);
        lastOpenDir = file.toPath().getParent().toString();

        // Choose some options to open the file
        GenericDialog gd = new GenericDialog("Open Table Options");
        gd.addCheckbox("Read Header", true);
        gd.addChoice("Delimiter", Delimiters.getAllLabels(), Delimiters.SEMICOLON.toString());
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        
        boolean readHeader = gd.getNextBoolean();
        String delimiter = Delimiters.fromLabel(gd.getNextChoice()).getDelimiter();
                
        DelimitedTableReader reader = new DelimitedTableReader();
        reader.setReadHeader(readHeader);
        reader.setDelimiters(delimiter);
        
        Table table;
        try
        {
            table = reader.readTable(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        
        table.setName(fileName);

        // add the new frame to the GUI
        TableFrame.create(table, frame);
    }

}
