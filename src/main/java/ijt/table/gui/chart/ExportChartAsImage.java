/**
 * 
 */
package ijt.table.gui.chart;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.internal.chartpart.Chart;

import ij.IJ;
import ijt.table.gui.ChartFrameAction;
import ijt.table.gui.frame.ChartFrame;

/**
 * 
 */
public class ExportChartAsImage implements ChartFrameAction
{
    @Override
    public void run(ChartFrame frame)
    {
        // get table references by the frame
        Chart<?,?> chart = ((ChartFrame) frame).getChart();
        
        // opens a dialog to choose the file
        String defaultName = frame.getJFrame().getTitle() + ".png";
        File file = chooseFileToSave(frame, "Save Chart", defaultName,
                ChartImageFileFilters.COMMON, ChartImageFileFilters.PNG, ChartImageFileFilters.BMP,
                ChartImageFileFilters.JPEG, ChartImageFileFilters.GIF);
        if (file == null)
        {
            return;
        }
        
        try
        {
            if (file.getName().endsWith(".png"))
            {
                BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapFormat.PNG);
            }
            else if (file.getName().endsWith(".BMP"))
            {
                BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapFormat.BMP);
            }
            else if (file.getName().endsWith(".JPG"))
            {
                BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapFormat.JPG);
            }
            else if (file.getName().endsWith(".GIF"))
            {
                BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapFormat.GIF);
            }
            else
            {
                throw new RuntimeException("Unknown extension: " + file.getName());
            }
        } 
        catch (IOException ex)
        {
            ex.printStackTrace(System.err);
            IJ.showMessage("Chart I/O Error", "Could not write the chart.");
            return;
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err);
            IJ.showMessage("Chart I/O Error", "Could not write the chart.");
            return;
        }
    }

    /**
     * Creates a new JFileChooser instance to open a file. The dialog
     * automatically opens within the last directory used for saving.
     * 
     * @param frame
     *            the reference frame for positioning the file chooser dialog
     *            (may be null)
     * @param title
     *            the title of the dialog
     * @param fileFilters
     *            an optional list of file filters. The first one of the file
     *            filters is selected as current filter.
     * @return the selected file, or null if the dialog was canceled
     */
    public File chooseFileToSave(ChartFrame frame, String title, String defaultName, FileFilter... fileFilters)
    {
//        if (app.userPreferences.useSaveFileSystemDialog)
//        {
//            return chooseFileToSave_awt(frame, title, defaultName, fileFilters);
//        }
        
        // create dialog using last open path
        JFileChooser dlg = new JFileChooser();
//        JFileChooser dlg = new JFileChooser(this.app.userPreferences.lastSavePath);

        // setup dialog title
        if (title != null)
        {
            dlg.setDialogTitle(title);
        }

        // add optional file filters
        for (FileFilter filter : fileFilters)
        {
            dlg.addChoosableFileFilter(filter);
        }
        if (fileFilters.length > 0)
        {
            dlg.setFileFilter(fileFilters[0]);
        }

        // if a name is selected, use it as default file
        if (defaultName != null)
        {
            dlg.setSelectedFile(new File(defaultName));
//            dlg.setSelectedFile(new File(this.app.userPreferences.lastSavePath, defaultName));
        }

//        // add an action listener to keep path for future opening
//        dlg.addActionListener(evt -> {
//            if (evt.getActionCommand() == JFileChooser.APPROVE_SELECTION)
//            {
//                // update path for future opening
//                File file = dlg.getSelectedFile();
//                String path = file.getParent();
////                this.app.userPreferences.lastSavePath = path;
//            }
//        });

        // Open dialog to choose the file
        int ret = dlg.showSaveDialog(frame == null ? null : frame.getJFrame());
        if (ret != JFileChooser.APPROVE_OPTION)
        { return null; }

        // return the selected file
        return dlg.getSelectedFile();
    }
}
