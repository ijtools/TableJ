/**
 * 
 */
package ijt.table.gui;

import java.util.Collection;
import java.util.TreeMap;

import ijt.table.Table;

/**
 * Keeps a list of DataTable indexed by their names. Ensure names are unique.
 * 
 * @author dlegland
 *
 */
public class TableManager
{
    // =============================================================
    // Management of a global static instance
    
    private static TableManager instance = null;
    
    /**
     * Returns the unique global instance of TableManager. Not that other instances
     * may be created independently.
     * 
     * @return the unique global instance of TableManager.
     */
    public final static TableManager getInstance()
    {
        if (instance == null)
        {
            instance = new TableManager();
        }
        return instance;
    }

    
    // =============================================================
    // class variables

    /**
     * The list of DataTable instances managed by the application.
     */
    TreeMap<String, Table> tables = new TreeMap<String, Table>();
    
    
    // =============================================================
    // Creation of new handles

    /**
     * Creates a unique name for a table, given a base name (file name). If the
     * manager already contains a table with same base name, an index is added to
     * make the name unique.
     * 
     * @param baseName a base name for the table, for example the file name
     * @return a unique name based on the filename.
     */
    public String createTableName(String baseName)
    {
        // avoid empty table names
        if (baseName == null || baseName.isEmpty())
        {
            baseName = "NoName";
        }
        
        // if no table with such name exist, just keep it
        if (!hasTableWithName(baseName))
        {
            return baseName;
        }
        
        // otherwise, we first check if name contains an "index"
        // here: the number(s) at the end of the name, before the extension, separated by a dash
        
        // extract the sting containing extension (with final dot)
        String extString = "";
        int len = baseName.length();
        int dotIndex = baseName.lastIndexOf(".");
        // use extension with up to four characters
        if (dotIndex !=-1 && (len - dotIndex) < 6) 
        {
            extString = baseName.substring(dotIndex, len);
            baseName = baseName.substring(0, dotIndex);
        }
        
        // identifies the set of digits at the end of name 
        String digits = new String("0123456789");
        int lastIndex = baseName.length() - 1;
        String currentChar = ""; 
        while (lastIndex > 0)
        {
            currentChar = baseName.substring(lastIndex, lastIndex + 1);
            // iterates until a non digit character is found
            if (!digits.contains(currentChar))
            {
                break;
            }
            lastIndex--;
        }
        
        // check end of name matches an indexed image name pattern
        if (lastIndex < baseName.length() - 1 && currentChar.equals("-"))
        {
            baseName = baseName.substring(0, lastIndex);
        }
        
        // create names with the pattern until we found a non existing one
        int index = 1;
        while (true)
        {
            String newName = baseName + "-" + index + extString;
            if (!hasTableWithName(newName))
            {
                return newName;
            }
            index++;
        }
    }
    
    private boolean hasTableWithName(String name)
    {
        return tables.containsKey(name);
    }


    // =============================================================
    // Get data
    
    /**
     * Get a table from its name, or throw an exception if the table can not be
     * found.
     * 
     * @param name
     *            the name of the table (case sensitive)
     * @return the table indexed with the name.
     */
    public Table getTable(String name)
    {
        Table table = tables.get(name);
        if (table == null)
        {
            throw new RuntimeException("Does not contain any table with name: " + name);
        }
        return table;
    }
    
    public void addTable(Table table)
    {
        String tableName = table.getName();
        if (tables.containsKey(tableName))
        {
            throw new IllegalArgumentException("Already contains a table with name: " + tableName);
        }
        
        this.tables.put(tableName, table);
    }

    public void removeTable(String tableName)
    {
        if (!tables.containsKey(tableName))
        {
            throw new IllegalArgumentException("Does not contain any table indexed with name: " + tableName);
        }
        
        this.tables.remove(tableName);
    }
    
    public Collection<String> getTableNames()
    {
        return tables.keySet();
    }
    
    public Collection<Table> getTables()
    {
        return tables.values();
    }
    
    /**
     * Removes all the tables from this manager.
     */
    public void clear()
    {
        tables.clear();
    }
}
