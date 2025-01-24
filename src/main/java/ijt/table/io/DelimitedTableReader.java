/**
 * 
 */
package ijt.table.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import ijt.table.CategoricalColumn;
import ijt.table.NumericColumn;
import ijt.table.Table;

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

    public Table readTable(InputStream stream) throws IOException
    {
        // meta data for table
        
        // number of columns
        int nCols;
        
        // table data, indexed by columns first.
        ArrayList<ArrayList<String>> columns;
        
        // the names of the columns
        String[] colNames;
        
        // the names of the rows
        ArrayList<String> rowNames = new ArrayList<String>();

        // convert stream to text reader that keep line number
        LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(stream)));
        
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
        
        // read regular lines containing data
        while (true)
        {
            String line = reader.readLine(); 
            if (line == null || line.isEmpty())
            {
                break;
            }
            nRows++;

            // split the tokens of current line
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
        Table table = Table.create(nRows, nCols);
        
        // convert string arrays to double values
        for (int c = 0; c < nCols; c++)
        {
            // tokens of current column
            ArrayList<String> colData = columns.get(c);
            
            // check if column contains only numeric values
            double[] values = new double[nRows];
            boolean isNumeric = true;
            for (int r = 0; r < nRows; r++)
            {
                String token = colData.get(r);
                if(token.matches(".*\\d.*"))
                {
                    values[r] =  Double.parseDouble(token);
                }
                else
                {
                    isNumeric = false;
                    break;
                }
            }
            
            if (isNumeric)
            {
                table.setColumn(c, NumericColumn.create(values));
            }
            else
            {
                table.setColumn(c, CategoricalColumn.create(colData.toArray(new String[0])));
            }
        }

        // populates meta-data
        table.setColumnNames(colNames);
        if (readRowNames)
        {
            table.setRowNames(rowNames.toArray(new String[0]));
        }
        
        return table;
    }
    
    
    // =============================================================
    // implementation of the TableReader interface

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.table.io.TableReader#readTable()
     */
    @Override
    public Table readTable(File file) throws IOException
    {
        // create input stream from file, and call the delegated method.
        Table table = readTable(new FileInputStream(file));

        // set the name of the table to the name of the file
        table.setName(file.getName());

        return table;
    }
}
