/**
 * 
 */
package ijt.table.gui.action.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;

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
import ijt.table.columns.IntegerColumn;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.action.file.OpenDemoTable;

/**
 * 
 */
public class ConvertNumericColumnToInteger implements TableFrameAction
{

    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();
        new CustomDialog(frame, "Convert To Categorical Column", table);
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
        TableFrame refFrame;
        Table table;
        
        String[] numColNames; 

        // widgets
        JDialog dlg;

        JList<String> list;
        JComboBox<String> resultsComboBox;
        JButton okButton = new JButton("OK");
        
        public CustomDialog(TableFrame refFrame, String dlgTitle, Table refTable)
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
//            gbc.gridx = 0;
//            gbc.gridy = row;
//            gbc.gridwidth = 1;
//            mainPanel.add(new JLabel("Transform:"), gbc);
            
//            gbc.gridx = 1;
//            gbc.gridwidth = 1;
//            gbc.fill = GridBagConstraints.HORIZONTAL;
//            mainPanel.add(transformComboBox, gbc);
            
//            row++;
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
                    IntegerColumn col2 = convert(col);
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
                    newColumns[i] = convert(col);
                    newColumns[i].setName(col.getName() + "_cat");
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
                // convert columns
                for (int i = 0; i < inds.length; i++)
                {
                    int colIndex = table.findColumnIndex(this.numColNames[inds[i]]);
                    NumericColumn col = (NumericColumn) table.getColumn(colIndex);
                    newColumns[i] = convert(col);
                }

                // create new table
                Table res = Table.create(newColumns);
                res.setName(table.getName() + "-fact");
                TableFrame.create(res, refFrame);
            }
        }
    }
    
    private static final IntegerColumn convert(NumericColumn col)
    {
        if (col instanceof IntegerColumn)
        {
            return convertIntegerColumn((IntegerColumn) col);
        }
        int n = col.size();
        int[] data = new int[col.size()];
        for (int i = 0; i < n; i ++)
        {
            // add 0.5 for rounding
            data[i] = (int) (col.getValue(i) + 0.5);
        }
        
        return NumericColumn.create(col.getName(), data); 
    }

    private static final IntegerColumn convertIntegerColumn(IntegerColumn col)
    {
        return NumericColumn.create(col.getName(), col.getIntValues()); 
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
