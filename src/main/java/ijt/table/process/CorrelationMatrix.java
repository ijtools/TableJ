/**
 * 
 */
package ijt.table.process;

import ijt.table.DataTable;

/**
 * Compute the correlation matrix of a numeric table, and returns the result in a
 * new symmetric numeric table.
 * 
 * @author dlegland
 *
 */
public class CorrelationMatrix 
{
    /**
     * 
     */
    public CorrelationMatrix()
    {
    }

    
    public DataTable process(DataTable table)
    {
        DataTable covMat = new CovarianceMatrix().process(table);
        
        int nc = table.columnCount();

        DataTable corrMat = DataTable.create(nc, nc);
        
        for (int i = 0; i < nc; i++)
        {
            corrMat.setValue(i, i, 1.0);
            
            for (int j = i+1; j < nc; j++)
            {
                double cij = covMat.getValue(i, j);
                double ci = covMat.getValue(i, i);
                double cj = covMat.getValue(j, j);
                double corr = cij / Math.sqrt(ci * cj);
                corrMat.setValue(i, j, corr);
                corrMat.setValue(j, i, corr);
            }
        }
        
        // populate set up meta-data
        corrMat.setColumnNames(table.getColumnNames());
        corrMat.setRowNames(table.getColumnNames());
        
        return corrMat;
    }  
}
