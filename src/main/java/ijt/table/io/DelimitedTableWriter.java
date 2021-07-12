/**
 * 
 */
package ijt.table.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import ijt.table.Table;

/**
 * @author dlegland
 *
 */
public class DelimitedTableWriter implements TableWriter
{
    // =============================================================
    // Class members
    
    /**
     * The file to write table in.
     */
    File file;
    
    /**
     * The delimiter to print between tokens in the file. Can be "\t", " ",
     * ";"... Default is "\t" (tab character).
     */
    String delimiter = "\t";
	
    /**
     * The pattern used to print floating point values. Default is "%7.3f".
     */
	String floatPattern = "%7.3f";
	
	
    // =============================================================
    // Constructors

	/**
	 * Creates a new instance with default delimiter set as tabulation.
	 */
	public DelimitedTableWriter(File file)
	{
	    this.file = file;
	}

	/**
     * Creates a new instance specifying the delimiter.
     * 
     * @param delim
     *            the delimiters
     */
	public DelimitedTableWriter(File file, String delim)
	{
	    this.file = file;
		this.delimiter = delim;
	}


	// =============================================================
    // Getters and setters

	/**
     * @return the delimiter
     */
    public String getDelimiter()
    {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }

    /**
     * @return the floatPattern
     */
    public String getFloatPattern()
    {
        return floatPattern;
    }

    /**
     * @param floatPattern the floatPattern to set
     */
    public void setFloatPattern(String floatPattern)
    {
        this.floatPattern = floatPattern;
    }

    
    // =============================================================
    // Implementation of TableWriter interface

    /* (non-Javadoc)
	 * @see net.sci.table.io.TableWriter#writeTable(net.sci.table.Table)
	 */
	@Override
	public void writeTable(Table table) throws IOException
	{
	    // open the file for writing
		PrintWriter writer;
		writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		
		// retrieve table size
		int nc = table.columnCount();
		int nr = table.rowCount();

		// retrieve column and row names
		String[] colNames = table.getColumnNames();
        String[] rowNames = table.getRowNames();
        
        // print header if appropriate
		if (colNames != null)
		{
		    if (rowNames != null)
		    {
	            writer.print("name" + this.delimiter);
		    }
			writer.print(colNames[0]);
			for (int c = 1; c < nc; c++)
			{
				writer.print(this.delimiter + colNames[c]);
			}
			writer.println("");
		}
		
		// print the content of each regular row
		for (int r = 0; r < nr; r++)
		{
			if (rowNames != null)
			{
				writer.print(rowNames[r] + delimiter);
			}
			
			writer.print(createToken(table, r, 0));
			for (int c = 1; c < nc; c++)
			{
				writer.print(delimiter + createToken(table, r, c));
			}

			writer.println("");
		}
		
		// close file
		writer.close();
	}
	
	/**
     * Generates the string representation of the selected data to write into
     * the file. If the content of the cell is numeric, it is printed as using
     * the floatPattern. If the content is categorical, the name of the level is
     * returned.
     * 
     * @param table
     *            the table to print data from
     * @param iRow
     *            the row index of the data to print
     * @param iCol
     *            the column index of the data to print
     * @return a string representation of the data located at (iRow, iCol) in
     *         the table.
     */
	private String createToken(Table table, int iRow, int iCol)
	{
	    Object obj = table.get(iRow, iCol);
	    if (obj instanceof String)
	    {
	        return (String) obj;
	    }
	    
	    return String.format(Locale.ENGLISH, floatPattern, table.getValue(iRow, iCol));
	}

}
