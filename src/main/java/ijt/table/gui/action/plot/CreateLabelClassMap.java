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
            IJ.error("Requires a Categorical Column");
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
                    int value = ((CategoricalColumn) classColumn).getLevelIndex(row) + 1;
                    res.set(x, y, value);
                }
            }
        }
        
        ImagePlus resPlus = new ImagePlus(refImage.getTitle(), res);
        resPlus.copyScale(refImage);
        resPlus.setDisplayRange(0, ((CategoricalColumn) classColumn).levelCount() + 1);
        
        // create default label Color Model
        byte[][] colorMap = interpolateLut(createGlasbeyDarkLut(), 255);
        ColorModel cm = createColorModel(colorMap, Color.BLACK);
        resPlus.getProcessor().setColorModel(cm);

        resPlus.show();
    }
    
    /**
     * Create lookup table with a maximally distinct sets of colors, retaining
     * only colors with low luminance, making it useful for displaying
     * categorical data or label maps over a white background.
     * 
     * References:
     * <ul>
     * <li><a href=
     * "https://colorcet.holoviz.org/user_guide/Categorical.html">Colorcet
     * Categorical colormaps</a></li>
     * <li>Kovesi, Peter. Good Colour Maps: How to Design Them. <a href=
     * "https://arxiv.org/abs/1509.03700">https://arxiv.org/abs/1509.03700</a></li>
     * <li>Glasbey, Chris, Gerie van der Heijden, Vivian FK Toh, and Alision
     * Gray. "Colour displays for categorical images." Color Research &amp;
     * Application 32, no. 4 (2007): 304-309.</li>
     * </ul>
     * 
     * @return Dark Glasbey lookup table with 256 entries
     */
    private static final byte[][] createGlasbeyDarkLut()
    {
        int[][] data = {
                { 215, 0, 0 }, { 140, 60, 255 }, { 2, 136, 0 }, { 0, 172, 199 }, { 231, 165, 0 }, { 255, 127, 209 },
                { 108, 0, 79 }, { 88, 59, 0 }, { 0, 87, 89 }, { 21, 225, 141 }, { 0, 0, 221 }, { 162, 118, 106 },
                { 188, 183, 255 }, { 192, 4, 185 }, { 100, 84, 115 }, { 121, 0, 0 }, { 7, 116, 216 }, { 115, 155, 125 },
                { 255, 120, 82 }, { 0, 75, 0 }, { 143, 123, 1 }, { 243, 0, 123 }, { 143, 186, 0 }, { 166, 123, 184 },
                { 90, 2, 163 }, { 227, 175, 175 }, { 160, 58, 82 }, { 162, 200, 200 }, { 158, 75, 0 }, { 84, 103, 69 },
                { 187, 195, 137 }, { 95, 123, 136 }, { 96, 56, 60 }, { 131, 136, 255 }, { 57, 0, 0 }, { 227, 83, 255 },
                { 48, 83, 130 }, { 127, 202, 255 }, { 197, 102, 143 }, { 0, 129, 106 }, { 146, 158, 183 },
                { 204, 116, 7 }, { 127, 43, 142 }, { 0, 190, 164 }, { 45, 177, 82 }, { 78, 51, 255 }, { 0, 229, 0 },
                { 255, 0, 206 }, { 200, 88, 72 }, { 229, 156, 255 }, { 29, 161, 255 }, { 110, 112, 171 },
                { 200, 154, 105 }, { 120, 87, 59 }, { 4, 218, 230 }, { 193, 163, 196 }, { 255, 106, 138 },
                { 187, 0, 254 }, { 146, 83, 128 }, { 159, 2, 116 }, { 148, 161, 80 }, { 55, 68, 37 }, { 175, 109, 255 },
                { 89, 109, 0 }, { 255, 49, 71 }, { 131, 128, 87 }, { 0, 109, 46 }, { 137, 86, 175 }, { 90, 74, 163 },
                { 119, 53, 22 }, { 134, 195, 154 }, { 95, 17, 35 }, { 213, 133, 129 }, { 164, 41, 24 }, { 0, 136, 177 },
                { 203, 0, 68 }, { 255, 160, 86 }, { 235, 78, 0 }, { 108, 151, 0 }, { 83, 134, 73 }, { 117, 90, 0 },
                { 200, 196, 64 }, { 146, 211, 112 }, { 75, 152, 148 }, { 77, 35, 13 }, { 97, 52, 92 }, { 132, 0, 207 },
                { 139, 0, 49 }, { 159, 110, 50 }, { 172, 132, 153 }, { 198, 49, 137 }, { 2, 84, 56 }, { 8, 107, 132 },
                { 135, 168, 236 }, { 100, 102, 239 }, { 196, 93, 186 }, { 1, 159, 112 }, { 129, 81, 89 },
                { 131, 111, 140 }, { 179, 192, 218 }, { 185, 145, 41 }, { 255, 151, 178 }, { 167, 147, 225 },
                { 105, 141, 190 }, { 76, 80, 1 }, { 72, 2, 204 }, { 97, 0, 110 }, { 69, 106, 102 }, { 157, 87, 67 },
                { 123, 172, 181 }, { 205, 132, 189 }, { 0, 84, 193 }, { 123, 47, 79 }, { 251, 124, 0 }, { 52, 192, 0 },
                { 255, 156, 136 }, { 225, 183, 105 }, { 83, 97, 119 }, { 92, 58, 124 }, { 237, 165, 218 },
                { 240, 83, 163 }, { 93, 126, 105 }, { 196, 119, 80 }, { 209, 72, 104 }, { 110, 0, 235 }, { 31, 52, 0 },
                { 193, 65, 4 }, { 109, 213, 194 }, { 70, 112, 159 }, { 162, 1, 196 }, { 10, 130, 137 }, { 175, 166, 1 },
                { 166, 92, 107 }, { 254, 119, 255 }, { 139, 133, 174 }, { 199, 127, 233 }, { 154, 171, 133 },
                { 135, 108, 217 }, { 1, 186, 247 }, { 175, 94, 210 }, { 89, 81, 43 }, { 182, 0, 95 }, { 124, 182, 106 },
                { 73, 133, 255 }, { 0, 194, 130 }, { 210, 149, 171 }, { 163, 75, 168 }, { 227, 6, 227 }, { 22, 163, 0 },
                { 57, 46, 0 }, { 132, 48, 51 }, { 94, 149, 170 }, { 90, 16, 0 }, { 123, 70, 0 }, { 111, 111, 49 },
                { 51, 88, 38 }, { 77, 96, 182 }, { 162, 149, 100 }, { 98, 64, 40 }, { 69, 212, 88 }, { 112, 170, 208 },
                { 46, 107, 78 }, { 115, 175, 158 }, { 253, 21, 0 }, { 216, 180, 146 }, { 122, 137, 59 },
                { 125, 198, 217 }, { 220, 145, 55 }, { 236, 97, 94 }, { 236, 95, 212 }, { 229, 123, 167 },
                { 166, 108, 152 }, { 0, 151, 68 }, { 186, 95, 34 }, { 188, 173, 83 }, { 136, 216, 48 },
                { 135, 53, 115 }, { 174, 168, 210 }, { 227, 140, 99 }, { 209, 177, 236 }, { 55, 66, 159 },
                { 58, 190, 194 }, { 102, 157, 77 }, { 158, 3, 153 }, { 78, 78, 122 }, { 123, 76, 134 }, { 195, 53, 49 },
                { 141, 102, 119 }, { 170, 0, 45 }, { 127, 1, 117 }, { 1, 130, 77 }, { 115, 74, 103 }, { 114, 119, 145 },
                { 110, 0, 153 }, { 160, 186, 82 }, { 225, 110, 49 }, { 197, 106, 113 }, { 109, 91, 150 },
                { 163, 60, 116 }, { 50, 98, 0 }, { 136, 0, 80 }, { 51, 88, 105 }, { 186, 141, 124 }, { 25, 89, 255 },
                { 145, 146, 2 }, { 44, 139, 213 }, { 23, 38, 255 }, { 33, 211, 255 }, { 164, 144, 175 },
                { 139, 109, 79 }, { 94, 33, 62 }, { 220, 3, 179 }, { 111, 87, 202 }, { 101, 40, 33 }, { 173, 119, 0 },
                { 163, 191, 247 }, { 181, 132, 70 }, { 151, 56, 220 }, { 178, 81, 148 }, { 114, 66, 163 },
                { 135, 143, 209 }, { 138, 112, 177 }, { 107, 175, 54 }, { 90, 122, 201 }, { 199, 159, 255 },
                { 86, 132, 26 }, { 0, 214, 167 }, { 130, 71, 57 }, { 17, 67, 29 }, { 90, 171, 117 }, { 145, 91, 1 },
                { 246, 69, 112 }, { 255, 151, 3 }, { 225, 66, 49 }, { 186, 146, 207 }, { 52, 88, 77 },
                { 248, 128, 125 }, { 145, 52, 0 }, { 179, 205, 0 }, { 46, 159, 211 }, { 121, 139, 159 },
                { 81, 129, 125 }, { 193, 54, 215 }, { 236, 5, 83 }, { 185, 172, 126 }, { 72, 112, 50 },
                { 132, 149, 101 }, { 217, 157, 137 }, { 0, 100, 163 }, { 76, 144, 120 }, { 143, 97, 152 },
                { 255, 83, 56 }, { 167, 66, 59 }, { 0, 110, 112 }, { 152, 132, 62 }, { 220, 176, 200 } };
        
        // create map
        byte[][] map = new byte[data.length][3];

        // cast elements
        for (int i = 0; i < data.length; i++) {
            map[i][0] = (byte) data[i][0];
            map[i][1] = (byte) data[i][1];
            map[i][2] = (byte) data[i][2];
        }
        
        return map;
    }

    /**
     * Interpolates between LUT values such that the result array has the
     * specified number of colors.
     * 
     * @param baseLut
     *            the LUT to interpolated
     * @param nColors
     *            the number of colors of the new LUT
     * @return a nColors-by-3 array of color components
     */
    private final static byte[][] interpolateLut(byte[][] baseLut, int nColors) {
        
        int n0 = baseLut.length;
        // allocate memory for new lut
        byte[][] lut = new byte[nColors][3];
        
        // linear interpolation of each color of new lut
        for (int i = 0; i < nColors; i++) {
            // compute color index in original lut
            float i0 = ((float) i) * n0 / nColors;
            int i1 = (int) Math.floor(i0);
            
            // the two surrounding colors
            byte[] col1 = baseLut[i1];
            byte[] col2 = baseLut[Math.min(i1 + 1, n0 - 1)];

            // the ratio between the two surrounding colors
            float f = i0 - i1;
            
            // linear interpolation of surrounding colors with cast
            lut[i][0] = (byte) ((1. - f) * (col1[0] & 0xFF) + f * (col2[0] & 0xFF));
            lut[i][1] = (byte) ((1. - f) * (col1[1] & 0xFF) + f * (col2[1] & 0xFF));
            lut[i][2] = (byte) ((1. - f) * (col1[2] & 0xFF) + f * (col2[2] & 0xFF));
        }
        
        return lut;
    }


    /**
     * Creates a Java.awt.image.ColorModel from a color map given as an triplet
     * of byte arrays, and a color for the background that will be associated 
     * to label 0.
     * 
     * @param cmap a colormap given as a triplet of byte arrays
     * @param bg the color associated to the background
     * @return the corresponding color model
     */
    private final static ColorModel createColorModel(byte[][] cmap, Color bg) {
        int n = cmap.length;
        byte[] r = new byte[n+1];
        byte[] g = new byte[n+1];
        byte[] b = new byte[n+1];
        
        r[0] = (byte) bg.getRed();
        g[0] = (byte) bg.getGreen();
        b[0] = (byte) bg.getBlue();

        for (int i = 0; i < n; i++) {
            r[i+1] = cmap[i][0];
            g[i+1] = cmap[i][1];
            b[i+1] = cmap[i][2];
        }
        return new IndexColorModel(8, n+1, r, g, b);
    }
    

//    private static final String[] computeDisplayLabelsOptionStrings(Table table)
//    {
//        String[] colNames = table.getColumnNames();
//        if (!table.hasRowNames())
//        {
//            return colNames;
//        }
//
//        String[] colNames2 = new String[colNames.length + 1];
//        colNames2[0] = table.getRowNameLabel();
//        for (int i = 0; i < colNames.length; i++)
//        {
//            colNames2[i+1] = colNames[i];
//        }
//        return colNames2;
//    }
    

}
