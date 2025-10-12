/**
 * 
 */
package ijt.table.gui.action.plot;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.HashMap;
import java.util.Map;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ijt.table.CategoricalColumn;
import ijt.table.Column;
import ijt.table.Table;
import ijt.table.columns.IntegerColumn;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;

/**
 * Generate a color image from 1) a label map and 2) a data table with row names
 * corresponding to labels, and a categorical column corresponding to the class
 * to display.
 */
public class CreateLabelClassMap implements FramePlugin
{
    /**
     * 
     */
    public CreateLabelClassMap()
    {
    }

    @Override
    public void run(BaseFrame frame, String options)
    {
        if (!(frame instanceof TableFrame)) return;
        Table table = ((TableFrame) frame).getTable();
        
        // create the list of image names
        int[] indices = WindowManager.getIDList();
        if (indices == null) {
            IJ.error("No image", "Need at least one image to work");
            return;
        }
        String[] imageNames = new String[indices.length];
        for (int i = 0; i < indices.length; i++) {
            imageNames[i] = WindowManager.getImage(indices[i]).getTitle();
        }
        
        
        String[] colNames = table.getColumnNames();
        
        GenericDialog gd = new GenericDialog("Create Label Class Map");
        gd.addChoice("Label Map Image:", imageNames, imageNames[0]);
        gd.addChoice("Class Column:", colNames, colNames[0]);
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        
        // set up current parameters
        int refImageIndex = gd.getNextChoiceIndex();
        ImagePlus refImage = WindowManager.getImage(refImageIndex + 1);
        int classColumnIndex = gd.getNextChoiceIndex();
        Column classColumn = table.getColumn(classColumnIndex);
        if (!(classColumn instanceof CategoricalColumn))
        {
            if (classColumn instanceof IntegerColumn)
            {
                classColumn = CategoricalColumn.convert(classColumn);
            }
            else
            {
                IJ.error("Requires a Categorical Column");
                return;
            }
        }
        
        // retrieve row names
        String[] rowNames = table.getRowNames();
        if (rowNames == null)
        {
            IJ.error("Requires a table with row names");
        }
        
        // parse labels from row names
        Map<Integer,Integer> labelToRow = new HashMap<>();
        for (int r = 0; r < table.rowCount(); r++)
        {
            int label = Integer.parseInt(rowNames[r]);
            labelToRow.put(label, r);
        }
        
        ImageProcessor img = refImage.getProcessor();
        int sizeX = img.getWidth();
        int sizeY = img.getHeight();
        ImageProcessor res = new ByteProcessor(sizeX, sizeY);
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = (int) img.getf(x, y);
                if (label == 0) continue;
                if (labelToRow.containsKey(label))
                {
                    int row = labelToRow.get(label);
                    int value = ((CategoricalColumn) classColumn).getLevelIndex(row);
                    res.set(x, y, value + 1);
                }
            }
        }
        
        ImagePlus resPlus = new ImagePlus(refImage.getTitle(), res);
        resPlus.copyScale(refImage);
        resPlus.setDisplayRange(0, ((CategoricalColumn) classColumn).levelCount());
        
        CategoricalColumn col2 = ((CategoricalColumn) classColumn);
        Color[] groupColors = col2.levelColors();
        
        // create default label Color Model
        ColorModel cm = createColorModel(groupColors, Color.WHITE);
        resPlus.getProcessor().setColorModel(cm);

        resPlus.show();
    }
    

    /**
     * Creates a Java.awt.image.ColorModel from a color map given as an triplet
     * of byte arrays, and a color for the background that will be associated 
     * to label 0.
     * 
     * @param cmap a colormap given as an array of colors
     * @param bgColor the color associated to the background
     * @return the corresponding color model
     */
    private final static ColorModel createColorModel(Color[] colors, Color bgColor) {
        byte[] r = new byte[256];
        byte[] g = new byte[256];
        byte[] b = new byte[256];
        
        r[0] = (byte) bgColor.getRed();
        g[0] = (byte) bgColor.getGreen();
        b[0] = (byte) bgColor.getBlue();

        for (int i = 0; i < colors.length; i++) 
        {
            Color color = colors[i];
            r[i+1] = (byte) color.getRed();
            g[i+1] = (byte) color.getGreen();
            b[i+1] = (byte) color.getBlue();
        }
        return new IndexColorModel(8, 256, r, g, b);
    }

}
