/**
 * 
 */
package ijt.table.gui.action.file;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ij.gui.GenericDialog;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.io.DelimitedTableReader;

/**
 * @author dlegland
 *
 */
public class OpenTable implements TableFrameAction
{
    enum Delimiter
    {
        TAB("Tabulation ('\t')", "\t"),
        SEMICOLON("Semi-Colon (';')", ";"),
        COMA("Coma (',')", ","),
        SPACE("Space (' ')", " ");
        
        public String toString()
        {
            return this.label;
        }

        public static String[] getAllLabels()
        {
            int n = Delimiter.values().length;
            String[] result = new String[n];

            int i = 0;
            for (Delimiter weight : Delimiter.values())
                result[i++] = weight.label;

            return result;
        }

        /**
         * Determines the operation type from its label.
         * 
         * @param label the name of a chamfer weight 
         * @return the Delimiter enum corresponding to the given name
         * 
         * @throws IllegalArgumentException
         *             if label name is not recognized.
         */
        public static Delimiter fromLabel(String label)
        {
            if (label != null)
                label = label.toLowerCase();
            for (Delimiter weight : Delimiter.values())
            {
                String cmp = weight.label.toLowerCase();
                if (cmp.equals(label))
                    return weight;
            }
            throw new IllegalArgumentException(
                    "Unable to parse Delimiter with label: " + label);
        }

        Delimiter(String label, String delim)
        {
            this.label = label;
            this.delimiter= delim;
        }
        
        public String  getDelimiter()
        {
            return delimiter;
        }
        
        String label;
        String delimiter;
    }

    @Override
    public void run(TableFrame frame)
    {
        // Open a FileChooser to select the table file
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(frame.getJFrame());

        if (returnVal == JFileChooser.CANCEL_OPTION)
        {
            return;
        }
        File file = fc.getSelectedFile();

        // Choose some options to open the file
        GenericDialog dlg = new GenericDialog("Open Table Options");
        dlg.addCheckbox("Read Header", true);
        dlg.addChoice("Delimiter", Delimiter.getAllLabels(), Delimiter.SEMICOLON.label);
        
        dlg.showDialog();
        if (dlg.wasCanceled())
        {
            return;
        }
        
        boolean readHeader = dlg.getNextBoolean();
        String delimiter = Delimiter.fromLabel(dlg.getNextChoice()).getDelimiter();
                
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
        frame.createNewTableFrame(table);
    }

}
