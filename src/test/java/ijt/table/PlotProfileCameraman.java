/**
 * 
 */
package ijt.table;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ijt.table.gui.TableFrame;

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

        Table table = Table.create(512, 1);
        table.setName("Cameraman-profile");
        table.setColumnNames(new String[] { "Intensity" });
        for (int i = 0; i < 512; i++)
        {
            table.setValue(i, 0, image.get(i, 256));
        }

        TableFrame frame = new TableFrame(table);
        frame.setVisible(true);
    }
}
