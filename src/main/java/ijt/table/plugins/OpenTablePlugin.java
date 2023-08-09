/**
 * 
 */
package ijt.table.plugins;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.io.DelimitedTableReader;
import ijt.table.io.Delimiters;

/**
 * Plugin for opening a data table stored in CSV file.
 * 
 * @see ijt.table.gui.action.file.OpenTable
 */
public class OpenTablePlugin implements PlugIn
{

    @Override
    public void run(String arg)
    {
        // Open a FileChooser to select the table file
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.CANCEL_OPTION)
        {
            return;
        }
        File file = fc.getSelectedFile();

        // Choose some options to open the file
        GenericDialog dlg = new GenericDialog("Open Table Options");
        dlg.addCheckbox("Read Header", true);
        dlg.addChoice("Delimiter", Delimiters.getAllLabels(), Delimiters.SEMICOLON.toString());
        
        dlg.showDialog();
        if (dlg.wasCanceled())
        {
            return;
        }
        
        boolean readHeader = dlg.getNextBoolean();
        String delimiter = Delimiters.fromLabel(dlg.getNextChoice()).getDelimiter();
                
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
        
        table.setName(file.getName());
        
        // add the new frame to the GUI
        TableFrame frame = new TableFrame(table);
        frame.setVisible(true);
    }
}
