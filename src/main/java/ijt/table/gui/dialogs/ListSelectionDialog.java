/**
 * 
 */
package ijt.table.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Choose a selection of items by displaying a scrollable JLIst.
 * 
 */
public class ListSelectionDialog
{
    /**
     * Opens a dialog representing the different items within a scrollable list,
     * and returns the array of selected item indices. If user clicks on the
     * "Cancel" button, an empty array is returned.
     * 
     * @param frameComp
     *            a component belonging to the parent frame, or the parent frame
     *            itself.
     * @param labelText
     *            the text to display on top of the list
     * @param dlgTitle
     *            the title of the dialog
     * @param values
     *            the array of possible values, as an array of String
     * @return the array of selected values indices.
     */
    public static final int[] selectElementIndices(Component frameComp, String labelText, String dlgTitle, String[] values)
    {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        ListSelectionDialog dialog = new ListSelectionDialog(frame, labelText, dlgTitle, values);
        dialog.setVisible(true);
        return dialog.selectedIndices;  
    }

    JDialog dlg;
    
    int[] selectedIndices = new int[0];
    
    JList<String> list;
    JLabel listLabel;
    JButton okButton;
    JButton cancelButton;
    
    private ListSelectionDialog(Frame refFrame,
            String labelText,
            String dlgTitle,
            String[] values)
    {
        this.dlg = new JDialog(refFrame, dlgTitle, true);
        
        createWidgets(values, labelText);
        setupLayout();
        dlg.setLocationRelativeTo(refFrame);
        
        this.dlg.setVisible(true);
    }
    
    private void createWidgets(String[] values, String labelText)
    {
        listLabel = new JLabel(labelText);
        
        // create a list with multiple psb selections
        list = new JList<String>(values);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(15);
        
        // create control buttons
        okButton = new JButton("OK");
        okButton.addActionListener(evt -> {
            selectedIndices = list.getSelectedIndices();
            setVisible(false);
        });
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(evt -> setVisible(false));
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
    
    public void setVisible(boolean state)
    {
        this.dlg.setVisible(state);
    }
    
    public static void main(String... args)
    {
        String[] names = new String[] {"Tokyo", "Paris", "Berlin", "Reykjavik", "Seoul", "Madrid", "London", "Rome"};
        int[] indices = ListSelectionDialog.selectElementIndices(new JFrame(), "Choose Cities:", "City Chooser", names);
        for (int i : indices)
        {
            System.out.println(names[i]);
        }
    }
}
