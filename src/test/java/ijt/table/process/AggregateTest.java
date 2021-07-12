/**
 * 
 */
package ijt.table.process;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ijt.table.CategoricalColumn;
import ijt.table.Column;
import ijt.table.Table;
import ijt.table.io.DelimitedTableReader;
import ijt.table.io.TableReader;

/**
 * @author dlegland
 *
 */
public class AggregateTest
{
    /**
     * Test method for {@link ijt.table.process.Aggregate#process(ijt.table.Table)}.
     * @throws IOException 
     */
    @Test
    public void testProcess_String_irisSpecies() throws IOException
    {
        Table table = readIrisTable();
        
        Table res = new Aggregate().process(table, "Species");
        
        assertNotNull(res);
        assertEquals(3, res.rowCount());
        assertEquals(4, res.columnCount());
        assertTrue(res.getValue(2, 0) > res.getValue(2, 3));
    }

    /**
     * Test method for {@link ijt.table.process.Aggregate#process(ijt.table.Table)}.
     * @throws IOException 
     */
    @Test
    public void testProcess_Column_irisSpecies() throws IOException
    {
        Table table = readIrisTable();
        
        int nRows = table.rowCount();
        Table data = Table.create(nRows, 4);
        for (int i = 0; i < 4; i++)
        {
            data.setColumnName(i, table.getColumnName(i));
            data.setColumn(i, table.getColumn(i));
        }
        Column species = table.getColumn(4);
        
        Table res = new Aggregate().process(data, (CategoricalColumn) species);
        
        assertNotNull(res);
        assertEquals(3, res.rowCount());
        assertEquals(4, res.columnCount());
        assertTrue(res.getValue(2, 0) > res.getValue(2, 3));
    }
    
    private Table readIrisTable() throws IOException
    {
        String fileName = getClass().getResource("/tables/fisherIris.txt").getFile();
        TableReader reader = new DelimitedTableReader();
        return reader.readTable(new File(fileName));
    }
}
