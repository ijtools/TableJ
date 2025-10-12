/**
 * 
 */
package ijt.table.cluster;

import java.util.Random;

import ijt.table.CategoricalColumn;
import ijt.table.Table;

/**
 * KMeans algorithm, used to cluster individuals by minimizing intra class
 * variance.
 * 
 * @author dlegland
 *
 */
public class KMeans
{
    // =============================================================
    // Settings for computing KMeans
    
    /**
     * Number of classes / clusters to create.
     */
	int nClasses;

    /**
     * Number of iterations used to fit class centroids. Default is 10.
     */
    int nIters= 10;
    

    // =============================================================
    // Inner data
    
    /**
     * The centroid of each class. Stored as a k-by-p array, where k is the
     * number of classes and p is the number of features.
     */
    double[][] centroids = null;
    
    /**
     * The names of features of original table.
     */
    String[] featureNames = null;
    
    /**
     * The name of the table used to fit the centroids (for building table names).
     */
    String baseName;


    // =============================================================
    // Constructor
    
	/**
	 * Creates a new KMeans classifier with a given number of classes.
     * 
     * @param nClasses
     *            the number of classes
     */
	public KMeans(int nClasses)
	{
		this.nClasses = nClasses;
	}


    // =============================================================
    // New methods specific to KMeans
    
    /**
     * @return a table instance containing coordinates of class centroids.
     */
    public Table centroids()
    {
        // check centroids are initialized
        if (centroids == null)
        {
            throw new RuntimeException("KMeans class must be initialized using the fit(...) method.");
        }
        
        // get table dimensions
        int nc = this.nClasses;
        int nf = this.centroids[0].length;
        
        // pattern for row names
        int nDigits = (int) (Math.floor(Math.log10(nc)) + 1);
        String pattern = "Class%0" + nDigits + "d";

        // create table
        Table res = Table.create(nc, nf);
        res.setColumnNames(this.featureNames);
        res.setName(baseName + "-KM" + this.nClasses + "_centroids");
        
        // fill with centroid coordinates
        String[] rowNames = new String[nc];
        for (int c = 0; c < nc; c++)
        {
            for (int f = 0; f < nf; f++)
            {
                res.setValue(c, f, this.centroids[c][f]);
            }

            // annotate table
            rowNames[c] = String.format(pattern, c);
        }
        res.setRowNames(rowNames);
        
        return res;
    }

    
	/***
	 * Initializes this KMeans based on the input table.
	 * 
	 * @param table
	 *            the table used for initialization
	 * @return a reference to this KMeans instance
	 */
	public KMeans fit(Table table)
	{
	    // get table dimensions
	    int nr = table.rowCount();
	    int np = table.columnCount();

	    // small checkups
	    if (!table.isNumeric())
	    {
            throw new IllegalArgumentException("Requires a table with numeric columns only");
	    }
	    if (nr < np)
	    {
	        throw new IllegalArgumentException("Requires a table with at least as many rows as columns");
	    }
	    
	    // allocated the centroid array: nClasses * nParams array
	    this.centroids = new double[nClasses][np];
	    this.featureNames = table.getColumnNames();
	    this.baseName = table.getName();

	    int[] indices = KMeans.randomSubsetIndices(nr, nClasses);

	    // compute initial centroids
	    for (int k = 0; k < nClasses; k++)
	    {
	        for (int p = 0; p < np; p++)
	        {
	            this.centroids[k][p] = table.getValue(indices[k], p);
	        }
	    }

	    // performs initial allocation
	    int[] classes = findClassIndices(table, this.centroids, new int[nr]);

	    // Performs several iterations (count initialization as first iteration)
	    for (int iIter = 1; iIter < nIters; iIter++)
	    {
	        centroids = computeClassCentroids(table, classes, nClasses);
	        classes = findClassIndices(table, this.centroids, classes);
	    }

	    return this;
	}
	
    /**
     * Chooses <it>k</it> indices between 0 and <it>n-1</it> without repetition.
     * 
     * @param n
     *            the number possible indices
     * @param k
     *            the number of indices in the subset
     * @return an array with <it>k</it> integer values between 0 and
     *         <it>n-1</it>
     */
    private static final int[] randomSubsetIndices(int n, int k)
    {
        // choose random individuals
        Random rand = new Random();
        int[] indices = new int[k];
        for (int i = 0; i < k; i++)
        {
            // check index is not already chosen
            do
            {
                indices[i] = rand.nextInt(n);
            } 
            while (containsIndex(indices, i, indices[i]));
        }
        return indices;
    }
    
    private static final boolean containsIndex(int[] indices, int n, int index)
    {
        for (int i = 0; i < n; i++)
        {
            if (indices[i] == index)
            {
                return true;
            }
        }
        return false;
    }

	/**
	 * Identifies class index of each observation in input table, based on this
	 * (initialized) KMeans.
	 * 
	 * @param table
	 *            the table to predict
	 * @return a table containing the class index of each row in original table
	 */
	public Table predict(Table table)
	{
	    // get table dimension
	    int nr = table.rowCount();
	    int nc = table.columnCount();
	    
        // KMeans class must have been initialized
	    if (this.centroids.length == 0)
	    {
	        throw new RuntimeException("KMeans class must have been initialized");
	    }
        // check consistency of column dimension
	    if (nc != this.centroids[0].length)
	    {
            throw new IllegalArgumentException(
                    String.format(
                            "Number of columns of table (%d) does not match centroids dimension (%d)",
                            nc, this.centroids.length));
	    }
	    
        // Create array of level names
        int ng = this.centroids.length;
        String[] classNames = new String[ng];
        for (int g = 0; g < ng; g++)
        {
            classNames[g] = "class" + (g+1);
        }
        
        // Create result Column
        int[] classIndices = findClassIndices(table, this.centroids, new int[nr]);
        CategoricalColumn classColumn = CategoricalColumn.create("Class", classIndices, classNames);
                
        // Create result table
        Table res = Table.create(classColumn);
        res.setName(table.getName() + "-KM" + this.nClasses + "_predict");

        // keep row axis meta-data
        if (table.hasRowNames())
        {
            res.setRowNames(table.getRowNames());
            res.setRowNameLabel(table.getRowNameLabel());
        }
        
        return res;
	}
	
    private int[] findClassIndices(Table table, double[][] centroids, int[] classes)
    {
        // allocate memory for result
        int nr = table.rowCount();
        if (classes == null)
        {
            classes = new int[nr];
        }
        
        // iterate over individual rows
        for (int i = 0; i < nr; i++)
        {
            double[] row = table.getRowValues(i);
            classes[i] = findClosestPoint(row, centroids);
        }
        
        // return allocated classes
        return classes;
    }

    private static final int findClosestPoint(double[] point, double[][] points)
	{
		int index = -1;
		double minDist = Double.POSITIVE_INFINITY;
		for (int iClass = 0; iClass < points.length; iClass++)
		{
			double dist = distanceSq(point, points[iClass]);
			if (dist < minDist)
			{
				index = iClass;
				minDist = dist;
			}
		}
		return index;
	}
	
	private static final double distanceSq(double[] p1, double[] p2)
	{
		double distSq = 0;
		for (int i = 0; i < p1.length; i++)
		{
			double dp = p2[i] - p1[i];
			distSq += dp * dp;
		}
		return distSq;
	}
	
	private static final double[][] computeClassCentroids(Table table, int[] classes, int nClasses)
	{
		int nr = table.rowCount();
		int np = table.columnCount();
		
		double[][] cumsum = new double[nClasses][np];
		int[] counts = new int[nClasses];
		
		// update cumulated sum and count
		for (int i = 0; i < nr; i++)
		{
			int index = classes[i];
			for (int p = 0; p < np; p++)
			{
				cumsum[index][p] += table.getValue(i, p);
			}
			counts[index]++;
		}
		
		// divide cumulative sums by counts to obtain centroids
		for (int i = 0; i < nClasses; i++)
		{
			int count = counts[i];
			for (int p = 0; p < np; p++)
			{
				cumsum[i][p] /= count;
			}
		}
		
		return cumsum;
	}

	
	// =============================================================
    // Implementation of TableOperator interface
    
    public Table process(Table table)
    {
        return this.fit(table).predict(table);
    }
}
