/**
 * 
 */
package ijt.table.gui.action.edit;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import ij.IJ;
import ij.gui.GenericDialog;
import ijt.table.CategoricalColumn;
import ijt.table.Column;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * Select rows of a table based on the values within a table, based on a logical
 * condition on the values contained in one of the columns. Returns a new table,
 * with the same number of columns.
 */
public class FilterRows implements TableFrameAction
{
    @Override
    public void run(TableFrame frame)
    {
        // Get the data table
        if (!(frame instanceof TableFrame))
        {
            return;
        }
        Table table = ((TableFrame) frame).getTable();

        // get general info from table
        String[] colNames = table.getColumnNames();

        // Display dialog for choosing options
        GenericDialog dlg = new GenericDialog("Filter Table Rows");
        dlg.addChoice("Column", colNames, colNames[0]);
        dlg.addChoice("Operation", RelationalOperator.getAllLabels(), RelationalOperator.GT.toString());
        dlg.addNumericField("Value", 0.0, 2);
        
        int colIndex = 0;
        RelationalOperator op = null;
        double value = Double.NaN; 
        
        while (Double.isNaN(value)) 
        {
            dlg.showDialog();
            // wait for user input
            if (dlg.wasCanceled()) 
            {
                return;
            }
            
            // Parse dialog contents
            colIndex = table.findColumnIndex(dlg.getNextChoice());
            op = RelationalOperator.fromLabel(dlg.getNextChoice());
            value = dlg.getNextNumber();
            if (!Double.isNaN(value))
            {
                break;
            }
            IJ.error("Impossible to parse value", "Number error");
        }
        
        // identify index of rows to keep
        int nRows = table.rowCount();
        ArrayList<Integer> rowIndices = new ArrayList<Integer>(nRows);
        for (int i = 0; i < nRows; i++)
        {
            if (op.process(table.getValue(i, colIndex), value))
            {
                rowIndices.add(i);
            }
        }
        
        // create a new table from filtered columns
        Column[] newCols = table.columns().stream()
                .map(col -> selectRows(col, rowIndices))
                .toArray(Column[]::new);
        Table res = Table.create(newCols);
        
        // Default name for table
        String tableName = table.getName();
        if (tableName == null || tableName.length() == 0)
        {
            tableName = "data";
        }
        res.setName(tableName + "-rowFilt");
        
        // compute new row names
        if (table.hasRowNames())
        {
            String[] names = new String[rowIndices.size()];
            for (int i = 0; i < rowIndices.size(); i++)
            {
                names[i] = table.getRowName(rowIndices.get(i));
            }
            res.setRowNames(names);
        }
        
        // add the new frame to the GUI
        TableFrame.create(res, frame);
    }
    private static final Column selectRows(Column column, ArrayList<Integer> rowIndices)
    {
        if (column instanceof NumericColumn)
        {
            NumericColumn numCol = (NumericColumn) column;
            NumericColumn res = numCol.newInstance(column.getName(), rowIndices.size());
            for (int i = 0; i < rowIndices.size(); i++)
            {
                res.setValue(i, numCol.getValue(rowIndices.get(i))); 
            }
            return res;
        }
        
        if (column instanceof CategoricalColumn)
        {
            CategoricalColumn catCol = (CategoricalColumn) column;
            String[] values = new String[rowIndices.size()];
            for (int i = 0; i < rowIndices.size(); i++)
            {
                values[i] = catCol.getString(rowIndices.get(i)); 
            }
            return CategoricalColumn.create(column.getName(), values);
        }
        
        throw new RuntimeException("Unable to manage column with type: " + column.getClass());
    }
    
    public enum RelationalOperator
    {
        GT("Greater Than", (a,b) -> a > b),
        LT("Lower Than", (a,b) -> a < b),
        GE("Greater Or Equal", (a,b) -> a >= b),
        LE("Lower Or Equal", (a,b) -> a <= b),
        EQ("Equal", (a,b) -> a == b),
        NE("Not Equal", (a,b) -> a != b);
        
        private String label;
        private BiFunction<Double,Double,Boolean> function;
        
        private RelationalOperator(String label, BiFunction<Double,Double,Boolean> function)
        {
            this.label = label;
            this.function = function;
        }
        
        public boolean process(double a, double b)
        {
            return function.apply(a, b);
        }
        
        /**
         * Converts this relational operator into a string.
         * 
         * @return a String representation of this RelationalOperator
         */
        public String toString() 
        {
            return this.label;
        }
        
        /**
         * Returns the list of labels for this enumeration.
         * 
         * @return the list of labels for this enumeration.
         */
        public static String[] getAllLabels()
        {
            return Stream.of(RelationalOperator.values())
                    .map(op -> op.label)
                    .toArray(String[]::new);
        }
        
        /**
         * Determines the RelationalOperator enumeration item from its label.
         * 
         * @param label
         *            the label of the RelationalOperator
         * @return the parsed RelationalOperator
         * @throws IllegalArgumentException
         *             if label is not recognized.
         */
        public static RelationalOperator fromLabel(String label)
        {
            for (RelationalOperator item : RelationalOperator.values()) 
            {
                if (item.label.equalsIgnoreCase(label)) return item;
            }
            throw new IllegalArgumentException("Unable to parse RelationalOperator with label: " + label);
        }
    }
}
