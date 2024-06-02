package org.solutions.grid.datastructures;

import com.google.common.primitives.UnsignedInts;
import cz.cvut.fel.esw.server.proto.Location;
import cz.cvut.fel.esw.server.proto.Walk;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Graph where nodes are locations and edges are paths between them.
 */
public class Graph {
    /**
     * Adjacency list in which locations are represented
     * by their ids(assigned as order number of location as it comes to the server).
     */
    private List<List<Edge>> adjList;
    /**
     * List of locations to easily retrieve location by its id.
     */
    private List<LocationRecord> locations;
    /**
     * Approximate number of locations in order to effectively store them in the graph.
     */
    private int expectedLocationsNumber;
    /**
     * Hash grid for faster search of identical locations in graph.
     */
    private HashGrid grid = new HashGrid();

    public Graph(int expectedLocationsNumber) {
        this.expectedLocationsNumber = expectedLocationsNumber;
        this.adjList = new ArrayList<>(expectedLocationsNumber);
        for (int i = 0; i < expectedLocationsNumber; i++){
            adjList.add(new ArrayList<>());
        }
        this.locations = new ArrayList<>(expectedLocationsNumber);
    }

    public List<List<Edge>> getAdjList() {
        return adjList;
    }

    public List<LocationRecord> getLocations() {
        return locations;
    }


    /**
     * Adding walks into graph and grid.
     * @param walks
     */
    public void addNewWalks(List<Walk> walks){
        for (Walk walk : walks) {
            addWalk(walk);
        }
    }

    /**
     * Adding walk into graph and grid.
     * @param walk
     */
    public void addWalk(Walk walk){
        /* Wrapping locations(to add id) and lengths(unsigned long(protobuf) to java long). */
        List<LocationRecord> newLocations = new ArrayList<>();
        for (Location l: walk.getLocationsList()){
            newLocations.add(new LocationRecord(l));
        }
        List<Long> lengths = new ArrayList<>();
        for (Integer length: walk.getLengthsList()){
            lengths.add(UnsignedInts.toLong(length));
        }

        /* Adding edges one by one. */
        LocationRecord existingLocationFrom;
        LocationRecord existingLocationTo = null;
        for (int i = 0; i < newLocations.size() - 1; i++) {
            if (i == 0) {
                existingLocationFrom = this.grid.addLocation(newLocations.get(i));
                if (existingLocationFrom == newLocations.get(i)) { /* inserted given location as new  */
                    existingLocationFrom.setId(this.locations.size());
                    this.locations.add(existingLocationFrom);
                }
            } else {
                existingLocationFrom = existingLocationTo;
            }

            existingLocationTo = this.grid.addLocation(newLocations.get(i + 1));
            if (existingLocationTo == newLocations.get(i + 1)) { /* inserted given location as new  */
                existingLocationTo.setId(this.locations.size());
                this.locations.add(existingLocationTo);
            }

            /* Adding edge. */
            addEdge(existingLocationFrom, existingLocationTo, lengths.get(i));
        }
    }

    public void addEdge(LocationRecord from, LocationRecord to, Long length){
        Edge sameEdge = findEdge(from, to);
        if (sameEdge == null) { /* if no such edge in graph initialize new */
            adjList.get(from.getId()).add(new Edge(to.getId(), length, 1));
        } else { /* otherwise accumulate edges for further computation of average distance */
            sameEdge.incrementEdgesCounter();
            sameEdge.increaseDistancesSum(length);
        }

    }

    /**
     * Find existing edge in the graph.
     * @param origin origin node of the edge.
     * @param destination destination node of the edge.
     * @return existing edge in the graph if such exists and null otherwise.
     */
    @Nullable
    public Edge findEdge(LocationRecord origin, LocationRecord destination) {
        List<Edge> neighbours = adjList.get(origin.getId());
        for (Edge edge : neighbours) {
            if (destination.getId() == edge.getToId()) {
                return edge;
            }
        }
        return null;
    }


    /**
     * Clears whole graph including locations and grid.
     */
    public void reset(){
        locations.clear();
        adjList.clear();
        for (int i = 0; i < expectedLocationsNumber; i++){
            adjList.add(new ArrayList<>());
        }
        grid.clear();
    }

    public HashGrid getGrid() {
        return grid;
    }

    public int getLocationsSize(){
        return locations.size();
    }
}
