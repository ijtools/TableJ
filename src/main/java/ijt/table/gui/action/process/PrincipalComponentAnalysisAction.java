/**
 * 
 */
package ijt.table.gui.action.process;

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
    public void run(TableFrame parentFrame)
    {
        Table table = parentFrame.getTable();
        
        PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis().fit(table);
        
        // add new frames to the GUI
        TableFrame.create(pca.eigenValues());
        TableFrame.create(pca.loadings());
        TableFrame.create(pca.scores());
    }
}
