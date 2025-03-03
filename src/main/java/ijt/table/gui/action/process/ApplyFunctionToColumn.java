/**
 * 
 */
package ijt.table.gui.action.process;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

import ij.gui.GenericDialog;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.process.SummaryStatistics;

/**
 * 
 */
public class ApplyFunctionToColumn implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();

        // get general info from table
        int nCols = table.columnCount();
        String[] colNames = table.getColumnNames();

        // Display dialog for choosing options
        GenericDialog dlg = new GenericDialog("Select Columns", frame.getJFrame());

        String[] labels = Transformer.getAllLabels();
        dlg.addChoice("Function", labels, labels[0]);
        
        // add one check box for each column
        // TODO: replace with a List Choice
        int nCols2 = Math.min(nCols, 20);
        for (int i = 0; i < nCols2; i++)
        {
            dlg.addCheckbox(colNames[i], false);
        }
        // TODO: add an option to handle result
        dlg.showDialog();

        // wait for user input
        if (dlg.wasCanceled()) 
        {
            return;
        }
        
        // Parse dialog contents
        Transformer transformer = Transformer.fromLabel(dlg.getNextChoice());
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < nCols2; i++)
        {
            if (dlg.getNextBoolean())
            {
                indices.add(i);
            }
        }
        
        NumericColumn[] newCols = new NumericColumn[indices.size()];
        for (int i = 0; i < indices.size(); i++)
        {
            NumericColumn col = (NumericColumn) table.getColumn(table.findColumnIndex(colNames[indices.get(i)]));
            newCols[i] = transformer.process(col);
        }
        
        Table res = Table.create(newCols);
        res.setName(table.getName() + "-" + transformer.name());
        TableFrame.create(res, frame);
    }
    
    String[] getNumericColumnNames(Table table)
    {
        return table.columns().stream()
                .filter(col -> col instanceof NumericColumn)
                .map(col -> col.getName())
                .toArray(String[]::new);
    }

    enum Transformer
    {
        LOG("Log"),
        SQRT("Sqrt"),
        LOG10("Log10"), 
        ZSCORE("zscore");
        
        private String name;
        
        private Transformer(String name)
        {
            this.name = name;
        }
        
        public NumericColumn process(NumericColumn column)
        {
            String name = column.getName() + "-" + this.name;
            switch(this)
            {
                case LOG: return transform(column, name, Math::log);
                case SQRT: return transform(column, name, Math::sqrt);
                case LOG10: return transform(column, name, Math::log10);
                case ZSCORE: 
                {
                    double mean = SummaryStatistics.mean(column);
                    double std = SummaryStatistics.std(column);
                    return transform(column, name, v -> (v - mean) / std);
                }

                default:
                    throw new RuntimeException("Unable to process Function: " + this.name);
            }
        }
        
        private NumericColumn transform(NumericColumn column, String name, Function<Double, Double> fun)
        {
            int n = column.size();
            NumericColumn res = column.newInstance(name, n);
            for (int i = 0; i < n; i++)
            {
                res.setValue(i, fun.apply(column.getValue(i)));
            }
            return res;
        }
        
        /**
         * Converts this Transformer into a string.
         * 
         * @return a String representation of this Transformer
         */
        public String toString() 
        {
            return this.name;
        }
        
        /**
         * Returns the list of labels for this enumeration.
         * 
         * @return the list of labels for this enumeration.
         */
        public static String[] getAllLabels()
        {
            return Stream.of(Transformer.values())
                    .map(op -> op.name)
                    .toArray(String[]::new);
        }
        
        /**
         * Determines the Transformer enumeration item from its label.
         * 
         * @param label
         *            the label of the Transformer
         * @return the parsed Transformer
         * @throws IllegalArgumentException
         *             if label is not recognized.
         */
        public static Transformer fromLabel(String label)
        {
            for (Transformer item : Transformer.values()) 
            {
                if (item.name.equalsIgnoreCase(label)) return item;
            }
            throw new IllegalArgumentException("Unable to parse Function with label: " + label);
        }
    }
}
