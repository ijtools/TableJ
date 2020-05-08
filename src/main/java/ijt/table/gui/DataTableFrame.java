/**
 * 
 */
package ijt.table.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import ijt.table.DataTable;
import ijt.table.RowNumberTable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Display the contents of a DataTable object into a JTable embedded into a
 * JFrame.
 * 
 * @author David Legland
 *
 */
public class DataTableFrame extends JFrame implements WindowListener, ActionListener
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    DataTable table;

    public DataTableFrame(DataTable table)
    {
        super("Data Table");

        this.table = table;

        // setup layout and menus
        setupLayout();
        setupMenu();
        pack();

        // setup listeners
        addWindowListener(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupLayout()
    {
        // Need to cast table data to object array...
        int nRows = table.getRowNumber();
        int nCols = table.geColumnNumber();
        Object[][] dats = new Object[nRows][nCols];
        for (int r = 0; r < nRows; r++)
        {
            for (int c = 0; c < nCols; c++)
            {
                dats[r][c] = table.getValue(r, c);
            }
        }

        // Create JTable instance
        String[] colNames = table.getColumnNames();
        if (colNames == null)
        {
            colNames = new String[nCols];
            for (int c = 0; c < nCols; c++)
                colNames[c] = Integer.toString(c + 1);
        }
        JTable jTable = new JTable(dats, colNames);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Create the frame containing the table
        setPreferredSize(new Dimension(400, 300));

        // Setup layout
        Container panel = getContentPane();

        JScrollPane scrollPane = new JScrollPane(jTable);
        JTable rowTable = new RowNumberTable(jTable);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        // panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(jTable.getTableHeader(), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupMenu()
    {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        addMenuItem(fileMenu, "Save As...", new SaveAsAction());
        addMenuItem(fileMenu, "Quit", new ExitAction());
        bar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        addMenuItem(helpMenu, "About...", new AboutAction());
        bar.add(helpMenu);

        this.setJMenuBar(bar);
    }

    private void addMenuItem(JMenu parent, String label, Action action)
    {
        JMenuItem item = new JMenuItem(action);
        item.setText(label);
        parent.add(item);
    }

    @Override
    public void windowActivated(WindowEvent evt)
    {
    }

    @Override
    public void windowClosed(WindowEvent evt)
    {
    }

    @Override
    public void windowClosing(WindowEvent evt)
    {
        System.out.println("close");
        dispose();
    }

    @Override
    public void windowDeactivated(WindowEvent evt)
    {
    }

    @Override
    public void windowDeiconified(WindowEvent evt)
    {
    }

    @Override
    public void windowIconified(WindowEvent evt)
    {
    }

    @Override
    public void windowOpened(WindowEvent evt)
    {
    }

    @Override
    public void actionPerformed(ActionEvent evt)
    {
    }

    private class SaveAsAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("save as...");
        }
    }

    private class ExitAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e)
        {
            dispose();
        }
    }

    private class AboutAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("About...");
        }
    }

    /**
     * A simple main function to quickly test GUI on a toy example.
     */
    public static void main(String[] args)
    {
        DataTable tbl = new DataTable(15, 5);
        tbl.setColumnNames(new String[] { "length", "area", "diameter", "number", "density" });
        JFrame frame = new DataTableFrame(tbl);
        frame.setVisible(true);
    }

}
