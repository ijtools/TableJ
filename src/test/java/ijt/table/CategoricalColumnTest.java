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
public class CategoricalColumnTest
{

    /**
     * Test method for {@link ijt.table.CategoricalColumn#levelNumber()}.
     */
    @Test
    public void testLevelNumber()
    {
        String[] levels = new String[] {"yes", "yes", "no", "no", "yes", "no"};
        CategoricalColumn col = new CategoricalColumn(levels);
        
        int nLevels = col.levelNumber();
        
        assertEquals(2, nLevels);
    }

    /**
     * Test method for {@link ijt.table.CategoricalColumn#iterator()}.
     */
    @Test
    public void testIterator()
    {
        String[] levels = new String[] {"yes", "yes", "no", "no", "yes", "no"};
        CategoricalColumn col = new CategoricalColumn(levels);

        int count = 0;
        for (@SuppressWarnings("unused") String str : col)
            count++;
            
        assertEquals(count, 6);
    }

}
