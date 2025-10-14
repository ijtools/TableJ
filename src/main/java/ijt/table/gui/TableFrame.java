/**
 * 
 */
package ijt.table.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import ijt.table.Table;

/**
 * Display the contents of a DataTable object into a JTable embedded into a
 * JFrame.
 * 
 * @author David Legland
 *
 */
public class TableFrame extends BaseFrame
{
    /**
     * Creates a Frame that displays a table, and make it visible.
     * 
     * @param table
     *            the table to display in the frame
     * @return a new instance of TableFrame.
     */
    public static final TableFrame create(Table table)
    {
        TableFrame frame = new TableFrame(table);
        frame.setVisible(true);
        return frame;
    }
    
    /**
     * Creates a Frame that displays a table, and make it visible.
     * 
     * @param table
     *            the table to display in the frame
     * @param parentFrame
     *            another frame used to initialize the position of the new frame
     * @return a new instance of TableFrame.
     */
    public static final TableFrame create(Table table, BaseFrame parentFrame)
    {
        TableFrame frame = new TableFrame(table);

        // Compute position according to position of current frame
        Point pos = parentFrame.jFrame.getLocation();
        int x = pos.x + 20;
        int y = pos.y + 20;
        frame.jFrame.setLocation(x, y);
        
        frame.setVisible(true);
        return frame;
    }
    
    static Map<Table, TableFrame> tableFrameMap = new HashMap<>();
    
    public static final TableFrame retrieveTableFrame(Table table)
    {
        return tableFrameMap.get(table);
    }
    
    /**
     * The table displayed within this frame.
     */
    Table table;

    /**
     * Creates a new frame for displaying the content of the given DataTable.
     * 
     * The data table is indexed into the global table manager. Therefore the
     * name of the table may be modified to make sure it is unique within the
     * manager.
     * 
     * @param table
     */
    public TableFrame(Table table)
    {
        super("Data Table");

        this.table = table;
        tableFrameMap.put(table, this);
        
        // index the table into the manager
        String name = table.getName();
        TableManager manager = TableManager.getInstance();
        name = manager.createTableName(name);
        table.setName(name);
        manager.addTable(table);

        // Create the frame containing the table
        jFrame.setPreferredSize(new Dimension(400, 300));

        // setup layout and menus
        setupLayout();
        setupMenu();
        updateTitle();
        jFrame.pack();
        putFrameMiddleScreen();
        
        // setup listeners
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent evt)
            {
                close();
            }           
        });
    }

    private void setupLayout()
    {
        // Create JTable instance
        JTable jTable = computeJTable(table);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Setup layout
        Container panel = jFrame.getContentPane();

        JScrollPane scrollPane = new JScrollPane(jTable, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // decorate the scroll panel with row index
        JTable rowTable = new RowNumberTable(jTable);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        panel.add(jTable.getTableHeader(), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private static final JTable computeJTable(Table table)
    {
        // convert table to TableModel
        TableModel model = createModel(table);
        JTable jtable = new JTable(model);
        
        // some setup
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) jtable.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        jtable.getTableHeader().setPreferredSize(new Dimension(jtable.getColumnModel().getTotalColumnWidth(), 32));
        
        return jtable;
    }

    private void setupMenu()
    {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        addPlugin(fileMenu, ijt.table.gui.action.file.OpenTable.class, "", "Open Table");
        addPlugin(fileMenu, ijt.table.gui.action.file.OpenDemoTable.class, "", "Open Demo Table");
        fileMenu.addSeparator();
        addPlugin(fileMenu, ijt.table.gui.action.file.SaveAs.class, "", "Save As...");
        addPlugin(fileMenu, ijt.table.gui.action.file.ConvertToResultsTable.class, "", "Convert to ResultsTable");
        fileMenu.addSeparator();
        addPlugin(fileMenu, ijt.table.gui.action.file.Close.class, "", "Close");
        bar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        addPlugin(editMenu, ijt.table.gui.action.edit.DisplayInfo.class, "", "Display Info");
        addPlugin(editMenu, ijt.table.gui.action.edit.Rename.class, "", "Rename");
        editMenu.addSeparator();
        addPlugin(editMenu, ijt.table.gui.action.edit.SelectColumns.class, "", "Select Columns...");
        addPlugin(editMenu, ijt.table.gui.action.edit.MergeColumnsAction.class, "", "Merge Columns...");
        addPlugin(editMenu, ijt.table.gui.action.edit.ConvertNumericColumnToCategorical.class, "", "Convert Numeric Column to Categorical");
        addPlugin(editMenu, ijt.table.gui.action.edit.ConvertNumericColumnToInteger.class, "", "Convert Numeric Column to Integer");
        editMenu.addSeparator();
        addPlugin(editMenu, ijt.table.gui.action.edit.FilterRows.class, "", "Filter Rows...");
        addPlugin(editMenu, ijt.table.gui.action.edit.MergeRowsAction.class, "", "Merge Rows...");
        addPlugin(editMenu, ijt.table.gui.action.edit.ClearRowNames.class, "", "Clear Row Names");
        editMenu.addSeparator();
        addPlugin(editMenu, ijt.table.gui.action.edit.Summary.class, "", "Summary");
        bar.add(editMenu);

        JMenu plotMenu = new JMenu("Plot");
        addPlugin(plotMenu, ijt.table.gui.action.plot.LinePlot.class, "", "Line Plot (Column)");
        addPlugin(plotMenu, ijt.table.gui.action.plot.MultiLinePlot.class, "", "Multi Line Plot (Column)");
        addPlugin(plotMenu, ijt.table.gui.action.plot.ScatterPlot.class, "", "Scatter Plot");
        addPlugin(plotMenu, ijt.table.gui.action.plot.GroupScatterPlot.class, "", "Group Scatter Plot");
        plotMenu.addSeparator();
        addPlugin(plotMenu, ijt.table.gui.action.plot.PlotTableColumnHistogram.class, "", "Column Histogram");
        plotMenu.addSeparator();
        addPlugin(plotMenu, ijt.table.gui.action.plot.HeatMapDisplay.class, "", "Heat Map");
        bar.add(plotMenu);

        JMenu processMenu = new JMenu("Process");
        addPlugin(processMenu, ijt.table.gui.action.process.ApplyFunctionToColumn.class, "", "Data Transformation...");
        processMenu.addSeparator();
        addPlugin(processMenu, ijt.table.gui.action.process.CorrelationMatrix.class, "", "Correlation Matrix");
        addPlugin(processMenu, ijt.table.gui.action.process.PrincipalComponentAnalysisAction.class, "", "Principal Component Analysis");
        processMenu.addSeparator();
        addPlugin(processMenu, ijt.table.gui.action.process.CrossTableAction.class, "", "Cross-Table...");
        processMenu.addSeparator();
        addPlugin(processMenu, ijt.table.gui.action.process.KMeansAction.class, "", "K-Means");
        processMenu.addSeparator();
        addPlugin(processMenu, ijt.table.gui.action.plot.CreateLabelClassMap.class, "", "Create Label Image Class Map");
        bar.add(processMenu);
        
        JMenu helpMenu = new JMenu("Help");
        addPlugin(helpMenu, ijt.table.gui.action.help.About.class, "", "About...");
        bar.add(helpMenu);

        jFrame.setJMenuBar(bar);
    }

    private JMenuItem addPlugin(JMenu menu, Class<? extends FramePlugin> itemClass, String optionsString, String label)
    {
        // retrieve plugin
        FramePlugin plugin = PluginManager.getInstance().retrievePlugin(itemClass);
        if (plugin == null) return null;
        
        // setup menu item
        JMenuItem item = new JMenuItem(label);
//        item.setIcon(this.emptyIcon);
//        item.setMargin(new Insets(0, 0, 0, 0));
        item.addActionListener(new ActionRunner(this, plugin));
        item.setEnabled(plugin.isAvailable(this));
        menu.add(item);
        return item;
    }
    
    private void putFrameMiddleScreen()
    {
        // set up frame size depending on screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(600, screenSize.width - 100);
        int height = Math.min(400, screenSize.width - 100);
        Dimension frameSize = new Dimension(width, height);
        this.jFrame.setSize(frameSize);

        // set up frame position depending on frame size
        int posX = (screenSize.width - width) / 4;
        int posY = (screenSize.height - height) / 4;
        this.jFrame.setLocation(posX, posY);
    }


    // ===================================================================
    // General methods
    
    public Table getTable()
    {
        return table;
    }
    
    public JFrame getJFrame()
    {
        return this.jFrame;
    }
    
    public void repaint()
    {
        // recompute display of table
        JTable jTable = computeJTable(table);

        // Setup layout
        Container panel = jFrame.getContentPane();
        panel.removeAll();

        JScrollPane scrollPane = new JScrollPane(jTable);
        JTable rowTable = new RowNumberTable(jTable);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        panel.add(jTable.getTableHeader(), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.validate();
        
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
     * Close the frame and removes all dependencies.
     */
    public void close()
    {
        String tableName = table.getName();
        TableManager.getInstance().removeTable(tableName);
        tableFrameMap.remove(table);
        jFrame.dispose();
    }
    
    
    // ===================================================================
    // Utility methods

    private static final TableModel createModel(Table table)
    {
        // table size
        int nRows = table.rowCount();
        int nCols = table.columnCount();

        // If row names are specified, add another column as first column
        int columnOffset = table.hasRowNames() ? 1 : 0;
        int nCols2 = nCols + columnOffset;
        
        // set up name of row columns
        String[] colNames = new String[nCols2];
        if (table.hasRowNames())
        {
            colNames[0] = "Row Names";
            String rowNameLabel = table.getRowNameLabel();
            if (!isBlankString(rowNameLabel))
            {
                colNames[0] = rowNameLabel;
            }
        }
        
        for (int iCol = 0; iCol < table.columnCount(); iCol++)
        {
            String colName = table.getColumnName(iCol);
            colNames[iCol + columnOffset] = colName;
        }
        
        // Convert numeric values to table of objects
        Object[][] data = new Object[nRows][nCols2];
        for (int iRow = 0; iRow < nRows; iRow++)
        {
            Object[] row = data[iRow];
            
            if (table.hasRowNames())
            {
                row[0] = table.getRowName(iRow);
            }
            for (int iCol = 0; iCol < nCols; iCol++)
            {
                row[iCol + columnOffset] = table.get(iRow, iCol);
            }
            data[iRow] = row;
        };
        
        // create model
        return new DefaultTableModel(data, colNames);
    }
    
    private static final boolean isBlankString(String str)
    {
        if (str == null) return true;
        return str.trim().isEmpty();
    }

    
    /**
     * A simple main function to quickly test GUI on a toy example.
     */
    public static void main(String[] args)
    {
        // Create a basic data table
        Table tbl = Table.create(15, 5);
        tbl.setColumnNames(new String[] { "length", "area", "diameter", "number", "density" });
        
        // Create the frame to display the table.
        TableFrame frame = new TableFrame(tbl);
        frame.setVisible(true);
    }

}
