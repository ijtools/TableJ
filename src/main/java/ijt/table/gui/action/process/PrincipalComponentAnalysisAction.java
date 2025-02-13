/**
 * 
 */
package ijt.table.gui.action.process;

import ijt.table.Column;
import ijt.table.NumericColumn;
import ijt.table.Table;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;
import ijt.table.transform.PrincipalComponentAnalysis;


/**
 * Performs principal components analysis of the current table. Table must
 * contains only numerical columns.
 * 
 * Displays the results into separate tables.
 * 
 * @author dlegland
 */
//TODO: Provides options to select 1) scaled or non-scaled analysis, 2) number of components to keep.
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
        
        // Compute result of principal component analysis
        PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis().fit(table);
        
        // add new frames to the GUI
        TableFrame.create(pca.eigenValues(), frame);
        TableFrame.create(pca.loadings(), frame);
        TableFrame.create(pca.scores(), frame);
    }
}
