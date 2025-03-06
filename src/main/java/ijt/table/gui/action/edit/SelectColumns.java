/**
 * 
 */
package ijt.table.gui.action.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;


/**
 * Opens a dialog to choose a selection of columns, and create a new table
 * containing only these columns.
 * 
 * @author dlegland
 */
public class SelectColumns implements TableFrameAction
{

    /* (non-Javadoc)
     * @see imago.gui.Plugin#run(imago.gui.ImagoFrame, java.lang.String)
     */
    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();

        // get general info from table
        int nRows = table.rowCount();
        String[] colNames = table.getColumnNames();

        // Opens a custom dialog to choose the name of columns to keep
        SelectionDialog dlg = new SelectionDialog(frame.getJFrame(), colNames);
        if (dlg.wasCanceled()) 
        {
            return;
        }
        
        // retrieve index of chosen columns
        int[] columnIndices = dlg.selectedIndices;
        
        // Default name for table
        String baseName = table.getName();
        if (baseName == null || baseName.length() == 0)
        {
            baseName = "data";
        }
        
        Table res = Table.create(nRows, columnIndices.length);
        for (int i = 0; i < columnIndices.length; i++)
        {
            int index = columnIndices[i];
            res.setColumn(i, table.getColumn(index));
            res.setColumnName(i, colNames[index]);
        }
        
        if (table.hasRowNames())
        {
            res.setRowNames(table.getRowNames());
        }
        res.setName(baseName + "-colSel");
        
        // add the new frame to the GUI
        TableFrame.create(res, frame);
    }

    private static class SelectionDialog
    {
        JDialog dlg;
        
        public enum Output {
            OK, CANCEL;
        };
        private Output output = Output.CANCEL;
        
        int[] selectedIndices = null;
        
        JList<String> list;
        JButton okButton;
        JButton cancelButton;
        
        public SelectionDialog(JFrame refFrame, String[] values)
        {
            this.dlg = new JDialog(refFrame, "Select Columns", true);
            
            createWidgets(values);
            setupLayout();
            dlg.setLocationRelativeTo(refFrame);
            
            this.dlg.setVisible(true);
        }
        
        private void createWidgets(String[] values)
        {
            // create a list with multiple psb selections
            list = new JList<String>(values);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            list.setVisibleRowCount(15);
            
            // create control buttons
            okButton = new JButton("OK");
            okButton.addActionListener(evt -> {
                selectedIndices = list.getSelectedIndices();
                this.dlg.setVisible(false);
                this.output = Output.OK;
            });
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(evt -> {
                this.dlg.setVisible(false);
                this.output = Output.CANCEL;
            });
        }
        
        private void setupLayout()
        {
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            
            // encapsulates the list widget into a scroll panel
            JScrollPane listScroller = new JScrollPane(list);
            listScroller.setPreferredSize(new Dimension(200, 200));
            
            // create a panel to show the list together with a label
            JPanel listPanel = new JPanel(new BorderLayout());
            listPanel.add(new JLabel("Choose Columns:"), BorderLayout.NORTH);
            listPanel.add(listScroller, BorderLayout.CENTER);
            listPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            mainPanel.add(listPanel, BorderLayout.CENTER);
            
            // lay out the buttons from left to right.
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonsPanel.add(okButton);
            buttonsPanel.add(cancelButton);
            mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
            
            dlg.setContentPane(mainPanel);
            dlg.pack();
        }
        
        /** Returns true if the user clicked on "Cancel". */
        public boolean wasCanceled() 
        {
            return output == Output.CANCEL;
        }
    }
    
    public static void main(String... args)
    {
        String[] names = new String[] {"Tokyo", "Paris", "Berlin", "Reykjavik", "Seoul", "Madrid", "London", "Roma"};
        SelectionDialog dlg = new SelectionDialog(new JFrame(""), names);
        if (!dlg.wasCanceled())
        {
            for (int i : dlg.selectedIndices)
            {
                System.out.println(names[i]);
            }
        }
    }
}
