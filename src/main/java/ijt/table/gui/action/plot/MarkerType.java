package ijt.table.gui.action.plot;

import java.util.stream.Stream;

import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

public enum MarkerType
{
	CIRCLE("Circle", SeriesMarkers.CIRCLE),
	CROSS("Cross", SeriesMarkers.CROSS),
	DIAMOND("Diamond", SeriesMarkers.DIAMOND),
	PLUS("Plus", SeriesMarkers.PLUS),
	SQUARE("Square", SeriesMarkers.SQUARE),
	TRIANGLE_UP("Triangle_Up", SeriesMarkers.TRIANGLE_UP),
	TRIANGLE_DOWN("Triangle_Down", SeriesMarkers.TRIANGLE_DOWN);

	private String label;
	private Marker marker;
	
	private MarkerType(String label, Marker marker)
	{
		this.label = label;
		this.marker = marker;
	}
	
	public void setupSeries(XYSeries series)
	{
		series.setMarker(marker);
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
		return Stream.of(MarkerType.values())
				.map(mt -> mt.label)
				.toArray(String[]::new);
	}
	
	/**
	 * Determines the MarkerType enumeration item from its label.
	 * 
	 * @param label
	 *            the label of the MarkerType
	 * @return the parsed MarkerType
	 * @throws IllegalArgumentException
	 *             if label is not recognized.
	 */
	public static MarkerType fromLabel(String label)
	{
		for (MarkerType item : MarkerType.values()) 
		{
			if (item.label.equalsIgnoreCase(label)) return item;
		}
		throw new IllegalArgumentException("Unable to parse MarkerType with label: " + label);
	}
}