/**
 * 
 */
package ijt.table.process;

import java.util.Arrays;

import ijt.table.NumericColumn;

/**
 * @author dlegland
 *
 */
public class SummaryStatistics
{
    public static final double max(NumericColumn col)
    {
        double maxVal = Double.NEGATIVE_INFINITY;
        for (double value : col)
        {
            maxVal = Math.max(maxVal, value);
        }
        return maxVal;
    }

    public static final double mean(NumericColumn col)
    {
        return sum(col) / col.size();
    }

    public static final double median(NumericColumn col)
    {
        int n = col.size();
        double[] values = new double[n];
        System.arraycopy(col.getValues(), 0, values, 0, n);
        Arrays.parallelSort(values);
        if (n % 2 == 1)
        {
            return values[(n - 1) / 2];
        }
        else
        {
            int med = n / 2;
            return (values[med] + values[med + 1]) * 0.5;
        }
    }

    public static final double min(NumericColumn col)
    {
        double minVal = Double.POSITIVE_INFINITY;
        for (double value : col)
        {
            minVal = Math.min(minVal, value);
        }
        return minVal;
    }

    public static final double std(NumericColumn col)
    {
        double mean = mean(col);
        double sumSq = 0.0;
        for (double value : col)
        {
            double vc = value - mean;
            sumSq += vc * vc;
        }
        return Math.sqrt(sumSq / (col.size() - 1.0));        
    }

    public static final double sum(NumericColumn col)
    {
        double sum = 0.0;
        for (double value : col)
        {
            sum += value;
        }
        return sum;
    }

    public static final double var(NumericColumn col)
    {
        double mean = mean(col);
        double sumSq = 0.0;
        for (double value : col)
        {
            double vc = value - mean;
            sumSq += vc * vc;
        }
        return sumSq / col.size();        
    }

}
