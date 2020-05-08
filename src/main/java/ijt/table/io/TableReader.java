/**
 * 
 */
package ijt.table.io;

import java.io.File;
import java.io.IOException;

import ijt.table.DataTable;

/**
 * @author dlegland
 *
 */
public interface TableReader
{
	public DataTable readTable(File file) throws IOException;
}
