/**
 * 
 */
package ijt.table.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ijt.table.Table;
import ijt.table.NumericColumn;
import ijt.table.RowNumberTable;
import ijt.table.TableManager;
import ijt.table.gui.action.edit.CorrelationMatrix;
import ijt.table.gui.action.edit.Rename;
import ijt.table.gui.action.edit.SelectColumns;
import ijt.table.gui.action.edit.Summary;
import ijt.table.gui.action.file.Close;
import ijt.table.gui.action.file.OpenDemoTable;
import ijt.table.gui.action.file.SaveAs;
import ijt.table.gui.action.help.About;
import ijt.table.gui.action.plot.LinePlot;

/**
 * Display the contents of a DataTable object into a JTable embedded into a
 * JFrame.
 * 
 * @author David Legland
 *
 */
public class TableFrame extends JFrame implements WindowListener, ActionListener
{
    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    Table table;

    /**
     * Creates a new frame for displaying the content of the given DataTable.
     * 
     * The data table is indexed into the global table manager. Therefore the
     * name of the table may ba modified to make sure it is unique within the
     * manager.
     * 
     * @param table
     */
    public TableFrame(Table table)
    {
        super("Data Table");

        this.table = table;
        
        // index the table into the manager
        String name = table.getName();
        TableManager mgr = TableManager.getInstance();
        name = mgr.createTableName(name);
        table.setName(name);
        mgr.addTable(table);


        // setup layout and menus
        setupLayout();
        setupMenu();
        updateTitle();
        pack();

        // setup listeners
        addWindowListener(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupLayout()
    {
        // Convert table data into necessary format
        Object[][] data = convertTableToObjectArray(table);
        String[] colNames = computeDisplayColNames(table);
        
        // Create JTable instance
        JTable jTable = new JTable(data, colNames);
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
    
    private Object[][] convertTableToObjectArray(Table table)
    {
        // table size
        int nRows = table.rowCount();
        int nCols = table.columnCount();
        
        // number of columns for displaying row name (either 0 or 1)
        int nRowCols = table.hasRowNames() ? 1 : 0;
        
        // allocate memory
        Object[][] data = new Object[nRows][nRowCols + nCols];
        
        // eventually add row names
        if (nRowCols > 0)
        {
            for (int r = 0; r < nRows; r++)
            {
                data[r][0] = table.getRowName(r);
            }
        }
        
        // fill up data
        for (int c = 0; c < nCols; c++)
        {
            if (table.getColumn(c) instanceof NumericColumn)
            {
                for (int r = 0; r < nRows; r++)
                    data[r][c + nRowCols] = table.getValue(r, c);
            }
            else
            {
                for (int r = 0; r < nRows; r++)
                    data[r][c + nRowCols] = table.get(r, c);
            }
        }
        return data;
    }
    
    private String[] computeDisplayColNames(Table table)
    {
        int nCols = table.columnCount();

        // number of columns for displaying row name (either 0 or 1)
        int nRowCols = table.hasRowNames() ? 1 : 0;
        
        String[] colNames = new String[nCols + nRowCols];
        
        if (nRowCols > 0)
        {
            colNames[0] = "Row Names";
        }
        
        String[] baseColNames = table.getColumnNames();
        for (int c = 0; c < nCols; c++)
        {
            colNames[c + nRowCols] = baseColNames[c];
        }
        
        return colNames;
    }

    private void setupMenu()
    {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        addMenuItem(fileMenu, "Open Demo Table", new OpenDemoTable());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Save As...", new SaveAs());
        addMenuItem(fileMenu, "Close", new Close());
        bar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        addMenuItem(editMenu, "Rename", new Rename());
        addMenuItem(editMenu, "Select Columns...", new SelectColumns());
        editMenu.addSeparator();
        addMenuItem(editMenu, "Correlation Matrix", new CorrelationMatrix());
        addMenuItem(editMenu, "Summary", new Summary());
        bar.add(editMenu);

        JMenu plotMenu = new JMenu("Plot");
        addMenuItem(plotMenu, "Line Plot (Column)", new LinePlot());
        bar.add(plotMenu);

        JMenu helpMenu = new JMenu("Help");
        addMenuItem(helpMenu, "About...", new About());
        bar.add(helpMenu);

        this.setJMenuBar(bar);
    }

    private void addMenuItem(JMenu parent, String label, TableFrameAction action)
    {
        ActionRunner runner = new ActionRunner(this, action);
        JMenuItem item =new JMenuItem(runner);
        item.setText(label);
        parent.add(item);
    }
    
    
    public Table getTable()
    {
        return table;
    }
    
    
    public void repaint()
    {
        super.repaint();
        updateTitle();
    }
    
    
    public void updateTitle()
    {
        System.out.println("update title");
        if (table == null)
        {
            setTitle("No Table");
            return;
        }
        
        int nr = table.rowCount();
        int nc = table.columnCount();
        String title = String.format(Locale.US, "%s - %dx%d", table.getName(), nr, nc);
        
        setTitle(title);
    }
    
    
    /**
     * Creates a new frame to display the input data table, and initializes it
     * with from this frame.
     * 
     * @param table
     *            the table to display in the new frame.
     * @return the new table frame.
     */

    public TableFrame createNewTableFrame(Table table)
    {
        // creates the frame
        TableFrame newFrame = new TableFrame(table);
        
        // Compute position according to position of current frame
        Point pos = this.getLocation();
        int x = pos.x + 20;
        int y = pos.y + 20;
        newFrame.setLocation(x, y);
        
        newFrame.setVisible(true);
        
        // return result
        return newFrame;
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
//        System.out.println("close");
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

    /**
     * A simple main function to quickly test GUI on a toy example.
     */
    public static void main(String[] args)
    {
        // Create a basic data table
        Table tbl = new Table(15, 5);
        tbl.setColumnNames(new String[] { "length", "area", "diameter", "number", "density" });
        
        // Create the frame to display the table.
        JFrame frame = new TableFrame(tbl);
        frame.setVisible(true);
    }

}
