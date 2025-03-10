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
    final void testMergeRows_numerical()
    {
        int nc = 4;
        Table table1 = Table.create(6, nc);
        Table table2 = Table.create(7, nc);
        
        Table res = Tables.mergeRows(table1,  table2);
        
        assertEquals(13, res.rowCount());
        assertEquals(nc, res.columnCount());
    }

    /**
     * Test method for {@link ijt.table.Tables#mergeRows(ijt.table.Table, ijt.table.Table[])}.
     */
    @Test
    final void testMergeRows_categorical()
    {
        Column col11 = NumericColumn.create("col1", 4);
        Column col12 = CategoricalColumn.create("col2", new String[] {"iris", "tulip", "iris", "dandelion"});
        Table table1 = Table.create(col11, col12);
        Column col21 = NumericColumn.create("foo", 5);
        Column col22 = CategoricalColumn.create("bar", new String[] {"tulip", "rose", "dandelion", "iris", "poinsettia"});
        Table table2 = Table.create(col21, col22);
        
        Table res = Tables.mergeRows(table1,  table2);
        
        assertEquals(9, res.rowCount());
        assertEquals(2, res.columnCount());
        assertEquals(table1.getColumn(0).getName(), res.getColumn(0).getName());
        assertEquals(table1.getColumn(1).getName(), res.getColumn(1).getName());
        assertEquals(res.getColumn(1).getString(3), "dandelion");
        assertEquals(res.getColumn(1).getString(6), "dandelion");
        assertEquals(res.getColumn(1).getString(8), "poinsettia");
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
