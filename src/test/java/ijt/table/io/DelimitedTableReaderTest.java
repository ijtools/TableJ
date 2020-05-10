/**
 * 
 */
package ijt.table.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ijt.table.CategoricalColumn;
import ijt.table.DataTable;
import ijt.table.NumericColumn;

/**
 * @author dlegland
 *
 */
public class DelimitedTableReaderTest
{

    /**
     * Test method for {@link ijt.table.io.DelimitedTableReader#readTable(java.io.File)}.
     * @throws IOException 
     */
    @Test
    public void testReadTable() throws IOException
    {
        String fileName = getClass().getResource("/tables/fisherIris.txt").getFile();
        File file = new File(fileName);
        
        TableReader reader = new DelimitedTableReader();
        
        DataTable table = reader.readTable(file);
        
        assertEquals(150, table.rowNumber());
        assertEquals(5, table.columnNumber());
        
        assertTrue(table.getColumn(0) instanceof NumericColumn);
        assertTrue(table.getColumn(1) instanceof NumericColumn);
        assertTrue(table.getColumn(2) instanceof NumericColumn);
        assertTrue(table.getColumn(3) instanceof NumericColumn);
        assertTrue(table.getColumn(4) instanceof CategoricalColumn);
//        table.print(System.out);
    }

}
