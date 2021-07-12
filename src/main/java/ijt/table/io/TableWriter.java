/**
 * 
 */
package ijt.table.io;

import java.io.IOException;

import ijt.table.Table;

/**
 * Defines the interface for writing data table.
 * 
 * @author dlegland
 *
 */
public interface TableWriter
{
	public void writeTable(Table table) throws IOException;
}
