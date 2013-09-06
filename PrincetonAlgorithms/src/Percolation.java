/*----------------------------------------------------------------
 *  Author:        Sathishkumar Kuppuswami
 *  Written:       02/09/2013
 *  Last updated:  02/09/2013
 *
 *  Implements Percolation API using Weighted Union Find API of Princeton
 *
 *  Compilation:   javac Percolation.java
 *  Execution:     java Percolation <<siteLength>>
 *  
 *  % java Percolation
 *  System percolated after opening xxx sites
 *----------------------------------------------------------------*/

public class Percolation {

    // Array storing Open/Closed state of every site in the system
    private char[] sites;
    // # of sites
    private int sitesCount;
    private WeightedQuickUnionUF weightedQUF;
    // Reference to index of virtual top site
    private int virtualTop;
    // Reference to index of virtual bottom site
    private int virtualBottom;
    // Length of the site
    private int siteLength;

    public Percolation(int n) {
        siteLength = n;
        sitesCount = n * n;
        sites = new char[sitesCount];

        for (int i = 0; i < sitesCount; i++) {
            sites[i] = 'C';
        }

        // Extra 2 cells to store virtual top and bottom nodes respectively
        weightedQUF = new WeightedQuickUnionUF(sitesCount + 2);

        // Value of virtual top site (400 in a 20 by 20 system)
        virtualTop = sitesCount;
        // Value of virtual top site (401 in a 20 by 20 system)
        virtualBottom = sitesCount + 1;
    }

    /**
     * Opens up the requested site and connects with the neighbors that are
     * already open
     * 
     * Throws a IndexOutOfBoundsException if given row/column is outside system
     * boundary.
     */
    public void open(int row, int column) {
        isWithinBoundary(row, column);
        int siteIndex = getIndex(row, column);
        if ((sites[siteIndex] == 'C')) {
            sites[siteIndex] = 'O';

            if (siteLength != 1) {
                if (row == 1) {
                    // If the site is a top site,
                    // connect it with virtual top site
                    weightedQUF.union(siteIndex, virtualTop);
                    sites[siteIndex] = 'F';
                }
                connectNeighbors(row, column);

                if ((row == siteLength)) {
                    // If the site is a bottom full site,
                    // connect with virtual bottom site
                    weightedQUF.union(siteIndex, virtualBottom);
                }
            } else {
                sites[siteIndex] = 'F';
                weightedQUF.union(siteIndex, virtualTop);
                weightedQUF.union(siteIndex, virtualBottom);
            }

        }
    }

    private void connectNeighbors(int row, int column) {
        connectWithLeftNeighbor(row, column);
        connectWithRightNeighbor(row, column);
        connectWithTopNeighbor(row, column);
        connectWithBottomNeighbor(row, column);
    }

    /**
     * Returns whether the system is percolated or not at any given point in
     * time
     * 
     */
    public boolean percolates() {
        return weightedQUF.connected(virtualTop, virtualBottom);
    }

    /**
     * Returns whether the given site is full or not.
     * 
     * Throws a IndexOutOfBoundsException if given row/column is outside system
     * boundary.
     */
    public boolean isFull(int row, int column) {
        isWithinBoundary(row, column);
        int siteIndex = getIndex(row, column);
        return sites[siteIndex] == 'F';
    }

    /**
     * Returns whether the given site is open or closed.
     * 
     * Throws a IndexOutOfBoundsException if given row/column is outside system
     * boundary.
     */
    public boolean isOpen(int row, int column) {
        isWithinBoundary(row, column);
        return sites[getIndex(row, column)] != 'C';
    }

    /**
     * Returns whether the given site is full or not.
     * 
     * Throws a IndexOutOfBoundsException if given row/column is outside system
     * boundary.
     */
    private boolean isWithinBoundary(int row, int column) {
        if (row < 1 || row > siteLength)
            throw new IndexOutOfBoundsException("row index " + row
                    + " must be between 1 and " + siteLength);

        if (column < 1 || column > siteLength) {
            throw new IndexOutOfBoundsException("row index " + column
                    + " must be between 1 and " + siteLength);
        }
        return true;
    }

    /**
     * Transforms given row,column pair to an index in single dimensional array
     * 
     */
    private int getIndex(int row, int column) {
        return ((row - 1) * siteLength) + (column - 1);
    }

    /**
     * Connects give row/column site with it's neighbor on the left
     * 
     */
    private void connectWithLeftNeighbor(int row, int column) {
        // Check that site is not the first column and left neighbor is open
        if (column != 1 && isOpen(row, column - 1)) {
            int siteIndex = getIndex(row, column);
            int neighborIndex = getIndex(row, column - 1);
            if (sites[neighborIndex] == 'F' && sites[siteIndex] != 'F') {
                weightedQUF.union(siteIndex, neighborIndex);
                sites[siteIndex] = 'F';
                connectWithTopNeighbor(row, column);
                connectWithRightNeighbor(row, column);
                connectWithBottomNeighbor(row, column);
            } else if (sites[siteIndex] == 'F' && sites[neighborIndex] != 'F') {
                weightedQUF.union(siteIndex, neighborIndex);
                sites[neighborIndex] = 'F';
                connectWithLeftNeighbor(row, column - 1);
                connectWithTopNeighbor(row, column - 1);
                connectWithBottomNeighbor(row, column - 1);
            }
        }
    }

    /**
     * Returns whether the given site is full or not.
     * 
     */
    private void connectWithRightNeighbor(int row, int column) {
        // Check that site is not the last column and right
        // neighbor is open
        if (column != siteLength && isOpen(row, column + 1)) {
            int siteIndex = getIndex(row, column);
            int neighborIndex = getIndex(row, column + 1);
            if (sites[neighborIndex] == 'F' && sites[siteIndex] != 'F') {
                weightedQUF.union(siteIndex, neighborIndex);
                sites[siteIndex] = 'F';
                connectWithLeftNeighbor(row, column);
                connectWithTopNeighbor(row, column);
                connectWithBottomNeighbor(row, column);
            } else if (sites[siteIndex] == 'F' && sites[neighborIndex] != 'F') {
                weightedQUF.union(siteIndex, neighborIndex);
                sites[neighborIndex] = 'F';
                connectWithTopNeighbor(row, column + 1);
                connectWithRightNeighbor(row, column + 1);
                connectWithBottomNeighbor(row, column + 1);
            }
        }
    }

    /**
     * Returns whether the given site is full or not.
     * 
     */
    private void connectWithTopNeighbor(int row, int column) {
        // Check that site is not the first row and top neighbor is open
        if (row != 1 && isOpen(row - 1, column)) {
            int siteIndex = getIndex(row, column);
            int neighborIndex = getIndex(row - 1, column);
            if (sites[neighborIndex] == 'F' && sites[siteIndex] != 'F') {
                weightedQUF.union(siteIndex, neighborIndex);
                sites[siteIndex] = 'F';
                connectWithLeftNeighbor(row, column);
                connectWithBottomNeighbor(row, column);
                connectWithRightNeighbor(row, column);
            } else if (sites[siteIndex] == 'F' && sites[neighborIndex] != 'F') {
                weightedQUF.union(siteIndex, neighborIndex);
                sites[neighborIndex] = 'F';
                connectWithLeftNeighbor(row - 1, column);
                connectWithRightNeighbor(row - 1, column);
                connectWithTopNeighbor(row - 1, column);
            }
        }
    }

    /**
     * Returns whether the given site is full or not.
     * 
     */
    private void connectWithBottomNeighbor(int row, int column) {
        // Check that given site is not the last row and bottom neighbor is open
        if (row != siteLength && isOpen(row + 1, column)) {
            int siteIndex = getIndex(row, column);
            int neighborIndex = getIndex(row + 1, column);
            if (sites[neighborIndex] == 'F' && sites[siteIndex] != 'F') {
                weightedQUF.union(siteIndex, neighborIndex);
                sites[siteIndex] = 'F';
                connectWithLeftNeighbor(row, column);
                connectWithRightNeighbor(row, column);
                connectWithTopNeighbor(row, column);
            } else if (sites[siteIndex] == 'F' && sites[neighborIndex] != 'F') {
                weightedQUF.union(siteIndex, neighborIndex);
                sites[neighborIndex] = 'F';
                connectWithLeftNeighbor(row + 1, column);
                connectWithRightNeighbor(row + 1, column);
                connectWithBottomNeighbor(row + 1, column);
            }
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]); // input file
        int N = in.readInt(); // N-by-N percolation system

        // repeatedly read in sites to open and draw resulting system
        Percolation perc = new Percolation(N);
        while (!in.isEmpty()) {
            int i = in.readInt();
            int j = in.readInt();
            perc.open(i, j);
        }

    }
}
