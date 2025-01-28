/**
 * 
 */
package ijt.table.gui.chart;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A collection of file filters that can be used to save charts as image.
 * 
 * @author dlegland
 */
public class ChartImageFileFilters
{
    /**
     * File filter for most common image file extensions.
     */
    public static final FileFilter COMMON = new FileNameExtensionFilter("Common image files", "bmp", "gif", "jpg", "jpeg", "png");
    
    /**
     * File filter for BitMap file extension.
     */
    public static final FileFilter BMP = new FileNameExtensionFilter("BMP - Bitmap files", "bmp");

    /**
     * File filter for GIF (Graphics Interchange Format) file extension.
     */
    public static final FileFilter GIF = new FileNameExtensionFilter("GIF - Graphics Interchange Format", "gif");
    
    /**
     * File filter for JPEG file extension.
     */
    public static final FileFilter JPEG = new FileNameExtensionFilter("JPG/JPEG - JPEG files", "jpg", "jpeg");

    /**
     * File filter for PNG file extension.
     */
    public static final FileFilter PNG = new FileNameExtensionFilter("PNG - Portable Network Graphics", "png");

    /**
     * Private constructor to prevent instantiation.
     */
    private ChartImageFileFilters()
    {
    }
}
