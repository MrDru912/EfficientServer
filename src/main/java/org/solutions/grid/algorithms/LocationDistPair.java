package org.solutions.grid.algorithms;

/**
 * Helping class for storing pair distance and location for priority queue in dijkstra algorithm.
 */
public class LocationDistPair {
    private final Integer location;
    private long distance;

    public LocationDistPair(Integer location, long distance) {
        this.location = location;
        this.distance = distance;
    }

    public Integer getLocation() {
        return location;
    }

    public long getDistance() {
        return distance;
    }

}
