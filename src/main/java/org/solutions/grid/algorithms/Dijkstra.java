package org.solutions.grid.algorithms;

import org.solutions.grid.datastructures.Edge;
import org.solutions.grid.datastructures.Graph;
import org.solutions.grid.datastructures.LocationRecord;

import java.util.*;

public class Dijkstra {

    /**
     * Counts distance from the origin location to the destination location.
     * @param graph the graph of locations represented by an adjacency list
     * @param origin the starting node for the algorithm
     * @param destination sum of distances from the origin location to any other node
     * @return sum of distances from the origin location to any other node
     */
    public static long findShortestPathOneToOne(Graph graph, LocationRecord origin, LocationRecord destination){
        return dijkstraAlgorithm(graph, origin, destination)[destination.getId()];
    }

    /**
     * Counts sum of distances from the origin location to any other
     * @param graph the graph of locations represented by an adjacency list
     * @param origin the starting node for the algorithm
     * @return sum of distances from the origin location to any other node
     */
    public static long findShortestPathOneToAll(Graph graph, LocationRecord origin){
        long[] distances = dijkstraAlgorithm(graph, origin, null);
        long sum = 0L;
        for (Long dist: distances){
            if (dist < (Long.MAX_VALUE)) sum += dist;
        }
        return sum;
    }

    /**
     * Implements Dijkstra's algorithm using a priority queue for better performance.
     *
     * @param graph the graph of locations represented by an adjacency list
     * @param origin the starting node for the algorithm
     * @param destination the target node for the algorithm; if {@code null}, calculates distances to all nodes
     * @return an array of distances from the origin to each node if {@code destination} is {@code null},
     *         or an array containing only the distance between the origin and the destination node
     */
    public static long[] dijkstraAlgorithm(Graph graph, LocationRecord origin, LocationRecord destination){
        List<List<Edge>> adj_list = graph.getAdjList();
        long[] dist = new long[graph.getLocationsSize()];
        PriorityQueue<LocationDistPair> pq = new PriorityQueue<>(
                Comparator.comparing(LocationDistPair::getDistance,
                        Long::compareTo));
        boolean[] processed = new boolean[graph.getLocations().size()];

        Arrays.fill(dist, Long.MAX_VALUE);

        dist[origin.getId()] = 0L;
        pq.add(new LocationDistPair(origin.getId(), 0L));

        while (!pq.isEmpty()) {
            LocationDistPair current = pq.poll();
            Integer currentLocationId = current.getLocation();

            if (processed[currentLocationId]) continue;
            processed[currentLocationId] = true;

            long currentDist = current.getDistance();
            if (destination != null && currentLocationId == destination.getId()) {
                return dist;
            }

            if (currentDist > dist[currentLocationId]) {
                continue;
            }

            for (Edge edge : adj_list.get(currentLocationId)) {
                int neighbourId = edge.getToId();
                long newDist = currentDist + edge.getAvgDistance();

                if (!processed[neighbourId] &&
                        newDist < dist[neighbourId]) {
                    dist[neighbourId] = newDist;
                    pq.add(new LocationDistPair(neighbourId, newDist));
                }
            }
        }

        return dist;
    }

}
