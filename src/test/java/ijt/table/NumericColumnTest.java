/**
 * 
 */
package ijt.table;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class NumericColumnTest
{

//    /**
//     * Test method for {@link ijt.table.NumericColumn#levelNumber()}.
//     */
//    @Test
//    public void testLevelNumber()
//    {
//        NumericColumn col = new NumericColumn("test", 6);
//        
//        assertEquals(2, nLevels);
//    }

    /**
     * Test method for {@link ijt.table.NumericColumn#iterator()}.
     */
    @Test
    public void testIterator()
    {
        NumericColumn col = NumericColumn.create(new double[6]);

        int count = 0;
        for (@SuppressWarnings("unused") double v : col)
            count++;
            
        assertEquals(count, 6);
    }

}
