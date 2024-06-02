package org.solutions.grid.datastructures;

import cz.cvut.fel.esw.server.proto.Location;

/**
 * Wrapper class for location protobuf message containing location id
 */
public class LocationRecord {
    private int x;
    private int y;
    /**
     * id of location is in fact an index of order in which locations come to the server.
     */
    private int id;
    public LocationRecord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public LocationRecord(Location location) {
        this(location.getX(), location.getY());
    }

    public int y() {
        return y;
    }

    public int x() {
        return x;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
