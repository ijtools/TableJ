/**
 * 
 */
package ijt.table.io;

/**
 * Common delimiters used in CSV files.
 */
public enum Delimiters
{
    TAB("Tabulation ('\t')", "\t"),
    SEMICOLON("Semi-Colon (';')", ";"),
    COMA("Coma (',')", ","),
    SPACE("Space (' ')", " "),
    WHITE_SPACES("White Spaces (' \t')", " \t");
    
    public String toString()
    {
        return this.label;
    }

    public static String[] getAllLabels()
    {
        int n = Delimiters.values().length;
        String[] result = new String[n];

        int i = 0;
        for (Delimiters weight : Delimiters.values())
            result[i++] = weight.label;

        return result;
    }

    /**
     * Determines the operation type from its label.
     * 
     * @param label the name of a chamfer weight 
     * @return the Delimiter enum corresponding to the given name
     * 
     * @throws IllegalArgumentException
     *             if label name is not recognized.
     */
    public static Delimiters fromLabel(String label)
    {
        if (label != null)
            label = label.toLowerCase();
        for (Delimiters weight : Delimiters.values())
        {
            String cmp = weight.label.toLowerCase();
            if (cmp.equals(label))
                return weight;
        }
        throw new IllegalArgumentException(
                "Unable to parse Delimiter with label: " + label);
    }

    Delimiters(String label, String delim)
    {
        this.label = label;
        this.delimiter= delim;
    }
    
    public String  getDelimiter()
    {
        return delimiter;
    }
    
    String label;
    String delimiter;
}
