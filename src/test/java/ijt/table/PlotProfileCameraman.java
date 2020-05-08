/**
 * 
 */
package ijt.table;

import javax.swing.JFrame;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ijt.table.gui.DataTableFrame;

/**
 * @author David Legland
 *
 */
public class PlotProfileCameraman
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String fileName = PlotProfileCameraman.class.getResource("/images/cameraman.tif").getFile();
        ImagePlus imagePlus = IJ.openImage(fileName);

        ImageProcessor image = imagePlus.getProcessor();

        DataTable table = new DataTable(512, 1);
        table.setColumnNames(new String[] { "Intensity" });
        for (int i = 0; i < 512; i++)
        {
            table.setValue(i, 0, image.get(i, 256));
        }

        JFrame frame = new DataTableFrame(table);
        frame.setVisible(true);
    }
}
