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
import ijt.table.Column;
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

    /**
     * The delimiters between the tokens within the file. Default is " \t",
     * corresponding to either space or tabulation delimiter.
     */
    String delimiters = " \t";

    /**
     * Specifies if column header are present in the file. Default is true.
     */
    boolean readHeader = true;

    /**
     * The number of lines to skip before starting reading data. Default is 0.
     */
    int skipLines = 0;

    /**
     * Specifies if row names are present in the file. Default is true.
     */
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
        ArrayList<ArrayList<String>> columnTokens;
        
        // the names of the columns
        String[] colNames;
        
        // the names of the rows
        ArrayList<String> rowNames = new ArrayList<String>();

        // convert stream to text reader that keep line number
        LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(stream)));
        
        // eventually skip some lines
        skipHeaderLines(reader);
        
        // parse header line
        String firstLine = reader.readLine();
        String[] lineTokens = splitLine(firstLine);

        int columnOffset = readRowNames ? 1 : 0;
        
        // number of rows read
        int nRows = 0;
        
        // parse first line to identify number of columns 
        if (readHeader)
        {
            // first line is the header
            nCols = lineTokens.length - columnOffset;
            colNames = new String[nCols];
            for (int i = 0; i < nCols; i++)
            {
                colNames[i] = lineTokens[i + columnOffset];
            }
        }
        else
        {
            // first line is a data line
            nRows++;
            nCols = lineTokens.length;
            colNames = new String[nCols];
        }
        
        // Allocate a new list of tokens for each column
        columnTokens = new ArrayList<ArrayList<String>>(nCols);
        for (int c = 0; c < nCols; c++)
        {
            columnTokens.add(new ArrayList<String>());
        }
        
        if (!readHeader)
        {
            // read column values as strings
            for (int c = 0; c < nCols; c++)
            {
                columnTokens.get(c).add(lineTokens[c + columnOffset]);
            }
        }
        
        // read regular lines containing data
        nRows += readRegularLines(reader, columnTokens, rowNames);
        
        reader.close();
        
        // convert columns
        Table table = Table.create(nRows, nCols);
        
        // convert string arrays to double values when appropriate
        for (int c = 0; c < nCols; c++)
        {
            // tokens of current column
            ArrayList<String> colData = columnTokens.get(c);
            table.setColumn(c, createColumn(colData));
        }

        // populates meta-data
        table.setColumnNames(colNames);
        if (readRowNames)
        {
            table.setRowNames(rowNames.toArray(new String[0]));
        }
        
        return table;
    }
    
    private void skipHeaderLines(BufferedReader reader) throws IOException
    {
        for (int r = 0; r < skipLines; r++)
        {
            reader.readLine(); 
        }
    }
    
    private int readRegularLines(BufferedReader reader, ArrayList<ArrayList<String>> columnTokens, ArrayList<String> rowNames) throws IOException
    {
        int nRows = 0;
        int nCols = columnTokens.size();
        
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
            String[] lineTokens = splitLine(line);
            
            // read row name
            if (readRowNames)
            {
                rowNames.add(lineTokens[0]);
                // read column values as strings
                for (int c = 0; c < nCols; c++)
                {
                    columnTokens.get(c).add(lineTokens[c + 1]);
                }           
            }
            else
            {
                // read column values as strings
                for (int c = 0; c < nCols; c++)
                {
                    columnTokens.get(c).add(lineTokens[c]);
                }           
            }
        }
        
        return nRows;
    }
    
    private String[] splitLine(String line)
    {
        return line.split("[" + delimiters + "]+");
    }
    
    private static final Column createColumn(ArrayList<String> colData)
    {
        // check if column contains only numeric values
        double[] values = new double[colData.size()];
        boolean isNumeric = true;
        for (int r = 0; r < colData.size(); r++)
        {
            String token = colData.get(r);
            if(token.matches(".*\\d.*"))
            {
                values[r] = Double.parseDouble(token);
            }
            else
            {
                isNumeric = false;
                break;
            }
        }
        
        return isNumeric 
                ? NumericColumn.create(values) 
                : CategoricalColumn.create(colData.toArray(new String[0]));
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
    
    class Data
    {
        ArrayList<ArrayList<String>> columnTokens;
        ArrayList<String> columnNames;
        ArrayList<String> rowNames;
    }
}
