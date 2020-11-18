/**
 * 
 */
package ijt.table.process;

import ijt.table.DataTable;
import ijt.table.NumericColumn;

/**
 * Compute the covariance matrix of a numeric table, and returns the result in a
 * new symmetric numeric table.
 * 
 * @author dlegland
 *
 */
public class CovarianceMatrix
{
    /**
     * 
     */
    public CovarianceMatrix()
    {
    }

    public DataTable process(DataTable table)
    {
        int nc = table.columnNumber();
        int nr = table.rowNumber();

        // compute the average value within each column
        double[] means = new double[nc];
        for (int c= 0; c < nc; c++)
        {
            double cumsum = 0;
            NumericColumn col = (NumericColumn) table.getColumn(c);
            for (double v : col)
            {
                cumsum += v;
            }
            means[c] = cumsum / nr;
        }
        
        DataTable covMat = DataTable.create(nc, nc);
        
        for (int i = 0; i < nc; i++)
        {
            NumericColumn colI = (NumericColumn) table.getColumn(i);

            // compute variance of column i
            double var = 0;
            for (int k = 0; k < nr; k++)
            {
                double v1 = colI.getValue(k) - means[i];
                var += v1 * v1;
            }
            covMat.setValue(i, i, var / (nr - 1));

            for (int j = i + 1; j < nc; j++)
            {
                NumericColumn colJ = (NumericColumn) table.getColumn(j);
                
                // compute covariance of columns i and j
                double cov = 0;
                for (int k = 0; k < nr; k++)
                {
                    double v1 = colI.getValue(k) - means[i];
                    double v2 = colJ.getValue(k) - means[j];
                    cov += v1 * v2;
                }
                cov /= (nr - 1);
                covMat.setValue(i, j, cov);
                covMat.setValue(j, i, cov);
            }
        }
        
        // populate set up meta-data
        covMat.setColumnNames(table.getColumnNames());
        covMat.setRowNames(table.getColumnNames());
        
        return covMat;
    }  
}
