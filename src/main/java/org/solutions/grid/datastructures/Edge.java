package org.solutions.grid.datastructures;

/* Class of the edge in the graph represented by adjacency list
 representing path between two locations. */
public class Edge {
    /**
     * Destination node/location.
     */
    private int toId;
    /**
     * Sum of all accumulated edges.
     */
    private long distancesSum;
    /**
     * Number of all accumulated edges.
     */
    private int edgesCounter;

    public Edge(int toId, long distancesSum, int edgesCounter) {
        this.toId = toId;
        this.distancesSum = distancesSum;
        this.edgesCounter = edgesCounter;
    }

    public int getToId() {
        return toId;
    }

    public void increaseDistancesSum(long distance) {
        this.distancesSum = this.distancesSum + distance;
    }

    public void incrementEdgesCounter() {
        this.edgesCounter += 1;
    }

    /**
     * Counts average distance using integer division.
     * @return average distance rounded to zero.
     */
    public long getAvgDistance(){
        return distancesSum / edgesCounter;
    }
}
