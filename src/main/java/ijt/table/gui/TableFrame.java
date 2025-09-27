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
import java.util.Locale;

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
import ijt.table.gui.action.edit.ClearRowNames;
import ijt.table.gui.action.edit.ConvertNumericColumnToCategorical;
import ijt.table.gui.action.edit.ConvertNumericColumnToInteger;
import ijt.table.gui.action.edit.DisplayInfo;
import ijt.table.gui.action.edit.FilterRows;
import ijt.table.gui.action.edit.MergeColumnsAction;
import ijt.table.gui.action.edit.MergeRowsAction;
import ijt.table.gui.action.edit.Rename;
import ijt.table.gui.action.edit.SelectColumns;
import ijt.table.gui.action.edit.Summary;
import ijt.table.gui.action.file.Close;
import ijt.table.gui.action.file.OpenDemoTable;
import ijt.table.gui.action.file.OpenTable;
import ijt.table.gui.action.file.SaveAs;
import ijt.table.gui.action.help.About;
import ijt.table.gui.action.plot.CreateLabelClassMap;
import ijt.table.gui.action.plot.GroupScatterPlot;
import ijt.table.gui.action.plot.HeatMapDisplay;
import ijt.table.gui.action.plot.LinePlot;
import ijt.table.gui.action.plot.MultiLinePlot;
import ijt.table.gui.action.plot.PlotTableColumnHistogram;
import ijt.table.gui.action.plot.ScatterPlot;
import ijt.table.gui.action.process.ApplyFunctionToColumn;
import ijt.table.gui.action.process.CorrelationMatrix;
import ijt.table.gui.action.process.CrossTableAction;
import ijt.table.gui.action.process.KMeansAction;
import ijt.table.gui.action.process.PrincipalComponentAnalysisAction;

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
                jFrame.dispose();
            }           
        });
    }

    private void setupLayout()
    {
        // Create JTable instance
        JTable jTable = computeJTable(table);

        // Setup layout
        Container panel = jFrame.getContentPane();

        JScrollPane scrollPane = new JScrollPane(jTable);
        JTable rowTable = new RowNumberTable(jTable);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        panel.add(jTable.getTableHeader(), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private static final JTable computeJTable(Table table)
    {
//        // Convert table data into necessary format
//        Object[][] data = convertTableToObjectArray(table);
//        String[] colNames = computeDisplayColNames(table);
//        
//        // Create JTable instance
//        JTable jTable = new JTable(data, colNames);
//        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        
//        return jTable;
//    }
//    private static final JTable createJTable(Table table)
//    {
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
        addMenuItem(fileMenu, "Open Table", new OpenTable());
        addMenuItem(fileMenu, "Open Demo Table", new OpenDemoTable());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Save As...", new SaveAs());
        addMenuItem(fileMenu, "Close", new Close());
        bar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        addMenuItem(editMenu, "Display Info", new DisplayInfo());
        addMenuItem(editMenu, "Rename", new Rename());
        editMenu.addSeparator();
        addMenuItem(editMenu, "Select Columns...", new SelectColumns());
        addMenuItem(editMenu, "Merge Columns...", new MergeColumnsAction());
        addMenuItem(editMenu, "Convert Numeric Column to Categorical", new ConvertNumericColumnToCategorical());
        addMenuItem(editMenu, "Convert Numeric Column to Integer", new ConvertNumericColumnToInteger());
        editMenu.addSeparator();
        addMenuItem(editMenu, "Filter Rows...", new FilterRows());
        addMenuItem(editMenu, "Merge Rows...", new MergeRowsAction());
        addMenuItem(editMenu, "Clear Row Names", new ClearRowNames());
        editMenu.addSeparator();
        addMenuItem(editMenu, "Summary", new Summary());
        bar.add(editMenu);

        JMenu plotMenu = new JMenu("Plot");
        addMenuItem(plotMenu, "Line Plot (Column)", new LinePlot());
        addMenuItem(plotMenu, "Multi Line Plot (Column)", new MultiLinePlot());
        addMenuItem(plotMenu, "Scatter Plot", new ScatterPlot());
        addMenuItem(plotMenu, "Group Scatter Plot", new GroupScatterPlot());
        plotMenu.addSeparator();
        addMenuItem(plotMenu, "Column Histogram", new PlotTableColumnHistogram());
        plotMenu.addSeparator();
        addMenuItem(plotMenu, "Heat Map", new HeatMapDisplay());
        plotMenu.addSeparator();
        addMenuItem(plotMenu, "Create Label Image Class Map", new CreateLabelClassMap());
        bar.add(plotMenu);

        JMenu processMenu = new JMenu("Process");
        addMenuItem(processMenu, "Data Transformation...", new ApplyFunctionToColumn());
        processMenu.addSeparator();
        addMenuItem(processMenu, "Correlation Matrix", new CorrelationMatrix());
        addMenuItem(processMenu, "Principal Component Analysis", new PrincipalComponentAnalysisAction());
        processMenu.addSeparator();
        addMenuItem(processMenu, "Cross-Table...", new CrossTableAction());
        processMenu.addSeparator();
        addMenuItem(processMenu, "K-Means", new KMeansAction());
        bar.add(processMenu);
        
        JMenu helpMenu = new JMenu("Help");
        addMenuItem(helpMenu, "About...", new About());
        bar.add(helpMenu);

        jFrame.setJMenuBar(bar);
    }

    private void addMenuItem(JMenu parent, String label, TableFrameAction action)
    {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(new ActionRunner(this, action));
        
        parent.add(item);
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
        return str.isBlank();
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
