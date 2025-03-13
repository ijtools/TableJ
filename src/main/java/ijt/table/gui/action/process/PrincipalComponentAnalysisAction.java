/**
 * 
 */
package ijt.table.gui.action.process;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableModel;

import org.knowm.xchart.AnnotationText;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.XYStyler;

import ij.gui.GenericDialog;
import ijt.table.Column;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.RowNumberTable;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.gui.action.file.OpenDemoTable;
import ijt.table.gui.frame.ChartFrame;
import ijt.table.transform.PrincipalComponentAnalysis;


/**
 * Performs principal components analysis of the current table. Table must
 * contains only numerical columns.
 * 
 * Displays the results into separate tables.
 * 
 * @author dlegland
 */
public class PrincipalComponentAnalysisAction implements TableFrameAction
{
    /* (non-Javadoc)
     * @see imago.gui.Plugin#run(imago.gui.ImagoFrame, java.lang.String)
     */
    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();
        // Check all columns are numeric
        for (Column column : table.columns())
        {
            if (!(column instanceof NumericColumn))
            {
                throw new IllegalArgumentException("Requires table with numeric columns only");
            }
        }
        
        new PcaDialog(frame, "Principal Components Analysis", table);
    }

    /**
     * The dialog that display scaling option, the table of eigen values, and a
     * menu for displaying selection of plots.
     */
    private static class PcaDialog
    {
        
        TableFrame refFrame;
        Table table;
        
        int maxComponents;

        // The result of PCA
        PrincipalComponentAnalysis pca;
        
        // widgets
        JDialog dlg;

        JCheckBox scaleCheckBox = new JCheckBox("Scale Variables", true);
        JSpinner maxComponentSpinner = null;
        JButton computeButton = new JButton("Compute");
        JTable eigenValuesTable = null;

        
        public PcaDialog(TableFrame refFrame, String dlgTitle, Table refTable)
        {
            this.dlg = new JDialog(refFrame.getJFrame(), dlgTitle, false);
            this.refFrame = refFrame;
            this.table = refTable;
            
            this.maxComponents = table.columnCount();
            
            // setup GUI
            setupMenu();
            createWidgets();
            setupLayout();
            this.dlg.setVisible(true);
        }
        
        private void setupMenu()
        {
            JMenuBar bar = new JMenuBar();

            JMenu fileMenu = new JMenu("File");
            addMenuItem(fileMenu, "Close", evt -> dlg.dispose());
            bar.add(fileMenu);

            JMenu tablesMenu = new JMenu("Tables");
            addMenuItem(tablesMenu, "Show Eigen Values Table", evt -> {
                if (this.pca != null) 
                    TableFrame.create(this.pca.eigenValues(), refFrame);
            });
            addMenuItem(tablesMenu, "Show Scores Table", evt -> {
                if (this.pca != null) 
                    TableFrame.create(this.pca.scores(), refFrame);
            });
            addMenuItem(tablesMenu, "Show Loadings Table", evt -> {
                if (this.pca != null) 
                    TableFrame.create(this.pca.loadings(), refFrame);
            });
            tablesMenu.addSeparator();
            addMenuItem(tablesMenu, "Show Normalisation Data", evt -> {
                if (this.pca != null) 
                    TableFrame.create(this.pca.normalisationData(), refFrame);
            });
            bar.add(tablesMenu);
            
            JMenu plotMenu = new JMenu("Plot");
            addMenuItem(plotMenu, "Scree Plot", evt -> onDisplayScreePlot());
            plotMenu.addSeparator();
            addMenuItem(plotMenu, "Score Plot...", evt -> onDisplayScorePlot());
            addMenuItem(plotMenu, "Loadings Plot...", evt -> onDisplayLoadingsPlot());
            addMenuItem(plotMenu, "Correlation Circle...", evt -> onDisplayCorrelationCircle());
            bar.add(plotMenu);

            dlg.setJMenuBar(bar);
        }
        
        private JMenuItem addMenuItem(JMenu parent, String label, ActionListener listener)
        {
            JMenuItem item = new JMenuItem(label);
            item.addActionListener(listener);
            parent.add(item);
            return item;
        }

        private void createWidgets()
        {
            int nCols = table.columnCount();
            maxComponentSpinner = new JSpinner(new SpinnerNumberModel(Math.min(nCols, 10), 2, nCols, 1));

            this.computeButton = new JButton("Compute");
            this.computeButton.addActionListener(evt -> onComputeButton());
            String[] colNames = new String[] { "Eigen Value", "Inertia (%)", "Cumulated Inertia" };
            this.eigenValuesTable = new JTable(new Double[nCols][3], colNames);
            this.eigenValuesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        private void setupLayout()
        {
            JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.BASELINE_LEADING;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            int row = 0;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            mainPanel.add(new JLabel(" "), gbc);
            
            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            mainPanel.add(this.scaleCheckBox, gbc);
   
            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            mainPanel.add(new JLabel("Max. Components:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = row;
            mainPanel.add(this.maxComponentSpinner, gbc);
   
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
            mainPanel.add(computeButton, gbc);
            
            // on the right: display table of eigen values
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JScrollPane scrollPane = new JScrollPane(this.eigenValuesTable);
            JTable rowTable = new RowNumberTable(this.eigenValuesTable);
            scrollPane.setRowHeaderView(rowTable);
            scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
            scrollPane.setPreferredSize(new Dimension(200, 200));

            rightPanel.add(eigenValuesTable.getTableHeader(), BorderLayout.NORTH);
            rightPanel.add(scrollPane, BorderLayout.CENTER);
            
            row = 0;
            gbc.gridx = 2;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.gridheight = 6;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            mainPanel.add(rightPanel, gbc);
            
            dlg.setContentPane(mainPanel);
            dlg.setMinimumSize(new Dimension(200, 200));
            dlg.setLocationRelativeTo(refFrame.getJFrame());
            dlg.pack();
        }

        private void onComputeButton()
        {
            // retrieve chosen options
            boolean scale = this.scaleCheckBox.isSelected();
            this.maxComponents = ((SpinnerNumberModel) this.maxComponentSpinner.getModel()).getNumber().intValue();

            // recompute PCA
            this.pca = new PrincipalComponentAnalysis(scale).fit(table);

            // update JTable
            Table eigenValues = this.pca.eigenValues();
            TableModel model = this.eigenValuesTable.getModel();
            for (int r = 0; r < eigenValues.rowCount(); r++)
            {
                for (int c = 0; c < 3; c++)
                {
                    model.setValueAt(Double.valueOf(eigenValues.getValue(r, c)), r, c);
                }
            }
        }

        private void onDisplayScreePlot()
        {
            Table eigenValues = pca.eigenValues();

            int nc = this.maxComponents;
            String[] compNames = new String[nc];
            String[] colNames = eigenValues.getRowNames();
            for (int c = 0; c < nc; c++)
                compNames[c] = colNames[c];

            // Create the Chart
            CategoryChart chart = new CategoryChartBuilder()
                    .width(600)
                    .height(500)
                    .xAxisTitle("Principal Components")
                    .build();

            // Additional chart style
            chart.getStyler()
                    .setLegendVisible(true)
                    .setLegendPosition(LegendPosition.InsideNE);
            
            // Initialize XY series
            double[] xInertia = new double[nc];
            double[] inertia = new double[nc];
            for (int i = 0; i < nc; i++)
            {
                xInertia[i] = (i + 1);
                inertia[i] = eigenValues.getValue(i, 1) * 100;
            }
            
            chart.addSeries("Inertia (%)", xInertia, inertia);

            ChartFrame.create(chart, "Scree Plot", this.refFrame);
        }

        private void onDisplayScorePlot()
        {
            Table scores = pca.scores();

            int nc = this.maxComponents;
            String[] compNames = new String[nc];
            String[] colNames = scores.getColumnNames();
            for (int c = 0; c < nc; c++)
                compNames[c] = colNames[c];

            GenericDialog gd = new GenericDialog("Score Plot");
            gd.addChoice("X-Axis", compNames, compNames[0]);
            gd.addChoice("Y-Axis", compNames, compNames[1]);
            gd.addNumericField("Marker_Size", 4, 0);
            gd.addCheckbox("Display Row Names", false);

            gd.showDialog();
            if (gd.wasCanceled())
            { return; }
            int xAxisIndex = gd.getNextChoiceIndex();
            int yAxisIndex = gd.getNextChoiceIndex();
            int markerSize = (int) gd.getNextNumber();
            boolean displayRowNames = gd.getNextBoolean();

            // Create the Chart
            XYChart chart = new XYChartBuilder()
                    .width(600)
                    .height(500)
                    .xAxisTitle(createComponentAxisLabel(xAxisIndex))
                    .yAxisTitle(createComponentAxisLabel(yAxisIndex))
                    .build();

            // Additional chart style
            XYStyler styler = chart.getStyler();
            styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter)
                    .setLegendVisible(false)
                    .setMarkerSize(markerSize);
            Font font = styler.getAnnotationTextFont();
            styler.setAnnotationTextFont(font.deriveFont(12f));
            
            // Initialize XY series
            double[] xData = scores.getColumnValues(xAxisIndex);
            double[] yData = scores.getColumnValues(yAxisIndex);
            chart.addSeries(table.getName(), xData, yData);

            // Optional display of labels
            if (displayRowNames)
            {
                // compute slight shift of each label
                double[] yDataRange = ((NumericColumn) scores.getColumn(yAxisIndex)).valueRange(); 
                double dy = (yDataRange[1] - yDataRange[0]) * 0.02;
                String[] labels = scores.getRowNames();
                if (labels == null || labels.length == 0) labels = computeDefaultNames(scores.rowCount());
                for (int i = 0; i < xData.length; i++)
                {
                    chart.addAnnotation(new AnnotationText(labels[i], xData[i], yData[i] + dy, false));
                }
            }

            ChartFrame.create(chart, "Score Plot", this.refFrame);
        }

        private void onDisplayLoadingsPlot()
        {
            Table loadings = pca.loadings();

            int nc = this.maxComponents;
            String[] compNames = new String[nc];
            String[] colNames = loadings.getColumnNames();
            for (int c = 0; c < nc; c++)
                compNames[c] = colNames[c];

            GenericDialog gd = new GenericDialog("Loadings Plot");
            gd.addChoice("X-Axis", compNames, compNames[0]);
            gd.addChoice("Y-Axis", compNames, compNames[1]);
            gd.addNumericField("Marker_Size", 2, 0);
            gd.addCheckbox("Display Feature Names", false);

            gd.showDialog();
            if (gd.wasCanceled())
            { return; }
            int xAxisIndex = gd.getNextChoiceIndex();
            int yAxisIndex = gd.getNextChoiceIndex();
            int markerSize = (int) gd.getNextNumber();
            boolean displayRowNames = gd.getNextBoolean();

            // Create the Chart
            XYChart chart = new XYChartBuilder()
                    .width(600)
                    .height(500)
                    .xAxisTitle(createComponentAxisLabel(xAxisIndex))
                    .yAxisTitle(createComponentAxisLabel(yAxisIndex))
                    .build();

            // Additional chart style
            XYStyler styler = chart.getStyler();
            styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter)
                    .setLegendVisible(false)
                    .setMarkerSize(markerSize);
            Font font = styler.getAnnotationTextFont();
            styler.setAnnotationTextFont(font.deriveFont(12f));
            
            // Initialize XY series
            double[] xData = loadings.getColumnValues(xAxisIndex);
            double[] yData = loadings.getColumnValues(yAxisIndex);
            chart.addSeries(table.getName(), xData, yData);

            // Optional display of labels
            if (displayRowNames)
            {
                // compute slight shift of each label
                double[] yDataRange = ((NumericColumn) loadings.getColumn(yAxisIndex)).valueRange(); 
                double dy = (yDataRange[1] - yDataRange[0]) * 0.02;
                String[] labels = loadings.getRowNames();
                if (labels == null || labels.length == 0) labels = computeDefaultNames(loadings.rowCount());
                for (int i = 0; i < xData.length; i++)
                {
                    chart.addAnnotation(new AnnotationText(labels[i], xData[i], yData[i] + dy, false));
                }
            }

            ChartFrame.create(chart, "Loadings Plot", this.refFrame);
        }

        private void onDisplayCorrelationCircle()
        {
            Table loadings = pca.loadings();

            int nc = this.maxComponents;
            String[] compNames = new String[nc];
            String[] colNames = loadings.getColumnNames();
            for (int c = 0; c < nc; c++)
                compNames[c] = colNames[c];

            GenericDialog gd = new GenericDialog("Correlation Circle");
            gd.addChoice("X-Axis", compNames, compNames[0]);
            gd.addChoice("Y-Axis", compNames, compNames[1]);
            gd.addNumericField("Marker_Size", 2, 0);

            gd.showDialog();
            if (gd.wasCanceled())
            { return; }
            int xAxisIndex = gd.getNextChoiceIndex();
            int yAxisIndex = gd.getNextChoiceIndex();
            int markerSize = (int) gd.getNextNumber();
           
            // Create the Chart
            XYChart chart = new XYChartBuilder()
                    .width(600)
                    .height(500)
                    .xAxisTitle(createComponentAxisLabel(xAxisIndex))
                    .yAxisTitle(createComponentAxisLabel(yAxisIndex))
                    .build();

            // Additional chart style
            XYStyler styler = chart.getStyler();
            styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter)
                    .setLegendVisible(false)
                    .setMarkerSize(markerSize);
            Font font = styler.getAnnotationTextFont();
            styler.setAnnotationTextFont(font.deriveFont(Font.BOLD, 14f));
            
            // Initialize XY series
            double[] xData = loadings.getColumnValues(xAxisIndex);
            double[] yData = loadings.getColumnValues(yAxisIndex);
            // normalizes each column by corresponding eigen value
            double evx = Math.sqrt(pca.eigenValues().getValue(xAxisIndex, 0));
            double evy = Math.sqrt(pca.eigenValues().getValue(yAxisIndex, 0));
            for (int i = 0; i < xData.length; i++)
            {
                xData[i] *= evx;
                yData[i] *= evy;
            }
            chart.addSeries("Data", xData, yData);
            
            int nPts = 200;
            double[] xCircle = new double[nPts+1];
            double[] yCircle = new double[nPts+1];
            for(int i = 0; i < nPts; i++)
            {
                xCircle[i] = Math.cos((i * Math.PI * 2.0) / nPts);
                yCircle[i] = Math.sin((i * Math.PI * 2.0) / nPts);
            }
            xCircle[nPts] = 1.0;
            XYSeries circle = chart.addSeries("Circle", xCircle, yCircle);
            circle.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
            circle.setLineColor(Color.BLACK).setLineStyle(new BasicStroke(1f));
            XYSeries xLine = chart.addSeries("XLine", new double[] {-1, +1}, new double[] {0, 0});
            xLine.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
            xLine.setLineColor(Color.BLACK).setLineStyle(new BasicStroke(1f));
            XYSeries yLine = chart.addSeries("YLine", new double[] {0, 0}, new double[] {-1, +1});
            yLine.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
            yLine.setLineColor(Color.BLACK).setLineStyle(new BasicStroke(1f));
            
            // compute slight shift of each label
            double dy = 0.02;
            String[] labels = loadings.getRowNames();
            if (labels == null || labels.length == 0) labels = computeDefaultNames(loadings.rowCount());
            for (int i = 0; i < xData.length; i++)
            {
                chart.addAnnotation(new AnnotationText(labels[i], xData[i], yData[i] + dy, false));
            }

            ChartFrame.create(chart, "Correlation Circle", this.refFrame);
        }

        private static final String[] computeDefaultNames(int n)
        {
            String[] res = new String[n];
            for (int i = 0; i < n; i++)
            {
                res[i] = "" + i;
            }
            return res;
        }

        private String createComponentAxisLabel(int index)
        {
            double inertia = pca.eigenValues().getValue(index, 1);
            return String.format(Locale.ENGLISH, "Principal Component %d (%5.2f%%)", index + 1, inertia * 100);
        }
    }

    public static void main(String... args) throws IOException
    {
        InputStream inputStream = OpenDemoTable.class.getResourceAsStream("/tables/fisherIris.txt");
        Table table = new ijt.table.io.DelimitedTableReader().readTable(inputStream);
        table.setName("fisherIris");

        Table numTable = Table.create(table.getColumn(0), table.getColumn(1), table.getColumn(2), table.getColumn(3));

        new PcaDialog(new TableFrame(numTable), "Principal Component Analysis", numTable);
    }
}
