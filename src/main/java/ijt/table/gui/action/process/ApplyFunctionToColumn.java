/**
 * 
 */
package ijt.table.gui.action.process;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import ijt.table.Column;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.BaseFrame;
import ijt.table.gui.FramePlugin;
import ijt.table.gui.TableFrame;
import ijt.table.gui.action.file.OpenDemoTable;
import ijt.table.process.SummaryStatistics;

/**
 * 
 */
public class ApplyFunctionToColumn implements FramePlugin
{

    @Override
    public void run(BaseFrame frame, String options)
    {
        Table table = ((TableFrame) frame).getTable();
        new CustomDialog(frame, "Feature Transform", table);
    }
    
    private static final String[] getNumericColumnNames(Table table)
    {
        return table.columns().stream()
                .filter(col -> col instanceof NumericColumn)
                .map(col -> col.getName())
                .toArray(String[]::new);
    }

    static class CustomDialog
    {
        BaseFrame refFrame;
        Table table;
        
        String[] numColNames; 

        // widgets
        JDialog dlg;

        JComboBox<String> transformComboBox;
        JList<String> list;
        JComboBox<String> resultsComboBox;
        JButton okButton = new JButton("OK");
        
        public CustomDialog(BaseFrame refFrame, String dlgTitle, Table refTable)
        {
            this.dlg = new JDialog(refFrame.getJFrame(), dlgTitle, false);
            this.refFrame = refFrame;
            this.table = refTable;
            
            this.numColNames = getNumericColumnNames(table);
            
            // setup GUI
            createWidgets();
            setupLayout();
            this.dlg.setVisible(true);
        }
        
        private void createWidgets()
        {
            // create a list with multiple psb selections
            
            list = new JList<String>(this.numColNames);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            list.setVisibleRowCount(10);
            
            String[] labels = Transformer.getAllLabels();
            transformComboBox = new JComboBox<String>(labels);
            resultsComboBox = new JComboBox<String>(new String[] {"Replace Columns", "Append Columns", "New Table"});
            
            okButton.addActionListener(evt -> onOkButton());
        }

        private void setupLayout()
        {
            JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.BASELINE_LEADING;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // encapsulates the list widget into a scroll panel
            JScrollPane listScroller = new JScrollPane(list);
            listScroller.setPreferredSize(new Dimension(150, 150));
            
            // create a panel to show the list together with a label
            JPanel listPanel = new JPanel(new BorderLayout());
            listPanel.add(new JLabel("Choose Columns:"), BorderLayout.NORTH);
            listPanel.add(listScroller, BorderLayout.CENTER);
            
            int row = 0;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            mainPanel.add(new JLabel("Transform:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(transformComboBox, gbc);
            
            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            mainPanel.add(listPanel, gbc);
   
            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.weighty = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Results:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = row;
            mainPanel.add(this.resultsComboBox, gbc);
   
            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            mainPanel.add(new JLabel(" "), gbc);
            
            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            mainPanel.add(okButton, gbc);
            
            dlg.setContentPane(mainPanel);
            dlg.setMinimumSize(new Dimension(200, 200));
            dlg.setLocationRelativeTo(refFrame.getJFrame());
            dlg.pack();
        }
        
        private void onOkButton()
        {
            dlg.setVisible(false);
            
            // Parse dialog contents
            Transformer transformer = Transformer.fromLabel((String) transformComboBox.getSelectedItem());
            
            int[] inds = list.getSelectedIndices();
            if (inds.length == 0) return;
            
            Column[] newColumns = new Column[inds.length];
            
            int resultIndex = resultsComboBox.getSelectedIndex(); 
            if (resultIndex == 0)
            {
                // replace columns
                for (int i = 0; i < inds.length; i++)
                {
                    int colIndex = table.findColumnIndex(this.numColNames[inds[i]]);
                    NumericColumn col = (NumericColumn) table.getColumn(colIndex);
                    NumericColumn col2 = transformer.process(col);
                    table.setColumn(colIndex, col2);
                }
                refFrame.repaint();
            }
            else if (resultIndex == 1)
            {
                // append columns
                for (int i = 0; i < inds.length; i++)
                {
                    int colIndex = table.findColumnIndex(this.numColNames[inds[i]]);
                    NumericColumn col = (NumericColumn) table.getColumn(colIndex);
                    newColumns[i] = transformer.process(col);
                }
                
                // add new columns to the table
                for (int i = 0; i < inds.length; i++)
                {
                    Column col = newColumns[i];
                    table.addColumn(col.getName(), col);
                }
                refFrame.repaint();
            }
            else if (resultIndex == 2)
            {
                // create new table
                for (int i = 0; i < inds.length; i++)
                {
                    int colIndex = table.findColumnIndex(this.numColNames[inds[i]]);
                    NumericColumn col = (NumericColumn) table.getColumn(colIndex);
                    newColumns[i] = transformer.process(col);
                    
                  Table res = Table.create(newColumns);
                  res.setName(table.getName() + "-" + transformer.name());
                  TableFrame.create(res, refFrame);
                }
            }
        }
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

    public static void main(String... args) throws IOException
    {
        InputStream inputStream = OpenDemoTable.class.getResourceAsStream("/tables/fisherIris.txt");
        Table table = new ijt.table.io.DelimitedTableReader().readTable(inputStream);
        table.setName("fisherIris");

        Table numTable = Table.create(table.getColumn(0), table.getColumn(1), table.getColumn(2), table.getColumn(3));

        new CustomDialog(new TableFrame(numTable), "Feature Transform", numTable);
    }
}
