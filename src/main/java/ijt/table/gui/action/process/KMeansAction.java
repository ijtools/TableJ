/**
 * 
 */
package ijt.table.gui.action.process;

import ij.gui.GenericDialog;
import ijt.table.Table;
import ijt.table.cluster.KMeans;
import ijt.table.gui.TableFrame;
import ijt.table.gui.TableFrameAction;

/**
 * Applies k-means algorithms to the observations represented by rows of the
 * current data table.
 * 
 * @author dlegland
 *
 */
public class KMeansAction implements TableFrameAction
{
    @Override
    public void run(TableFrame frame)
    {
        Table table = frame.getTable();

        GenericDialog gd = new GenericDialog("KMeans");
        gd.addNumericField("Cluster number", 3, 0);
        gd.addCheckbox("Display Centroids", false);
        
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        
        int nClusters = (int) gd.getNextNumber();
        boolean showCentroids = gd.getNextBoolean();

        // Default name for table
        String tableName = table.getName();
        if (tableName == null || tableName.length() == 0)
        {
            tableName = "Data";
        }
        
        // compute class centroids using k-means
        KMeans kmeans = new KMeans(nClusters);
        kmeans.fit(table);
        
        // associate original data points to classes
        Table clusterIndices = kmeans.predict(table);
        TableFrame clusterFrame = TableFrame.create(clusterIndices, frame);
        
        if (showCentroids)
        {
            Table centroids = kmeans.centroids(); 
            TableFrame.create(centroids, clusterFrame);
        }
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }

}
