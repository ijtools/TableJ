/**
 * 
 */
package ijt.table;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * 
 */
class TablesTest
{
    /**
     * Test method for {@link ijt.table.Tables#mergeColumns(ijt.table.Table, ijt.table.Table[])}.
     */
    @Test
    final void testMergeColumns()
    {
        int nr = 5;
        Table table1 = Table.create(nr, 3);
        Table table2 = Table.create(nr, 4);
        
        Table res = Tables.mergeColumns(table1,  table2);
        
        assertEquals(nr, res.rowCount());
        assertEquals(7, res.columnCount());
    }

    /**
     * Test method for {@link ijt.table.Tables#mergeRows(ijt.table.Table, ijt.table.Table[])}.
     */
    @Test
    final void testMergeRows()
    {
        int nc = 4;
        Table table1 = Table.create(6, nc);
        Table table2 = Table.create(7, nc);
        
        Table res = Tables.mergeRows(table1,  table2);
        
        assertEquals(13, res.rowCount());
        assertEquals(nc, res.columnCount());
    }

//    /**
//     * Test method for {@link ijt.table.Tables#crossTable(ijt.table.CategoricalColumn, ijt.table.CategoricalColumn)}.
//     */
//    @Test
//    final void testCrossTable()
//    {
//        fail("Not yet implemented"); // TODO
//    }

}
