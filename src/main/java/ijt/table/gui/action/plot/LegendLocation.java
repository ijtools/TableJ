/**
 * 
 */
package ijt.table.gui.action.plot;

import java.util.stream.Stream;

import org.knowm.xchart.style.Styler.LegendPosition;

/**
 * An enumeration of possible locations for legend of plots. Provides a method
 * to retrieve the corresponding XChart {@code LegendPosition} value.
 */
public enum LegendLocation
{
    NORTH_EAST("North-East", LegendPosition.InsideNE),
    NORTH_WEST("North-West", LegendPosition.InsideNW),
    SOUTH_EAST("South-East", LegendPosition.InsideSE),
    SOUTH_WEST("South-West", LegendPosition.InsideSW);
    
    String label;
    LegendPosition position;
    
    LegendLocation(String label, LegendPosition position)
    {
        this.label = label;
        this.position = position;
    }
    
    /**
     * Returns the corresponding XChart {@code LegendPosition} value.
     * 
     * @return the corresponding XChart {@code LegendPosition} value.
     */
    LegendPosition position()
    {
        return position;    
    }
    
    /**
     * Converts this marker type into a string.
     * 
     * @return a String representation of this marker type
     */
    public String toString() 
    {
        return this.label;
    }
    
    /**
     * Returns the list of labels for this enumeration.
     * 
     * @return the list of labels for this enumeration.
     */
    public static String[] getAllLabels()
    {
        return Stream.of(LegendLocation.values())
                .map(loc -> loc.label)
                .toArray(String[]::new);
    }
    
    /**
     * Determines the LegendLocation enumeration item from its label.
     * 
     * @param label
     *            the label of the LegendLocation
     * @return the parsed LegendLocation
     * @throws IllegalArgumentException
     *             if label is not recognized.
     */
    public static LegendLocation fromLabel(String label)
    {
        for (LegendLocation item : LegendLocation.values()) 
        {
            if (item.label.equalsIgnoreCase(label)) return item;
        }
        throw new IllegalArgumentException("Unable to parse LegendLocation with label: " + label);
    }
}
