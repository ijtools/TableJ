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
 * @author dlegland
 *
 */
public class KMeansAction implements TableFrameAction
{
    @Override
    public void run(TableFrame parentFrame)
    {
        Table table = parentFrame.getTable();

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
        
        KMeans kmeans = new KMeans(nClusters);
        kmeans.fit(table);
        
        Table clusterIndices = kmeans.predict(table);
        TableFrame clusterFrame = parentFrame.createNewTableFrame(clusterIndices);
        
        if (showCentroids)
        {
            Table centroids = kmeans.centroids(); 
            clusterFrame.createNewTableFrame(centroids);
        }
    }
    
    public boolean isAvailable(TableFrame frame)
    {
        return frame.getTable() != null;
    }

}
