/**
 * 
 */
package ijt.table.io;

import java.io.File;
import java.io.IOException;

import ijt.table.Table;

/**
 * @author dlegland
 *
 */
public interface TableReader
{
	public Table readTable(File file) throws IOException;
}
