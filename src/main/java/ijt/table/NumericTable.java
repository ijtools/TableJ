/**
 * 
 */
package ijt.table;

import java.util.Collection;

/**
 * A specialization of the Table interface that contains only numeric columns.
 * 
 * @author dlegland
 *
 */
public interface NumericTable extends Table
{
    // =============================================================
    // Override some methods from Table interface

    public NumericColumn getColumn(int index);
    
    public Collection<? extends NumericColumn> columns();
}
