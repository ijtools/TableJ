/**
 * 
 */
package ijt.table.plugins;

import java.awt.Frame;
import java.util.ArrayList;

import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.text.TextWindow;
import ijt.table.Table;
import ijt.table.gui.TableFrame;

/**
 * Converts an ImageJ table into a DataTable.
 */
public class ConvertImageJTablePlugin implements PlugIn
{

    @Override
    public void run(String arg)
    {
        // Get the list of windows containing tables
        TextWindow[] textWindows = getTableWindows();
        String[] tableNames = getWindowNames(textWindows);
        
        // opens a dialog to choose one of the tables
        GenericDialog gd = new GenericDialog("Draw Text from Column");
        gd.addChoice("Results Table:", tableNames, tableNames[0]);
        
        // wait for user choice
        gd.showDialog();
        if (gd.wasCanceled())
            return;
        
        // select the result table from its name
        String tableName = gd.getNextChoice();
        Frame tableFrame = WindowManager.getFrame(tableName);
        ResultsTable ijTable = ((TextWindow) tableFrame).getTextPanel().getResultsTable();
        
        // determine size of new table
        int nr = ijTable.getCounter();
         
        // determine whether table has a label for rows
        String[] headings = ijTable.getHeadings();
        int nc = headings.length;
        
        boolean hasRowLabel = ijTable.getLastColumn() == (headings.length - 2);
        if (hasRowLabel)
        {
            nc = nc - 1;
        }
        
        // convert IJ table into data table
        Table table = Table.create(nr, nc);
        for (int c = 0; c < nc; c++)
        {
            // choose the adequate column (use column headings)
            String colName = headings[c];
            if (hasRowLabel)
            {
                colName = headings[c + 1];
            }
            
            // update target column
            table.setColumnName(c, colName);
            double[] values = ijTable.getColumn(colName);
            if (values == null)
            {
                throw new RuntimeException("Could not read values of column " + colName + " in table: " + ijTable.getTitle());
            }
            for (int r = 0; r < nr; r++)
            {
                table.setValue(r, c, values[r]);
            }
        }
        
        // check row names
        if (hasRowLabel)
        {
            String[] rowNames = new String[nr];
            for (int r = 0; r < nr; r++)
            {
                rowNames[r] = ijTable.getLabel(r);
            }
            table.setRowNames(rowNames);
            table.setRowNameLabel(headings[0]);
        }

        // also propagates name
        table.setName(ijTable.getTitle());
        
        // add the new frame to the GUI
        TableFrame frame = new TableFrame(table);
        frame.setVisible(true);
    }
    
    /**
     * Iterates on the list of TextWindows, and keeps only the ones containing a
     * non-null ResultsTable
     * 
     * @return an array containing the list of open TextWindows
     */
    private static final TextWindow[] getTableWindows() 
    {
        Frame[] frames = WindowManager.getNonImageWindows();
        
        ArrayList<TextWindow> windows = new ArrayList<TextWindow>(frames.length);
        
        for (Frame frame : frames) 
        {
            if (frame instanceof TextWindow) 
            {
                TextWindow tw = (TextWindow) frame;
                if (tw.getTextPanel().getResultsTable() != null) 
                {
                    windows.add(tw);
                }
            }
        }
        
        return windows.toArray(new TextWindow[0]);
    }
    
    public static final String[] getWindowNames(TextWindow[] textWindows)
    {
        String[] names = new String[textWindows.length];
        for (int i = 0; i < textWindows.length; i++)
        {
            names[i] = textWindows[i].getTitle();
        }
        return names;
    }
}
