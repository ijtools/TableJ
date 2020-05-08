/**
 * 
 */
package ijt.table.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import ijt.table.DataTable;

/**
 * @author dlegland
 *
 */
public class DelimitedTableReader implements TableReader
{
	// =============================================================
	// Class variables

	String delimiters = " \t";
	
	boolean readHeader = true;
	
	int skipLines = 0;
	
	boolean readRowNames = true;
	
	// =============================================================
	// Constructors

	/**
	 * Creates a new instance of DelimitedTableReader.
	 */
	public DelimitedTableReader()
	{
	}

	/**
     * Creates a new instance of DelimitedTableReader, specifying the
     * delimiters.
     * 
     * @param delimiters
     *            the delimiters
     */
	public DelimitedTableReader(String delimiters)
	{
		this.delimiters = delimiters;
	}

	
	// =============================================================
	// Accessors and mutators

	public String getDelimiters()
	{
		return delimiters;
	}

	public void setDelimiters(String delimiters)
	{
		this.delimiters = delimiters;
	}

	public boolean isReadHeader()
	{
		return readHeader;
	}

	public void setReadHeader(boolean readHeader)
	{
		this.readHeader = readHeader;
	}

	public int getSkipLines()
	{
		return skipLines;
	}

	public void setSkipLines(int skipLines)
	{
		this.skipLines = skipLines;
	}

	public boolean isReadRowNames()
	{
		return readRowNames;
	}

	public void setReadRowNames(boolean readRowNames)
	{
		this.readRowNames = readRowNames;
	}


	// =============================================================
	// implementation of the TableReader interface

	/* (non-Javadoc)
	 * @see net.sci.table.io.TableReader#readTable()
	 */
	@Override
	public DataTable readTable(File file) throws IOException
	{
        // meta data for table
        int nCols;
        ArrayList<ArrayList<String>> columns;
        String[] colNames;
        ArrayList<String> rowNames = new ArrayList<String>();

        // Create text reader from file 
		LineNumberReader reader;
		try 
		{
			reader = new LineNumberReader(new FileReader(file));
		} 
		catch(IOException ex)
		{
			throw new RuntimeException("Could not open file: " + file, ex);
		}
		
		// number of rows read
		int nRows = 0;

		// eventually skip some lines
		for (int r = 0; r < skipLines; r++)
		{
			reader.readLine(); 
		}
		
		String delimiterRegexp = "[" + delimiters + "]+";

		// parse header line
		String firstLine = reader.readLine();
		String[] tokens = firstLine.split(delimiterRegexp);

		// parse first line to identify number of columns 
		if (readHeader)
		{
			// first line is the header
			nCols = tokens.length - 1;
			colNames = new String[nCols];
			for (int i = 0; i < nCols; i++)
			{
				colNames[i] = tokens[i + 1];
			}
		}
		else
		{
			// first line is a data line
			nRows++;
			nCols = tokens.length;
			colNames = new String[nCols];
		}
		
		// Allocate array lists for columns
		columns = new ArrayList<ArrayList<String>>(nCols);
		for (int c = 0; c < nCols; c++)
		{
			columns.add(new ArrayList<String>());
		}
		
		if (!readHeader)
		{
			// read column values as strings
			int offset = readRowNames ? 1 : 0;
			for (int c = 0; c < nCols; c++)
			{
				columns.get(c).add(tokens[c + offset]);
			}
		}
		
		// read regular lines
		while (true)
		{
			String line = reader.readLine(); 
			if (line == null)
			{
				break;
			}
			if (line.isEmpty())
			{
				break;
			}
			
			nRows++;
		
			tokens = line.split(delimiterRegexp);
			
			// read row name
			if (readRowNames)
			{
				rowNames.add(tokens[0]);
				// read column values as strings
				for (int c = 0; c < nCols; c++)
				{
					columns.get(c).add(tokens[c + 1]);
				}			
			}
			else
			{
				// read column values as strings
				for (int c = 0; c < nCols; c++)
				{
					columns.get(c).add(tokens[c]);
				}			
			}
		}
		
		reader.close();
		
		// convert columns
        DataTable table = new DataTable(nRows, nCols);
		
		// convert string arrays to double values
		for (int c = 0; c < nCols; c++)
		{
			ArrayList<String> column = columns.get(c);
			
//			ArrayList<String> levelTokens = new ArrayList<String>();
//			boolean isNumeric = true;
			
			for (int r = 0; r < nRows; r++)
			{
				String token = column.get(r);
				
				// test if contains a numeric value or not
				if(token.matches(".*\\d.*"))
				{
				    // numeric value
				    double value = Double.parseDouble(token);
                    table.setValue(r, c, value);
				}
				else
				{
                    throw new  RuntimeException("Can only import tables containing numeric values.");
//				    if (!(levelTokens.contains(token)))
//				    {
//				        levelTokens.add(token);
//				    }
//				    isNumeric = false;
				}
			}
            
//            // If column is not numeric, setup levels and store level indices 
//            if (!isNumeric)
//            {
//                // setup levels
//                String[] levels = levelTokens.toArray(new String[]{});
//                table.setLevels(c, levels);
//                
//                // compute level index for each column
//                for (int r = 0; r < nRows; r++)
//                {
//                    String token = column.get(r);
//                    table.setValue(r, c, levelTokens.indexOf(token));
//                }
//            }
		}

		// populates meta-data
		table.setColumnNames(colNames);
		if (readRowNames)
		{
			table.setRowNames(rowNames.toArray(new String[0]));
		}
		
		// also set the name of the table to the name of the file
		table.setName(file.getName());
		
		return table;
	}
}
