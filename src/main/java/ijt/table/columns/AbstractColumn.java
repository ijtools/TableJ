/**
 * 
 */
package ijt.table.columns;

import ijt.table.Column;

/**
 * An utility class that serves as a basis for more specialized Column
 * implementations.
 */
public abstract class AbstractColumn implements Column
{
    /**
     * The name of the column.
     */
    protected String name;
    
    protected AbstractColumn()
    {
        this("");
    }
    
    protected AbstractColumn(String name)
    {
        this.name = name;
    }
    
    
    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String newName)
    {
        this.name = newName;
    }
}
