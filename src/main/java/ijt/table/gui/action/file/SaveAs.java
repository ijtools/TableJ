/**
 * 
 */
package ijt.table.gui.action.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.swing.JFileChooser;

import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;

/**
 * @author dlegland
 *
 */
public class SaveAs implements FramePlugin
{
    private JFileChooser saveWindow = null;
    
    @Override
    public void run(BaseFrame frame, String options)
    {
        if (!(frame instanceof TableFrame)) return;
        Table table = ((TableFrame) frame).getTable();
        if (table == null)
        {
            return;
        }
        
        // create file dialog if it doesn't exist
        if (saveWindow == null)
        {
            saveWindow = new JFileChooser(".");
            // openWindow.setFileFilter(fileFilter);
        }

        // Open dialog to choose the file
        int ret = saveWindow.showSaveDialog(frame.getJFrame());
        if (ret != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        // Check the chosen file
        File fileName = saveWindow.getSelectedFile();

        // Open a stream to write formatted text data
        PrintWriter writer;
        try
        {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Could not open file: " + fileName, ex);
        }

        // TODO: create I/O package
        
        int nc = table.columnCount();
        int nr = table.rowCount();
        
        
        // Write header name of each column
        String[] colNames = table.getColumnNames();
        writer.print("name");
        for (int c = 0; c < nc; c++)
        {
            writer.print("\t" + colNames[c]);
        }
        writer.println();

        // Write the content of each row
        String[] rowNames = table.getRowNames();
        for (int r = 0; r < nr; r++)
        {

            if (rowNames != null)
                writer.print(rowNames[r]);
            else
                writer.print(r);

            for (int c = 0; c < nc; c++)
            {
                double val = table.getValue(r, c);
                String str = String.format(Locale.US, "%7.4f", val);
                writer.print("\t" + str);
            }

            writer.println();
        }

        // Closes the file
        writer.close();
    }

    public boolean isAvailable(BaseFrame frame)
    {
        if (!(frame instanceof TableFrame)) return false;
        return ((TableFrame) frame).getTable() != null;
    }
}
